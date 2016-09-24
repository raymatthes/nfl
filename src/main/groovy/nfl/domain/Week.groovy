package nfl

import groovy.transform.ToString

import static nfl.Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames = true, excludes = 'games')
class Week {
   static final Map<Integer, Week> WEEKS = [:]

   Integer week
   boolean skip
   TreeSet<Game> games = []
   Map<Name, BigDecimal> spreads = [:]

   Name getOpponentFor(Name name) {
      Game game = games.find { name in [it.home.name, it.away.name] }
      game?.getOpponentFor(name)
   }

}
