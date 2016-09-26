package nfl.domain

import groovy.transform.ToString

import static nfl.common.Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames = true, excludes = 'games')
class Team implements Comparable<Team> {

   Name name
   Map<Integer, Game> games = [:]

   @Override
   int compareTo(Team that) {
      this.name <=> that.name
   }

}
