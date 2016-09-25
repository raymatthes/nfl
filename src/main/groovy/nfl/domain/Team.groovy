package nfl.domain

import groovy.transform.ToString

import static nfl.common.Constants.*

/**
 * @author Ray Matthes
 */
@ToString(includeNames = true, excludes = 'games')
class Team implements Comparable {

   static final Map<Name, Team> TEAMS = Name.inject([:]) { teams, team ->
      teams[team] = new Team('name': team)
      teams
   }

   Name name
   Map<Integer, Game> games = [:]

   @Override
   int compareTo(Object that) {
      this.name <=> that.name
   }

}
