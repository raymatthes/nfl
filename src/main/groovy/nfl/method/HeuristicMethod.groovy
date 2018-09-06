package nfl.method

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import nfl.common.Constants
import nfl.common.Utils
import nfl.common.WeekConfig
import nfl.domain.Pick
import nfl.domain.Week

import static nfl.common.Constants.Name

/**
 * a practical method not guaranteed to be optimal or perfect, but sufficient for the immediate goal
 * of choosing weekly picks
 *
 * @author Ray Matthes
 */
class HeuristicMethod {

   static final long RANDOM_ITERATIONS = 100000L

   WeekConfig weekConfig

   public static void main(String[] args) {
      Date start = new Date()
      println "Start: ${start}"

      boolean download = true
      WeekConfig context = new WeekConfig().init(download)
      new HeuristicMethod(weekConfig: context).process()

      TimeDuration td = TimeCategory.minus(new Date(), start)
      println ""
      println "Elapsed: ${td}"
      println "End: ${new Date()}"
   }

   def process() {
      forward().prettyPrint('Forward')
      reverse().prettyPrint('Reverse')
      //middleOut().prettyPrint('Middle Out')
      random().prettyPrint('Random')
   }

   Pick forward() {
      def weekRange = (weekConfig.weekNumber..Constants.FINAL_WEEK)
      computePick(weekRange.toArray() as List<Integer>, 1L)
   }

   Pick reverse() {
      def weekRange = (Constants.FINAL_WEEK..weekConfig.weekNumber)
      computePick(weekRange.toArray() as List<Integer>, 1L)
   }

   // TODO not implemented yet
   Pick middleOut() {
      forward()
   }

   Pick random() {
      long seed = System.nanoTime()
      Random random = new Random(seed)
      Pick best = Pick.getSpike(weekConfig)
      List<Integer> weeks = (weekConfig.weekNumber..Constants.FINAL_WEEK).toArray() as List<Integer>
      (1..RANDOM_ITERATIONS).each {
         Collections.shuffle(weeks, random);
         Pick candidate = computePick(weeks, it)
         best = (candidate.total < best.total) ? candidate : best
      }
      best
   }

   Pick computePick(List<Integer> weekNumbers, long iteration) {
      Pick pick = new Pick(iteration: iteration, teams: [], total: 0, weekConfig: weekConfig)
      int count = weekNumbers.inject(0) { sum, item -> sum + Utils.picksForWeek(item) }
      pick.teams[count - 1] = null

      List<Name> available = weekConfig.remaining.collect()

      weekNumbers.each { Integer weekNumber ->
         Week week = weekConfig.weeks[weekNumber]
         Map<Name, BigDecimal> filtered = week.spreads.findAll { it.key in available }
         Map<Name, BigDecimal> sortedMap = filtered.sort { x, y ->
            x.value <=> y.value ?:
                  x.key <=> y.key
         }
         Iterator sorted = sortedMap.iterator()

         List<Name> eliminatedThisWeek = []
         int pickCount = Utils.picksForWeek(weekNumber)
         (1..pickCount).each { pickIndex ->
            def spread = sorted.find { !(it.key in eliminatedThisWeek) }

            if (!spread) {
               // available teams have a BYE week so abandon this Pick
               return Pick.getSpike(weekConfig)
            }

            int teamsIndex = computeTeamsIndex(weekNumber, pickIndex)
            Name name = spread.key

            pick.teams.set(teamsIndex, name)
            pick.total += spread.value

            available.remove(name)
            eliminatedThisWeek << name

            Name opponent = week.getOpponentFor(name)
            eliminatedThisWeek << opponent
         }
      }

      pick.teams.any { !it } ? Pick.getSpike(weekConfig) : pick
   }

   protected int computeTeamsIndex(int weekNumber, int pickIndex) {
      int result = (weekConfig.weekNumber..weekNumber).inject(0) { sum, item ->
         sum + Utils.picksForWeek(item as int)
      }
      result - Utils.picksForWeek(weekNumber) + pickIndex - 1
   }

}
