package nfl.common

/**
 * @author Ray Matthes
 */
class Constants {

   static String SURVIVOR_FILE = 'etc/survivor.html'
   static String USED_TEAMS_FILE = '/used-teams.yaml'
   static String DOWNLOAD_URL = 'http://www.survivorgrid.com/'

   static enum Name {
      ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
      MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SD, SF, SEA, TB, TEN, WAS, LA
   }

   static final int FINAL_WEEK = 17

   static final BigDecimal SPIKE = BigDecimal.valueOf(1000L)

   static final int DOUBLE_PICKS_START_WEEK = 3

}
