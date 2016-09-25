package nfl.method

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import nfl.common.Constants
import nfl.common.Utils
import nfl.domain.Game
import nfl.domain.Pick
import nfl.domain.Team
import nfl.domain.Week
import org.ccil.cowan.tagsoup.Parser

import java.math.RoundingMode

import static Constants.Name

/**
 * a practical method not guaranteed to be optimal or perfect, but sufficient for the immediate goal
 * of choosing weekly picks
 *
 * @author Ray Matthes
 */
class HeuristicMethod {

   static final long RANDOM_ITERATIONS = 100000L

   public static void main(String[] args) {
      Date start = new Date()
      println "Start: ${start}"

      boolean download = false
      ingestGames(download)

      List<Name> remaining = loadCurrentState()

      // TBD
//      forward(remaining).prettyPrint('Forward')
//      reverse(remaining).prettyPrint('Reverse')
//      middleOut(remaining).prettyPrint('Middle Out')

      forward2(remaining).prettyPrint('Forward2')
      reverse2(remaining).prettyPrint('Reverse2')
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
      computePick(remaining, weekRange.toArray() as List<Integer>, 1L)
   }

   static Pick reverse2(List<Name> remaining) {
      def weekRange = (Constants.FINAL_WEEK..Utils.currentWeekNumber())
      computePick(remaining, weekRange.toArray() as List<Integer>, 1L)
   }

   static Pick random(List<Name> remaining) {
      long seed = System.nanoTime()
      Random random = new Random(seed)
      Pick best = Pick.getSpike()
      List<Integer> weeks = (Utils.currentWeekNumber()..Constants.FINAL_WEEK).toArray() as List<Integer>
      (1..RANDOM_ITERATIONS).each {
         Collections.shuffle(weeks, random);
         Pick candidate = computePick(remaining, weeks, it)
         best = (candidate.total < best.total) ? candidate : best
      }
      best
   }

   static Pick computePick(List<Name> remaining, List<Integer> weekNumbers, long iteration) {
      Pick pick = new Pick(iteration: iteration, teams: [], total: 0)
      int currentWeek = Utils.currentWeekNumber()
      int count = weekNumbers.inject(0) { sum, item -> sum + Utils.picksForWeek(item) }
      pick.teams[count - 1] = null

      List<Name> available = remaining.collect()

      weekNumbers.each { Integer weekNumber ->
         Week week = Week.WEEKS[weekNumber]
         Map filtered = week.spreads.findAll { it.key in available }
         Map sortedMap = filtered.sort { x, y -> x.value <=> y.value }
         Iterator sorted = sortedMap.iterator()

         List<Name> eliminatedThisWeek = []
         int pickCount = Utils.picksForWeek(weekNumber)
         (1..pickCount).each { pickIndex ->
            def spread = sorted.find { !(it.key in eliminatedThisWeek) }

            if (!spread) {
               // available teams have a BYE week so abandon this Pick
               return Pick.getSpike()
            }

            int teamsIndex = computeTeamsIndex(currentWeek, weekNumber, pickCount, pickIndex)
            Name name = spread.key

            pick.teams.set(teamsIndex, name)
            pick.total += spread.value

            available.remove(name)
            eliminatedThisWeek << name

            Name opponent = week.getOpponentFor(name)
            eliminatedThisWeek << opponent
         }
      }

      pick.teams.any { !it } ? Pick.getSpike() : pick
   }

   protected static int computeTeamsIndex(int currentWeek, int weekNumber, int pickCount, int pickIndex) {
      ((currentWeek..weekNumber).inject(0) { sum, item -> sum + Utils.picksForWeek(item) }) - pickCount + pickIndex - 1
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