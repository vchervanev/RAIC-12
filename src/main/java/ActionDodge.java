import model.Shell;
import model.Unit;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 05.11.12
 * Time: 0:11
 * To change this template use File | Settings | File Templates.
 */
public class ActionDodge extends Action {

    Point p1 = new Point(0, 0);
    Point p2 = new Point(0, 0);
    Point p3 = new Point(0, 0);
    Point p4 = new Point(0, 0);
    Point s1 = new Point(0, 0);
    Point s2 = new Point(0, 0);
    private Point alarm = null;

    private void rectangle(Unit in, Point p1, Point p2, Point p3, Point p4) {
        double a = in.getHeight();
        double b = in.getWidth();
        double x0 = in.getX();
        double y0 = in.getY();
        double angle = in.getAngle();
        double x1, y1, x2, y2, x3, y3, x4, y4;
        double d;
        d = Math.sqrt((a * a) + (b * b)) / 2; // диагональ прямоугольника
        double beta = Math.atan(b / a);

        x1 = (x0 - d * Math.cos(angle + PI - beta)); // находим точку x1 делением диагонали
        y1 = (y0 - d * Math.sin(angle + PI - beta)); // находим точку y1 делением диагонали

        x3 = (x0 + d * Math.cos(angle - beta)); // находим точку x3 добавив к x1 диагональ
        y3 = (y0 + d * Math.sin(angle - beta)); // находим точку y3 добавив к y1 диагональ

        x2 = (x0 - d * Math.cos(angle + beta));
        y2 = (y0 - d * Math.sin(angle + beta));

        x4 = (x0 - d * Math.cos(angle - PI + beta));
        y4 = (y0 - d * Math.sin(angle - PI + beta));

        p1.setXY(x1, y1);
        p2.setXY(x2, y2);
        p3.setXY(x3, y3);
        p4.setXY(x4, y4);

    }

    private Point chekTrase(Point a1, Point a2, Point b1, Point b2) {
        final double eps = 0.000001;

        double d, da, db, ta, tb;

        d = (a1.x - a2.x) * (b2.y - b1.y) - (a1.y - a2.y) * (b2.x - b1.x);
        da = (a1.x - b1.x) * (b2.y - b1.y) - (a1.y - b1.y) * (b2.x - b1.x);
        db = (a1.x - a2.x) * (a1.y - b1.y) - (a1.y - a2.y) * (a1.x - b1.x);

        if (abs(d) < eps) return null;
        ta = da / d;
        tb = db / d;
        if ((0 <= ta) && (ta <= 1) && (0 <= tb) && (tb <= 1))
            return new Point(a1.x + ta * (a2.x - a1.x), a1.y + ta * (a2.y - a1.y));
        return null;
    }


    @Override
    public void estimate() {
        alarm = null;
        Shell[] shels = env.world.getShells();
        rectangle(env.self, p1, p2, p3, p4);
        for (Shell shell : shels) {
            String name = shell.getPlayerName();
            if (!env.myName.equals(name)) {
                //анализ полёта
                s1.setXY(shell.getX(), shell.getY());
                s2.setXY(shell.getX() + shell.getSpeedX() * 100, shell.getY() + shell.getSpeedY() * 100);

                Point a1 = chekTrase(s1, s2, p1, p2);
                if (a1 != null) alarm = a1;
                a1 = chekTrase(s1, s2, p3, p4);
                if (a1 != null) alarm = a1;
            }
        }
        if (alarm != null) {
            variant = Variant.dodge;
            System.out.println("Alarm!");
        } else
            variant = Variant.none;
    }

    @Override
    public void perform() {
        env.directMoveTo(alarm.x + env.dimention, alarm.y + env.dimention);
    }
}
