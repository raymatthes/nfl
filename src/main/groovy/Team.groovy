import groovy.transform.ToString

import static Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames = true, excludes = 'games')
class Team implements Comparable {

   Name name
   Map<Integer, Game> games = [:]

   @Override
   int compareTo(Object that) {
      this.name <=> that.name
   }

}
