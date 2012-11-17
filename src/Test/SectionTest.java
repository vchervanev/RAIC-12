import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 14.11.12
 * Time: 22:21
 */
public class SectionTest {

    @Test
    public void testSection0() {
        Section s = new Section(new Point(3,0), new Point(3, 3));
        double k = s.getK();
        assert Double.isInfinite(k);

        double b = s.getB();
        assert Double.isInfinite(b);
    }

    @Test
    public void testSection1() {
        Section s = new Section(new Point(0,0), new Point(3, 3));
        double k = s.getK();
        assert k == 1;

        double b = s.getB();
        assert b == 0;
    }

    @Test
    public void testSection2() {
        Section s1 = new Section(new Point(0,0), new Point(3, 3));
        Section s2 = new Section(new Point(0,2), new Point(2, 0));
        final Point intersection = Section.getIntersection(s1, s2);
        assert intersection.equals(new Point(1, 1));
    }

    @Test
    public void testSection3() {
        Section s1 = new Section(new Point(0,0), new Point(3, 3));
        Section s2 = new Section(new Point(2,3), new Point(2, 1));
        final Point intersection = Section.getIntersection(s1, s2);
        assert intersection.equals(new Point(2, 2));
    }

    @Test
    public void testSection4() {
        Section s1 = new Section(new Point(0,0), new Point(3, 3));
        Section s2 = new Section(new Point(3,2), new Point(4, 2));
        final Point intersection = Section.getIntersection(s1, s2);
        assert intersection.equals(Point.NaP);
    }


}
