import model.Unit;

import static java.lang.Math.*;


public class Point {
    public double x;
    public double y;

    public static double getDistance2(Point p1, Point p2) {
        return pow(p1.x - p2.x, 2) + pow(p1.y - p2.y, 2);
    }
    public Point(Unit unit) {
        this(unit.getX(), unit.getY());
    }

    public Point(Point point) {
        this(point.x, point.y);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Point))
            return false;
        Point p = (Point)object;
        return equals(p);
    }

    public boolean equals(Point in) {
        return (in.x == x && in.y == y) ||
                ((Double.isNaN(in.x) || Double.isNaN(in.y)) && (Double.isNaN(x) || Double.isNaN(y)));
    }

//    public boolean equals(double x, double y) {
//        return this.x == x && this.y ==y;
//    }
//
//    public void setXY(double x, double y) {
//        this.x = x;
//        this.y = y;
//    }

    /** Not a Point */
    public static final Point NaP = new Point(Double.NaN, Double.NaN);

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    public void add(Point p) {
        this.x += p.x;
        this.y += p.y;
    }

//    public void sub(Point p) {
//        this.x -= p.x;
//        this.y -= p.y;
//    }

    public static Point add(Point p1, Point... points) {
        final Point result = new Point(p1);
        for (Point p : points) {
            result.add(p);
        }
        return result;
    }

//    public static Point sub(Point p1, Point... points) {
//        final Point result = new Point(p1);
//        for (Point p : points) {
//            result.sub(p);
//        }
//        return result;
//    }

    public void rotate(Point c, double r, double angle) {
        double gamma = c.x != x ? -atan((c.y - y)/(c.x - x)) : -PI/2;
        if (signum(c.x - x) > 0){
            gamma += PI;
        }
//        System.out.println("g = " + 180*gamma/PI);
//        System.out.println("a = " + 180*angle/PI);
        angle += gamma;
//        System.out.println("* = " + 180*angle/PI);

        x = c.x + r*cos(angle);
        y = c.y + r*sin(angle);
    }
}
