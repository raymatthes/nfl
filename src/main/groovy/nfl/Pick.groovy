package nfl

import groovy.transform.ToString

import static nfl.Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames = true)
class Pick {
   long iteration
   List<Name> teams
   BigDecimal total

   def prettyPrint(String title) {
      println "Pick strategy: ${title}"
      println this
      int currentWeek = Utils.currentWeekNumber()
      Iterator teamsIterator = teams.iterator()
      (currentWeek..Constants.FINAL_WEEK).each { int weekNumber ->
         print "${String.format('%02d', weekNumber)} "
         (1..Utils.picksForWeek(weekNumber)).each {
            Name name = teamsIterator.next() as Name
            BigDecimal spread = Week.WEEKS[weekNumber].spreads[name]
            print "${name}(${spread}) "
         }
         println ''
      }
      println ''
   }
}
