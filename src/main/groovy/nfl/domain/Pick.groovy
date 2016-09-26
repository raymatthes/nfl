package nfl.domain

import groovy.transform.ToString
import nfl.common.Constants
import nfl.common.Utils
import nfl.common.WeekConfig

import static nfl.common.Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames = true, excludes = ['weekConfig'])
class Pick {

   long iteration
   List<Name> teams
   BigDecimal total
   WeekConfig weekConfig

   static Pick getSpike(WeekConfig config) {
      new Pick(iteration: 0, teams: [], total: Constants.SPIKE, weekConfig: config)
   }

   def prettyPrint(String title) {
      println "Pick strategy: ${title}"
      println this
      int currentWeek = weekConfig.weekNumber
      Iterator teamsIterator = teams.iterator()
      (currentWeek..Constants.FINAL_WEEK).each { int weekNumber ->
         print "${String.format('%02d', weekNumber)} "
         (1..Utils.picksForWeek(weekNumber)).each {
            Name name = teamsIterator.next() as Name
            BigDecimal spread = weekConfig.weeks[weekNumber].spreads[name]
            print "${name}(${spread}) "
         }
         println ''
      }
      println ''
   }
}
