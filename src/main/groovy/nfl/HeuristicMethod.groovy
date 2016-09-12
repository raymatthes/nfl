package nfl

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.ccil.cowan.tagsoup.Parser

import java.math.RoundingMode

import static nfl.Constants.Name

/**
 * a practical method not guaranteed to be optimal or perfect, but sufficient for the immediate goal
 * of choosing weekly picks
 *
 * @author Ray Matthes
 */
class HeuristicMethod {

   public static void main(String[] args) {
      Date start = new Date()
      println "Start: ${start}"

      boolean download = false
      ingestGames(download)

      List<Name> remaining = loadCurrentState()

//      forward(remaining).prettyPrint('Forward')
      forward2(remaining).prettyPrint('Forward2')
//      reverse(remaining).prettyPrint('Reverse')
      reverse2(remaining).prettyPrint('Reverse2')
//      middleOut(remaining).prettyPrint('Middle Out')
      random(remaining).prettyPrint('Random')

      TimeDuration td = TimeCategory.minus(new Date(), start)
      println ""
      println "Elapsed: ${td}"
      println "End: ${new Date()}"
   }

   static Pick middleOut(List<Name> remaining) {
      forward(remaining)
   }

   static Pick reverse(List<Name> remaining) {
      Pick pick = new Pick(iteration: 0, teams: [], total: 0)
      List<Name> available = remaining.collect()

      Week.WEEKS.iterator().reverse().each { weekEntry ->
         Integer weekNumber = weekEntry.key
         Week week = weekEntry.value
         Map filtered = week.spreads.findAll { it.key in available }
         Iterator sorted = filtered.sort { x, y -> x.value <=> y.value }.iterator()
         (1..Utils.picksForWeek(weekNumber)).each {
            def spread = sorted.next()
            pick.teams.add(0, spread.key)
            pick.total += spread.value
            available.remove(spread.key)
         }
      }
      pick
   }

   static Pick forward(List<Name> remaining) {
      Pick pick = new Pick(iteration: 0, teams: [], total: 0)
      List<Name> available = remaining.collect()
      Week.WEEKS.each { Integer weekNumber, Week week ->
         Map filtered = week.spreads.findAll { it.key in available }
         Iterator sorted = filtered.sort { x, y -> x.value <=> y.value }.iterator()
         (1..Utils.picksForWeek(weekNumber)).each {
            def spread = sorted.next()
            pick.teams << spread.key
            pick.total += spread.value
            available.remove(spread.key)
         }
      }
      pick
   }

   static Pick forward2(List<Name> remaining) {
      def weekRange = (Utils.currentWeekNumber()..Constants.FINAL_WEEK)
      computePick(remaining, weekRange.toArray() as List<Integer>)
   }

   static Pick reverse2(List<Name> remaining) {
      def weekRange = (Constants.FINAL_WEEK..Utils.currentWeekNumber())
      computePick(remaining, weekRange.toArray() as List<Integer>)
   }

   static Pick random(List<Name> remaining) {
      long seed = System.nanoTime()
      Pick best = new Pick(iteration: 0, teams: [], total: Constants.SPIKE)
      List<Integer> weeks = (Utils.currentWeekNumber()..Constants.FINAL_WEEK).toArray() as List<Integer>
      (1.100000).each {
         Collections.shuffle(weeks, new Random(seed));
         Pick candidate = computePick(remaining, weeks)
         best = (candidate.total < best.total) ? candidate : best
      }
      best
   }

   static Pick computePick(List<Name> remaining, List<Integer> weekNumbers) {
      Pick pick = new Pick(iteration: 0, teams: [], total: 0)
      int currentWeek = Utils.currentWeekNumber()

      int count = weekNumbers.inject(0) { sum, item -> sum + Utils.picksForWeek(item) }
      pick.teams[count - 1] = null

      List<Name> available = remaining.collect()
      weekNumbers.each { Integer weekNumber ->
         Week week = Week.WEEKS[weekNumber]
         Map filtered = week.spreads.findAll { it.key in available }
         Iterator sorted = filtered.sort { x, y -> x.value <=> y.value }.iterator()

         int picksForWeek = Utils.picksForWeek(weekNumber)
         (1..picksForWeek).each { pickIndex ->
            def spread = sorted.next()
            int teamsIndex = computeTeamsIndex(currentWeek, weekNumber, picksForWeek, pickIndex)
            pick.teams.set(teamsIndex, spread.key)
            pick.total += spread.value
            available.remove(spread.key)
         }
      }
      pick
   }

