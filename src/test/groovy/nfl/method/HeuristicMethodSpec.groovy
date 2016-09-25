package nfl.method

import nfl.common.Constants
import nfl.domain.Pick
import nfl.domain.Week
import spock.lang.Specification

import nfl.common.Constants.Name

/**
 * @author Ray Matthes
 */
class HeuristicMethodSpec extends Specification {

   void setup() {
   }

   void cleanup() {
   }

   def "test parseFile happy"() {
      setup:
      File html = new File(this.class.getResource('/2016/week01-survivor.html').file)
      HeuristicMethod.parseFile(html)

      when:
      def a = 1

      then:
      1 == Week.WEEKS[1].week
   }

   def "test forward week 01"() {
      setup:
      File html = new File(this.class.getResource('/2016/week01-survivor.html').file)
      HeuristicMethod.parseFile(html)
      List<Name> remaining = Name.values()

      when:
      Pick pick = HeuristicMethod.forward(remaining)

      then:
      pick
      new BigDecimal('-155.5') == pick.total
   }

   def "test reverse week 01"() {
      setup:
      File html = new File(this.class.getResource('/2016/week01-survivor.html').file)
      HeuristicMethod.parseFile(html)
      List<Name> remaining = Name.values()

      when:
      Pick pick = HeuristicMethod.reverse(remaining)

      then:
      pick
      new BigDecimal('-142.5') == pick.total
   }

}
