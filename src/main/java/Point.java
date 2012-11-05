
/**
 * User: Alexandr
 * Date: 04.11.12
 * Time: 17:46
 */
public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Point in) {
        return in.x == x && in.y ==y;
    }

    public boolean equals(double x, double y) {
        return this.x == x && this.y ==y;
    }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
