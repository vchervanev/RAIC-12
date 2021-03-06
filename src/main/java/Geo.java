import model.Unit;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 04.11.12
 * Time: 22:57
 * Хелпер геометрии
 */
public class Geo {
//    public static boolean hitTest(Unit unit1, Unit unit2, HitTestMode hiHetTestMode) {
//        return hitTest(unit1, unit2,
//                unit1.getX(), unit1.getY(),
//                unit2.getX(), unit2.getY(),
//                unit1.getAngle(), unit2.getAngle(),
//                hiHetTestMode);
//    }

//    public static boolean hitTest(Unit unit1, Unit unit2, double x1, double y1, double x2, double y2, double a1, double a2, HitTestMode hitHetTestMode) {
//        double r1 = hitHetTestMode == HitTestMode.minimum ? min(unit1.getHeight(), unit1.getWidth()) : max(unit1.getHeight(), unit1.getWidth());
//        double r2 = hitHetTestMode == HitTestMode.minimum ? min(unit2.getHeight(), unit2.getWidth()) : max(unit2.getHeight(), unit2.getWidth());
//        return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) <= r1*r1 + r2*r2;
//    }

    public enum HitTestMode { minimum, maximum }

    public static double getDistancePow2(Unit unit1, Unit unit2) {
        return pow(unit1.getX() - unit2.getX(), 2) + pow(unit1.getY() - unit2.getY(), 2);
    }
    public static double getDistancePow2(Unit unit1, double x2, double  y2) {
        return pow(unit1.getX() - x2, 2) + pow(unit1.getY() - y2, 2);
    }

    public static boolean isObtuse(Point p1, Point p2, Point p3) {
        double a = Point.getDistance2(p1, p2);
        double b = Point.getDistance2(p1, p3);
        double c = Point.getDistance2(p2, p3);
        if (a > b && a > c) {
            return a > b + c;
        } else if (b > c && b > a) {
            return b > a + c;
        } else {
            return  c > a + b;
        }
    }

    public static boolean isInside(Point p, Point[] points) {
        assert points.length == 4;
        return isInside(p, points[0], points[1], points[2]) || isInside(p, points[2], points[3], points[0]);
    }

    public static boolean isInside(Point p, Point p1, Point p2, Point p3) {
        double s1 = x_square(p1, p2, p3);
        double s2 = x_square(p1, p2, p) + x_square(p1, p3, p) + x_square(p2, p3, p);
        return s1 >= s2;
    }


    // 2 площади треугольника
    public static double x_square(Point p1, Point p2, Point p3)
    {
        //Math.Abs(x2*y3-x3*y2-x1*y3+x3*y1+x1*y2-x2*y1);
        return Math.abs(p2.x*p3.y - p3.x*p2.y -p1.x*p3.y + p3.x*p1.y + p1.x*p2.y - p2.x*p1.y);
    }

}
