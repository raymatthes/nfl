package nfl

import groovy.transform.ToString

/**
 * @author Ray Matthes
 */
@ToString(includeNames=true)
class Game implements Comparable {

   Integer week
   Team home
   Team away
   BigDecimal homeSpread
   BigDecimal awaySpread


   @Override
   int compareTo(Object that) {
      this.week <=> that.week ?:
            this.home <=> that.home ?:
                  this.away <=> that.away
   }

   @Override
   boolean equals(o) {
      if (this.is(o)) return true;
      if (!(o instanceof Game)) return false;

      Game that = (Game) o;

      if (this.week != that.week) return false;
      if (this.home != that.home) return false;
      if (this.away != that.away) return false;

      return true;
   }

   @Override
   int hashCode() {
      int result;
      result = week.hashCode();
      result = 31 * result + (home?.hashCode() ?: 0)
      result = 31 * result + (away?.hashCode() ?: 0)
      return result;
   }

}
