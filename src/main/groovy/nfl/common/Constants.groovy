package nfl.common

/**
 * @author Ray Matthes
 */
class Constants {

   static String SURVIVOR_FILE = 'var/survivor.html'
   static String USED_TEAMS_FILE = '/used-teams.yaml'
   static String DOWNLOAD_URL = 'http://www.survivorgrid.com/'

   static enum Name {
      ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
      LAC, LAR, MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SEA, SF, TB, TEN, WAS
   }

   static enum Name_2016 {
      ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
      LA, MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SD, SEA, SF, TB, TEN, WAS
   }

   static enum Name_2014_2015 {
      ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
      MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SD, SEA, SF, STL, TB, TEN, WAS
   }

   static final int FINAL_WEEK = 17

   static final BigDecimal SPIKE = BigDecimal.valueOf(1000L)

   static final int DOUBLE_PICKS_START_WEEK = 3

}
