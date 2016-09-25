package nfl.common

import nfl.domain.Week
import org.yaml.snakeyaml.Yaml

import static Constants.*

/**
 * @author Ray Matthes
 */
class Utils {

   static String prettyPrint(BigDecimal value) {
      def string = sprintf('%.0f', value)
      String regex = /(\d)(?=(\d{3})+$)/
      string.replaceAll(regex, /$1,/)
   }

   static List loadUsed() {
      String text = this.getResource(USED_TEAMS_FILE).text
      List list = new Yaml().load(text) as List
      list.collect { Name.valueOf(it) }
   }

   static download(File file) {
      URL url = new URL(DOWNLOAD_URL)
      String html = url.getText()
      file.write(html)
   }

   static int picksForWeek(int week) {
      (week < Constants.DOUBLE_PICKS_START_WEEK) ? 1 : 2
   }

   static int currentWeekNumber() {
      Week.WEEKS.iterator().next().key
   }

}
