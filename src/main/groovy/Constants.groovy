import static Constants.Name.*

/**
 * @author Ray Matthes
 */
class Constants {

   static enum Name {
      ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
      MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SD, SEA, SF, STL, TB, TEN, WAS
   }

   static final List<Name> USED = [
         NO,
         TB,
   ]

   static final Map<Name, Team> TEAMS = Name.inject([:]) { teams, team ->
      teams[team] = new Team('name': team)
      teams
   }


   static final Map<Integer, Week> WEEKS = [:]

   static final int LAST_WEEK = 17

}