   protected static int computeTeamsIndex(int currentWeek, int weekNumber, int picksForWeek, int pickIndex) {
      ((currentWeek..weekNumber).inject(0) { sum, item -> sum + Utils.picksForWeek(item) }) - picksForWeek + pickIndex - 1
   }

   protected static List<Name> loadCurrentState() {
      List used = Utils.loadUsed()
      List<Name> remaining = Name.values()
      remaining.removeAll(used)
      printRemainingCount(remaining)
      remaining
   }

   protected static ingestGames(boolean download) {
      File file = new File(Constants.SURVIVOR_FILE)
      if (download) {
         Utils.download(file)
      }
      String html = file.text
      def tagsoupParser = new Parser()
      def slurper = new XmlSlurper(tagsoupParser)
      def page = slurper.parseText(html)
      String title = page.head.title.text()
      int currentWeek = (title =~ /NFL Survivor Pool Picks Grid: Week (\d+) Help/)[0][1].toInteger()
      println "This is week number ${currentWeek}"
      (currentWeek..Constants.FINAL_WEEK).each { int week -> Week.WEEKS.put(week, new Week(week: week)) }
      def dataTable = page.depthFirst().findAll { it.@class.text() == 'datatable' }
      String[] header = dataTable.thead[0].tr.th[3..-2]*.text()
      def rows = (0..31).collect { dataTable.tbody[0].tr[it].td[3..-2]*.text() }
      parseGames(rows, currentWeek)
   }

   protected static printRemainingCount(List<Name> remaining) {
      int remainingCount = remaining.size()
      def permutationCount = (1..remainingCount).inject(1) { sum, value -> sum * (value as BigDecimal) }

      def permutationGenerator = new PermutationGenerator(remaining)
      assert permutationGenerator.total == permutationCount as BigInteger

      String prettyCount = Utils.prettyPrint(permutationCount)

      println "Working with ${remainingCount} remaining teams.  ${prettyCount} permutations."
   }

   private static parseGames(List rows, int currentWeek) {
      rows.each { row ->
         Name name = ((row[0] =~ /^([A-Z]+)/)[0][1]) as Name
         Team team = Team.TEAMS[name]

         (currentWeek..Constants.FINAL_WEEK).each { int week ->
            Game game = null
            int offset = week - currentWeek + 1
            def empty = [[null, null, null, null]]
            def all, awayFlag, opponent, spreadValue
            if (row[offset] =~ /PK$/) {
               spreadValue = '0.0'
               (all, awayFlag, opponent) = (row[offset] =~ /(@)?([A-Z]+)PK$/)[0]
            } else {
               (all, awayFlag, opponent, spreadValue) = ((row[offset] =~ /(@)?([A-Z]+)([+\-0-9\.]+)/) ?: empty)[0]
            }
            if (opponent) {
               BigDecimal spread = new BigDecimal(spreadValue).setScale(1, RoundingMode.HALF_UP)
               BigDecimal homeSpread = spread
               BigDecimal awaySpread = spread.negate()
               Team home = team
               Team away = Team.TEAMS[opponent as Name]
               if (awayFlag) {
                  home = away
                  homeSpread = awaySpread
                  away = team
                  awaySpread = homeSpread.negate()
               }
               game = new Game(week: week, home: home, away: away, homeSpread: homeSpread, awaySpread: awaySpread)
               Week.WEEKS[week].games << game
               Week.WEEKS[week].spreads.put(name, spread)
            }
            team.games.put(week, game)
         }
      }
   }

}
