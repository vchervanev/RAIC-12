/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 24.11.12
 * Time: 22:39
 * To change this template use File | Settings | File Templates.
 */
public class G {
    public static NetGraphClient ngc = Runner.ngc;

    public static void draw(Point[] points) {
        for(int i=0; i <points.length-1;i++) {
            draw(points[i], points[i+1]);
        }
        draw(points[points.length-1], points[0]);
    }

    public static void draw(Point a, Point b) {
        ngc.drawLine(a.x, a.y, b.x, b.y);
        update();
    }

    public static void draw(Section section) {
        draw(section.p1, section.p2);
    }

    public static void update() {
        ngc.sendCommand("update");
    }

    public static void yellow() {
        ngc.setColor("yellow");
    }
}
