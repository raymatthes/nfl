package nfl

import groovy.transform.ToString

import static nfl.Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames=true)
class Pick {
   long iteration
   List<Name> teams
   BigDecimal total
}
