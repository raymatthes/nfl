package nfl.common

import nfl.domain.Week
import spock.lang.Specification

/**
 * @author Ray Matthes
 */
class WeekConfigSpec extends Specification {

   WeekConfig sut

   void setup() {
      sut = new WeekConfig()
   }

   void cleanup() {
   }

   def "test loadTeams happy"() {
      when:
      sut.loadTeams()

      then:
      32 == sut.teams.size()
   }

   def "test loadWeeks happy"() {
      setup:
      sut.loadTeams()
      File html = new File(this.class.getResource('/2016/week01-survivor.html').file)

      when:
      sut.parseFile(html)

      then:
      1 == sut.weeks[1].week
   }

}
