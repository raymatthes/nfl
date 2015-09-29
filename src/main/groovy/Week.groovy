import groovy.transform.ToString

import static Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames=true, excludes='games')
class Week {
   Integer week
   boolean skip
   TreeSet<Game> games = []
   Map<Name, BigDecimal> spreads = [:]
}
