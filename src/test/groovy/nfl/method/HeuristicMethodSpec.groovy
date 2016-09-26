package nfl.method

import nfl.common.WeekConfig
import nfl.domain.Pick
import spock.lang.Specification

import nfl.common.Constants.Name

/**
 * @author Ray Matthes
 */
class HeuristicMethodSpec extends Specification {

   HeuristicMethod sut

   void setup() {
      sut = new HeuristicMethod()

      WeekConfig weekConfig = new WeekConfig()
      weekConfig.loadTeams()
      File html = new File(this.class.getResource('/2016/week01-survivor.html').file)
      weekConfig.parseFile(html)
      weekConfig.remaining = Name.values()

      sut.weekConfig = weekConfig
   }

   void cleanup() {
   }

   def "test forward week 01"() {
      when:
      Pick pick = sut.forward()

      then:
      pick
      new BigDecimal('-155.5') == pick.total
   }

   def "test reverse week 01"() {
      when:
      Pick pick = sut.reverse()

      then:
      pick
      new BigDecimal('-142.5') == pick.total
   }

}
