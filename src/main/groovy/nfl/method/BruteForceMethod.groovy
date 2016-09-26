package nfl.method

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import nfl.common.Utils
import nfl.common.WeekConfig
import nfl.domain.Game
import nfl.domain.Pick

import static nfl.common.Constants.FINAL_WEEK
import static nfl.common.Constants.Name

/**
 * a less practical method that examines every pick permutation
 *
 * this becomes feasible to run at week 12 with (12) remaining teams.
 * 479,001,600 permutations -- maybe 20 minutes or so
 *
 * @author Ray Matthes
 */
class BruteForceMethod {

   WeekConfig weekConfig

   public static void main(String[] args) {
      Date start = new Date()
      println "Start: ${start}"

      boolean download = false
      WeekConfig context = new WeekConfig().init(download)
      new BruteForceMethod(weekConfig: context).process()

      TimeDuration td = TimeCategory.minus(new Date(), start)
      println ""
      println "Elapsed: ${td}"
      println "End: ${new Date()}"
   }

   def process() {
      force().prettyPrint('Brute Force')
   }

   Pick force() {
      Date start = new Date()

      int remainingCount = weekConfig.remaining.size()
      def permutationCount = (1..remainingCount).inject(1) { sum, value -> sum * (value as BigDecimal) }

      PermutationGenerator<Name> permutationGenerator = new PermutationGenerator<Name>(weekConfig.remaining)
      assert permutationGenerator.total == permutationCount as BigInteger

      BigDecimal spike = BigDecimal.valueOf(1000L)
      Pick best = Pick.getSpike(weekConfig)

      //long loopLimit = 100000000L
      long loopLimit = 6L

      long loopIndex = 0
      permutationGenerator.find {
         loopIndex++

         BigDecimal total = (weekConfig.weekNumber..FINAL_WEEK).inject(BigDecimal.ZERO) { sum, weekNumber ->
            int offset = (weekNumber - weekConfig.weekNumber) * 2
            Name team1 = it[offset]
            Name team2 = it[offset + 1]
            BigDecimal spread1 = weekConfig.weeks.get(weekNumber).spreads.get(team1)
            BigDecimal spread2 = weekConfig.weeks.get(weekNumber).spreads.get(team2)

            if (spread1 == null || spread2 == null || isMatchup(weekNumber as int, team1, team2)) {
               // prevent choosing this permutation by spiking it
               sum += spike
            } else {
               sum += spread1 + spread2
            }

            sum
         }

         if (total < best.total) {
            best = new Pick(iteration: loopIndex, teams: it, total: total, weekConfig: weekConfig)
         }

         if (loopIndex % 1000000L == 0L) {
            TimeDuration td = TimeCategory.minus(new Date(), start)
            float percent = ((loopIndex as float) / permutationCount as float) * (100 as float)
            String percentString = sprintf('%.10f', percent)
            println "${percentString}% Iteration: ${Utils.prettyPrint(loopIndex as BigDecimal)}. Elapsed: ${td} Best pick: ${best}"
         }

         //loopIndex >= loopLimit
         false
      }

      best
   }

   protected boolean isMatchup(int week, Name team1, Name team2) {
      Game game = weekConfig.teams[team1].games[week]
      (game.home.name == team1 && game.away.name == team2) ||
            (game.away.name == team1 && game.home.name == team2)
   }

}
