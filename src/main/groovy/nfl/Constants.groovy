package nfl

import static Constants.Name.*

/**
 * @author Ray Matthes
 */
class Constants {

   static String SURVIVOR_FILE = 'etc/survivor.html'
   static String USED_TEAMS_FILE = 'etc/used-teams.yaml'
   static String DOWNLOAD_URL = 'http://www.survivorgrid.com/'

   static enum Name {
      ARI, ATL, BAL, BUF, CAR, CHI, CIN, CLE, DAL, DEN, DET, GB, HOU, IND, JAX, KC,
      MIA, MIN, NE, NO, NYG, NYJ, OAK, PHI, PIT, SD, SEA, SF, STL, TB, TEN, WAS
   }

   static final int FINAL_WEEK = 17

}
