import model.Shell;
import model.Tank;
import model.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.Math.min;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 05.11.12
 * Time: 0:11
 * To change this template use File | Settings | File Templates.
 */
public class ActionDodge extends Action {

    final static double SHELL_HEIGHT = 7.5;

    Point p1 = new Point(0, 0);
    Point p2 = new Point(0, 0);
    Point p3 = new Point(0, 0);
    Point p4 = new Point(0, 0);
    Point s1 = new Point(0, 0);
    Point s2 = new Point(0, 0);
    private Point safePoint;


    private Point[] findPoint(Point m, Point c, double dist) {
        Point[] p = new Point[2];
        if (m.y == c.y) {
            p[0] = new Point(m.x, m.y - dist);
            p[1] = new Point(m.x, m.y + dist);
            return p;
        }
        double d = -dist * dist;
        double c1 = (c.x - m.x) / (c.y - m.y);
        double a = 1 + c1 * c1;
        double b = 0;
        double x = (-b + sqrt(b * b - 4 * a * d)) / 2 / a + m.x;
        double y = -(x - m.x) * (c.x - m.x) / (c.y - m.y) + m.y;
        p[0] = new Point(x, y);
        x = (-b - sqrt(b * b - 4 * a * d)) / 2 / a + m.x;
        y = -(x - m.x) * (c.x - m.x) / (c.y - m.y) + m.y;
        p[1] = new Point(x, y);
        return p;
    }

    static private void rectangle(Unit in, Point p1, Point p2, Point p3, Point p4, double delta) {
        double a = in.getHeight() + delta;
        double b = in.getWidth() + delta;
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

    static Point chekTrase(Point a1, Point a2, Point b1, Point b2) {
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

    static boolean chekTrase(Point s1, Point s2, Unit u) {
        Point px1 = new Point(0, 0);
        Point px2 = new Point(0, 0);
        Point px3 = new Point(0, 0);
        Point px4 = new Point(0, 0);
        rectangle(u, px1, px2, px3, px4, SHELL_HEIGHT);
        if (chekTrase(s1, s2, px1, px2) != null) return true;
        return chekTrase(s1, s2, px3, px4) != null;
    }

    static boolean existWall(Point p1, Point p2, long[] exlude) {
        ArrayList<Unit> units = new ArrayList<Unit>();
        units.addAll(Arrays.asList(env.world.getShells()));
        units.addAll(Arrays.asList(env.world.getBonuses()));
        units.addAll(Arrays.asList(env.world.getTanks()));

        for (Unit u : units) {
            boolean test = true;
            for (long id : exlude)
                if (u.getId() != id) {
                    test = false;
                    break;
                }
            if (test) {
                if (chekTrase(p2, p1, u)) return true;
            }
        }
        return false;
    }

    @Override
    public void estimate() {
        rectangle(env.self, p1, p2, p3, p4, SHELL_HEIGHT);
        Point alarm = null;
        Point enemyShel = null;
        Shell[] shels = env.world.getShells();
        for (Shell shell : shels) {
            String name = shell.getPlayerName();
            if (!env.myName.equals(name)) {
                //анализ полёта
                s1.setXY(shell.getX(), shell.getY());
                s2.setXY(shell.getX() + shell.getSpeedX() * 100, shell.getY() + shell.getSpeedY() * 100);

                alarm = chekTrase(s1, s2, p1, p2);
                if (alarm == null)
                    alarm = chekTrase(s1, s2, p3, p4);
                if (alarm != null) {
                    //поиск преград
                    if (existWall(s1, alarm, new long[]{shell.getId(), env.self.getId()})) {
                        alarm = null;
                    } else
                        enemyShel = s1;
                }

            }
        }
        if (alarm != null) {
            variant = Variant.dodge;
            Point[] tst = findPoint(alarm, enemyShel, env.dimention);
            if (env.getDistanceTo(tst[0]) < env.getDistanceTo(tst[1]))
                safePoint = (tst[0]);
            else
                safePoint = (tst[1]);
        } else
            variant = Variant.none;
    }

    @Override
    public void perform() {
        env.directMoveTo(safePoint.x, safePoint.y);
    }
}
