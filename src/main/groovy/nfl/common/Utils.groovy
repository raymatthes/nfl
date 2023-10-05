package nfl.common


import org.yaml.snakeyaml.Yaml

import java.text.DecimalFormat

import static nfl.common.Constants.*

/**
 * @author Ray Matthes
 */
class Utils {

   static String prettyPrint(BigDecimal value) {
      new DecimalFormat(',###').format(value)
   }

   static List loadUsed() {
      String text = this.getResource(USED_TEAMS_FILE).text
      List<String> list = new Yaml().load(text) as List
      list.collect { Name.valueOf(it) }
   }

   static download(File file) {
      URL url = new URL(DOWNLOAD_URL)
      String html = url.getText()
      file.write(html)
   }

   static int picksForWeek(int week) {
      (week < DOUBLE_PICKS_START_WEEK) ? 1 : 2
   }

   static boolean isSameDivision(Name team1, Name team2) {
      Divisions[team1] == Divisions[team2]
   }

}
