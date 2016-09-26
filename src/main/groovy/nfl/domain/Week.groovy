package nfl.domain

import groovy.transform.ToString

import static nfl.common.Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames = true, excludes = 'games')
class Week {

   Integer week
   boolean skip
   TreeSet<Game> games = []
   Map<Name, BigDecimal> spreads = [:]

   Name getOpponentFor(Name name) {
      Game game = games.find { name in [it.home.name, it.away.name] }
      game?.getOpponentFor(name)
   }

}
