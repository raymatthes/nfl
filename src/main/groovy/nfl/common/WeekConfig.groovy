package nfl.common

import nfl.domain.Game
import nfl.domain.Team
import nfl.domain.Week
import org.ccil.cowan.tagsoup.Parser

import java.math.RoundingMode

import static nfl.common.Constants.*

/**
 * @author Ray Matthes
 */
class WeekConfig {

   int year
   int weekNumber
   Map<Integer, Week> weeks = [:]
   List<Name> remaining = []
   List<Name> used = []
   Map<Name, Team> teams = [:]

   public WeekConfig init(Boolean download = false) {
      loadTeams()
      loadWeeks(download)
      loadRemaining()
      this
   }

   def loadTeams() {
      teams = Name.inject([:]) { teams, team ->
         teams[team] = new Team(name: team)
         teams
      }
   }

   protected loadRemaining() {
      used = Utils.loadUsed()
      remaining = Name.values()
      remaining.removeAll(used)
      printRemainingCount()
   }

   protected loadWeeks(boolean download) {
      File file = new File(Constants.SURVIVOR_FILE)
      if (download) {
         Utils.download(file)
      }
      parseFile(file)
   }

   protected parseFile(File file) {
      String html = file.text
      def tagsoupParser = new Parser()
      def slurper = new XmlSlurper(tagsoupParser)
      def page = slurper.parseText(html)

      String title = page.head.title.text()
      String pattern =  /(\d+) NFL Survivor Pool Picks Grid: Week (\d+) Help/
      def matcher = (title =~ pattern)
      year = matcher[0][1].toInteger()
      weekNumber = matcher[0][2].toInteger()
      println "This is ${year} week number ${weekNumber}"

      weeks.clear()
      (weekNumber..Constants.FINAL_WEEK).each { int week -> weeks.put(week, new Week(week: week)) }
      def dataTable = page.depthFirst().findAll { it.@class.text() == 'datatable' }
      String[] header = dataTable.thead[0].tr.th[3..-2]*.text()
      def rows = (0..Name.values().size() - 1).collect { dataTable.tbody[0].tr[it].td[3..-2]*.text() }
      parseGames(rows)
   }

   protected printRemainingCount() {
      int remainingCount = remaining.size()
      def permutationCount = (1..remainingCount).inject(1) { sum, value -> sum * (value as BigDecimal) }

      def permutationGenerator = new PermutationGenerator(remaining)
      assert permutationGenerator.total == permutationCount as BigInteger

      String prettyCount = Utils.prettyPrint(permutationCount)

      println "Working with ${remainingCount} remaining teams.  ${prettyCount} permutations."
   }

   protected parseGames(List rows) {
      rows.each { row ->
         Name name = ((row[0] =~ /^([A-Z]+)/)[0][1]) as Name
         Team team = teams[name]

         (weekNumber..Constants.FINAL_WEEK).each { int week ->
            Game game = null
            int offset = week - weekNumber + 1
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
               Team away = teams[opponent as Name]
               if (awayFlag) {
                  home = away
                  homeSpread = awaySpread
                  away = team
                  awaySpread = homeSpread.negate()
               }
               game = new Game(week: week, home: home, away: away, homeSpread: homeSpread, awaySpread: awaySpread)
               weeks[week].games << game
               weeks[week].spreads.put(name, spread)
            }
            team.games.put(week, game)
         }
      }
   }

}
