package nfl

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.ccil.cowan.tagsoup.Parser

import java.math.RoundingMode

import static nfl.Constants.*

/**
 * @author Ray Matthes
 */
class Poc {

   public static void main(String[] args) {
      Date start = new Date()
      println "Start: ${start}"

      def tagsoupParser = new Parser()
      def slurper = new XmlSlurper(tagsoupParser)

      File file = new File('/Users/rmatthes/nfl/survivor.html')

      //download(file)

      String html = file.text

      def page = slurper.parseText(html)
      String title = page.head.title.text()
      int currentWeek = (title =~ /NFL Survivor Pool Picks Grid: nfl.Week (\d+) Help/)[0][1].toInteger()

      println "This is week number ${currentWeek}"

      (currentWeek..Constants.LAST_WEEK).each { int week -> Week.WEEKS.put(week, new Week(week: week)) }

      def dataTable = page.depthFirst().findAll { it.@class.text() == 'datatable' }

      String[] header = dataTable.thead[0].tr.th[3..-2]*.text()
      def rows = (0..31).collect { dataTable.tbody[0].tr[it].td[3..-2]*.text() }

      //println header
      //rows.each { println it }

      parseGames(rows, currentWeek)

      List<Name> remaining = Name.values()
      remaining.removeAll(Constants.USED)

      int remainingCount = remaining.size()
      def permutationCount = (1..remainingCount).inject(1) { sum, value -> sum * (value as BigDecimal) }

      def permutationGenerator = new PermutationGenerator(remaining)
      assert permutationGenerator.total == permutationCount as BigInteger

      String prettyCount = prettyPrint(permutationCount)

      println "Working with ${remainingCount} remaining teams.  ${prettyCount} permutations."

      BigDecimal spike = BigDecimal.valueOf(1000L)
      def best = new Pick(iteration: 0, teams: [], total: spike)

      //long loopLimit = 100000000L
      long loopLimit = 6L

      long loopIndex = 0
      permutationGenerator.find {
         loopIndex++

         false
      }

      TimeDuration td = TimeCategory.minus(new Date(), start)
      println ""
      println "Elapsed: ${td}"
      println "End: ${new Date()}"
   }

   static String prettyPrint(BigDecimal value) {
      def string = sprintf('%.0f', value)
      String regex = /(\d)(?=(\d{3})+$)/
      string.replaceAll(regex, /$1,/)
   }

   private static parseGames(List rows, int currentWeek) {
      rows.each { row ->
         Name name = ((row[0] =~ /^([A-Z]+)/)[0][1]) as Name
         Team team = Team.TEAMS[name]

         (currentWeek..Constants.LAST_WEEK).each { int week ->
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

   static def download(File file) {
      URL url = new URL('http://www.survivorgrid.com/')
      String html = url.getText()
      file.write(html)
   }
}
