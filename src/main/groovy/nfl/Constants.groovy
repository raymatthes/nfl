package nfl

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
         TB,
         NO,
         NE, SEA,
   ]


   static final int LAST_WEEK = 17

   /* 2014...
static final List<Name> USED = [
      NYJ,
      WAS,
      NO, ATL,
      HOU, PIT,
      DAL, PHI,
      TEN, DEN,
      BUF, CHI,
      CLE, TB,
      SEA, CIN,
      ARI, GB,
      SD, MIA,
      IND, SF,
      NYG, STL,
      DET, MIN,
      BAL, CAR,
      JAX, OAK,
      KC, NE
]
*/

}
