package nfl.domain

import groovy.transform.ToString
import nfl.common.Constants
import nfl.common.Utils

import static nfl.common.Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames = true)
class Pick {

   long iteration
   List<Name> teams
   BigDecimal total

   static Pick getSpike() {
      new Pick(iteration: 0, teams: [], total: Constants.SPIKE)
   }

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
