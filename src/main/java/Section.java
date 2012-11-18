import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 14.11.12
 * Time: 22:02
 * To change this template use File | Settings | File Templates.
 */
public class Section {
    public final Point p1;
    public final Point p2;
    public Section(Point a, Point b) {
        this.p1 = a; // a.x <= b.x ? a : b;
        this.p2 = b; //a.x <= b.x ? b : a;

    }
    private double k = Double.MAX_VALUE;
    public double getK(){
        if (k != Double.MAX_VALUE) {
            return k;
        }
        k = (p2.y - p1.y)/(p2.x - p1.x);

        return k;
    }

    private double b = Double.MAX_VALUE;

    public double getB() {
        if (b != Double.MAX_VALUE) {
            return  b;
        }
        b = p1.y - getK() * p1.x;
        return  b;
    }

    public static Point getIntersection(Section s1, Section s2) {
        return getIntersection(s1, s2, 0);
    }


    public static Point getIntersection(Section s1, Section s2, double epsilon) {
        if (s1.getK() == s2.getK()) {
            return Point.NaP;
        }
        double x;
        double y;

        if (Double.isInfinite(s1.getK()) || abs(s1.getK()) > 100000) {
            x = s1.p1.x;
            y = s2.getY(x);
        } else if (Double.isInfinite(s2.getK())  || abs(s2.getK()) > 100000){
            x = s2.p1.x;
            y = s1.getY(x);
        } else {
            x = ( s2.getB() - s1.getB() ) / ( s1.getK() -  s2.getK() );
            y = s1.getY(x);
        }

        Point intersect = new Point(x, y);
        //x1 ≤ x2; x3 ≤ x4;
//        должны выполняться условия:
        if (s1.isInside(intersect, epsilon) && s2.isInside(intersect, epsilon))
            return intersect;
        else
            return Point.NaP;
    }

    public double getY(double x) {
        return this.getK() * x + this.getB();
    }

//    public double getX(double y) {
//        return (y - this.getB())/this.getK();
//    }

    public boolean isInside(Point p) {
        return isInside(p, 0);
    }

    public boolean isInside(Point p, double epsilon) {
        double minX = min(p1.x, p2.x);
        double maxX = max(p1.x, p2.x);
        double minY = min(p1.y, p2.y);
        double maxY = max(p1.y, p2.y);
        return minX - epsilon <= p.x && p.x <= maxX + epsilon && minY - epsilon <= p.y && p.y <= maxY + epsilon;
    }

    @Override
    public String toString() {
        return p1 + " - " + p2;
    }
}
