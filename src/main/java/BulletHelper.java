import model.*;

import static java.lang.Math.*;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 04.11.12
 * Time: 22:54
 * Утилитные классы рассчета баллистики
 */
public class BulletHelper {
    static Env env = MyStrategy.env;

    public static double getHitRadius(Unit unit, Geo.HitTestMode hitTestMode) {
        if (hitTestMode == Geo.HitTestMode.minimum) {
            return pow(min(unit.getWidth(), unit.getHeight())/2.0, 2);
        } else {
            return pow(unit.getHeight()/2.0, 2) + pow(unit.getWidth()/2.0, 2);
        }
    }

    // реальный радиус поражения
    public static double getHitRadius(Unit attacker, Unit target) {
        return getAimInfo(attacker, target).radius;
    }

    public static AimInfo getAimInfo(Unit attacker, Unit target) {
        Point[] points = getUnitPoints(target);
        AimInfo aimInfo = new AimInfo();
        aimInfo.minAngle = 10;
        aimInfo.maxAngle = -10;

        for(Point point : points) {
            double angle = attacker.getAngleTo(point.x, point.y);
            if (angle > aimInfo.maxAngle)
                aimInfo.maxAngle = angle;
            if (angle < aimInfo.minAngle)
                aimInfo.minAngle = angle;
        }
        double viewAngle = (aimInfo.maxAngle - aimInfo.minAngle)/2.0;
        aimInfo.radius = attacker.getDistanceTo(target)*sin(viewAngle);
        return aimInfo;
    }

//    public static int checkHit(Shell shell, Tank tank, Geo.HitTestMode hitTestMode) {
//        int ticks = 0;
//        double distance = Geo.getDistancePow2(shell, tank);
//        double newDistance = distance;
//
//        double x1 = shell.getX();
//        double y1 = shell.getY();
//
//        double x2 = tank.getX();
//        double y2 = tank.getY();
//
//        double vx1 = shell.getSpeedX()/2.0;
//        double vy1 = shell.getSpeedY()/2.0;
//
//        double a1 = 1 - (shell.getType() == ShellType.REGULAR ? 1.0/99 : 1.0/199.0);
//        double a2 = 1;
//
//        double hitRadius = getHitRadius(tank, hitTestMode);
//        double hitRadiusShell = getHitRadius(shell, hitTestMode);
//
//        // в минимальном режиме максимальные помехи (проверка на попадание)
//        // в максимальном режиме минимальные помехи (проверка на защиту)
//        Geo.HitTestMode altMode = hitTestMode == Geo.HitTestMode.minimum ? Geo.HitTestMode.maximum : Geo.HitTestMode.minimum;
//        double hitRadiusShellAlt = getHitRadius(shell, altMode);
//
//        do {
//            distance = newDistance;
//
//            x1 += vx1;
//            vx1 *= a1;
//            y1 += vy1;
//            vy1 *= a1;
//
//            x2 += tank.getSpeedX()/2.0;
//            y2 += tank.getSpeedY()/2.0;
//
//            newDistance = pow(x1 - x2, 2) + pow(y1 - y2, 2);
//            for(Bonus bonus : MyStrategy.env.world.getBonuses()) {
//                double aHitRadius = getHitRadius(bonus, altMode);
//                if (altMode == Geo.HitTestMode.maximum) {
//                    aHitRadius += 5;
//                }
//
//                if (Geo.getDistancePow2(bonus, x1, y1) < aHitRadius + hitRadiusShellAlt) {
//                    //System.out.print("bonus stops bullet\n");
//                    return -1;
//                }
//            }
//
//            for(Tank aTank :  MyStrategy.env.world.getTanks()) {
//                double aHitRadius = getHitRadius(aTank, altMode);
//
//                if (    aTank.getId() != tank.getId() &&
////                        !aTank.getPlayerName().equals(tank.getPlayerName())
////                        && aTank.getCrewHealth() != 0 && aTank.getHullDurability() != 0 &&
//                        Geo.getDistancePow2(aTank, x1, y1) < aHitRadius + hitRadiusShellAlt)
//                    return -1;
//            }
//
//            if (newDistance < hitRadius + hitRadiusShell)
//                return ticks/2;
//            ticks++;
//        } while (newDistance < distance);
//        return -1;
//    }
    static long newId = 99999;

    /** Метод создает пулю, словно ее выстрелил танк*/
    public static Shell simulateShell(Tank tank, ShellType shellType) {
        return simulateShell(tank, shellType, tank.getTurretRelativeAngle() + tank.getAngle());
    }

    public static Shell simulateShell(Tank tank, ShellType shellType, double angle) {
        return simulateShell(tank, shellType, tank.getX(), tank.getY(), angle);
    }

    public static Shell simulateShell(Tank tank, ShellType shellType, double x, double y, double angle) {
        double speed = shellType == ShellType.REGULAR ? 16.7 : 13.3;
        double length = tank.getVirtualGunLength();
        return  new Shell(newId++, "", 5, 5, x + length*cos(angle), y + length*sin(angle),
                speed*cos(angle), speed*sin(angle), angle, 0, shellType);
    }

    public static HitTestResult hitTest(Shell shell, Unit unit, boolean strong){
        int ticks = getTickCountToHit(shell, unit);
        double dx = 0.8 * ticks * unit.getSpeedX();
        double dy = 0.8 * ticks * unit.getSpeedY();

        return hitTest(shell, unit, unit.getX() + dx, unit.getY() + dy, unit.getAngle(), strong);
    }

    public static boolean hitTestTank(Tank tank, Section trace, double hitRadius) {
        Point[] points = getUnitPoints(tank, tank.getX(), tank.getY(), tank.getAngle(), hitRadius);
        Section[] sections = getSections(points);
        Point hitPoint = getHitPoint(trace, sections).hitPoint;
        return !hitPoint.equals(Point.NaP);
    }

    public static boolean hitTestBonus(Bonus bonus, Section trace, double hitRadius) {
        Point[] points = getSimpleUnitPoints(bonus, hitRadius);
        Section[] sections = getSections(points);
        Point hitPoint = getHitPoint(trace, sections).hitPoint;
        return !hitPoint.equals(Point.NaP);
    }
    public static Point[] getSimpleUnitPoints(Unit unit, double hitRadius) {
        Point p1 = new Point(unit.getX() + unit.getWidth()/2 + hitRadius, unit.getY() - unit.getHeight()/2 - hitRadius);
        Point p2 = new Point(unit.getX() + unit.getWidth()/2 + hitRadius, unit.getY() + unit.getHeight()/2 + hitRadius);
        Point p3 = new Point(unit.getX() - unit.getWidth()/2 - hitRadius, unit.getY() + unit.getHeight()/2 + hitRadius);
        Point p4 = new Point(unit.getX() - unit.getWidth()/2 - hitRadius, unit.getY() - unit.getHeight()/2 - hitRadius);
        return new Point[] {p1, p2, p3, p4};
    }

    public static Section[] getSections(Point[] points) {
        Section[] sections = new Section[points.length+1];
        for(int i=0; i<points.length;i++) {
            Point p1 = points[i];
            Point p2 = i == points.length-1 ? points[0] : points[i+1];
            sections[i] = new Section(p1, p2);
        }
        return sections;
    }

    public static Point[] getUnitPoints(Unit unit) {
        return getUnitPoints(unit, unit.getX(), unit.getY(), unit.getAngle(), 0);
    }
    public static Point[] getUnitPoints(Unit unit, double x, double y, double angle, double hitSize) {
        Point c = new Point(x, y);
        Point dx1 = new Point(unit.getWidth()/2, 0);
        Point dy1 = new Point(0, unit.getHeight()/2);
        Point dx2 = new Point(-unit.getWidth()/2, 0);
        Point dy2 = new Point(0, -unit.getHeight()/2);
        Point p1 = Point.add(c, dx1, dy1);
        Point p2 = Point.add(c, dx1, dy2);
        Point p3 = Point.add(c, dx2, dy2);
        Point p4 = Point.add(c, dx2, dy1);

        double r = sqrt(pow(unit.getWidth()/2, 2) + pow(unit.getHeight()/2, 2)) + hitSize;

        Point[] points = new Point[] {p1, p2, p3, p4};
        for (Point point : points) {
            point.rotate(c, r, angle);
        }

        return points;
    }

    public static boolean positionTest(Unit unit, double x, double y, double angle) {
        Point[] points = getUnitPoints(unit, x, y, angle, 0);
        // проверка на границы мира
        for(Point point : points) {
            if (point.x < 0 || point.x > env.world.getWidth() || point.y < 0 || point.y > env.world.getHeight())
                return false;
        }
        // проверка на пересечение с танком
        // для грубой оценки
//        double r1 = sqrt(getHitRadius(unit, Geo.HitTestMode.maximum));
//        for(Tank tank : env.world.getTanks()) {
//            if (tank.getId() != unit.getId()) {
//                double r2 = sqrt(getHitRadius(tank, Geo.HitTestMode.maximum));
//                if (unit.getDistanceTo(tank) < r1 + r2) {
//                    // нужна детальная проверка
//                }
//
//            }
//        }
        return true;
    }

    /** stronger - признак проверки "попаду наверняка" */
    public static HitTestResult hitTest(Shell shell, Unit unit, double x, double y, double angle, boolean stronger) {

        double hitRadius = stronger ? shell.getHeight() : 0;


        Point[] points = getUnitPoints(unit, x, y, angle, hitRadius);

        Section trace = getShellSection(shell, shell.getDistanceTo(unit));

        Section[] hitSections = getSections(points);

        // точки пробития
        HitInfo hitInfo = getHitPoint(trace, hitSections);
        Point hitPoint = hitInfo.hitPoint;

        if (hitPoint.equals(Point.NaP)) {
            return HitTestResult.Miss;
        }

        // пересекается с нами
        // проверка на препятствия
        for (Tank aTank : env.world.getTanks()) {
            if (aTank.getId() != unit.getId()) {
                if (hitTestTank(aTank, trace, hitRadius))
                        return HitTestResult.Miss;
            }
        }
        // проверка на бонусы
        for(Bonus bonus : env.world.getBonuses()) {
            if (hitTestBonus(bonus, trace, hitRadius))
                    return HitTestResult.Miss;
        }


        if (stronger && unit instanceof Tank) {
            Tank tank = (Tank)unit;

            double distance = shell.getDistanceTo(tank);
            double fireR = distance*sin(shell.getAngleTo(tank));
            double minR = sqrt(getHitRadius(unit, Geo.HitTestMode.minimum));
            double maxR = getHitRadius(env.self, unit);
            // k 0..1
            double k = tank.getCrewHealth()/(double)tank.getCrewMaxHealth();
            double r = maxR - (maxR - minR)*k;

            if (abs(fireR) > r)
                return HitTestResult.Miss;
        }
        // проверка на рикошет
        Section attackSection = new Section(new Point(shell), hitPoint);
        if (shell.getType() == ShellType.REGULAR) {
            double angle1 = atan(attackSection.getK());
            double angle2 = atan(hitInfo.section.getK());
            if (abs(angle1 - angle2) <= PI/6 && env.self.getRemainingReloadingTime() == 0) {
                return HitTestResult.Miss;
            }
        }
        // показываем куда попали
        return new HitTestResult(getTickCountToHit(shell, hitPoint), TankSideType.Face, 0, hitPoint, hitInfo.section);

    }

    public static int getTickCountToHitSimple(double distance) {
        double speed = 16.7;
        double k = 1 - 1.0/99;
        int count = 0;
        while(distance > 0) {
            distance -= speed;
            speed *= k;
            count++;
        }
        return count;
    }

    public static int getTickCountToHit(Shell shell, Unit unit) {
        return getTickCountToHit(shell, new Point(unit));
    }
    public static int getTickCountToHit(Shell shell, Point point) {
        double distance = shell.getDistanceTo(point.x, point.y) - shell.getWidth()/2;
        double speed = sqrt(shell.getSpeedX()*shell.getSpeedX() + shell.getSpeedY()*shell.getSpeedY());
        double k = 1 - (shell.getType() == ShellType.REGULAR ? 1.0/99 : 1.0/199.0);
        int count = 0;
        while(distance > 0) {
            distance -= speed;
            speed *= k;
            count++;
        }
        return count;
    }
    private static HitInfo getHitPoint(Section trace, Section[] hitSections) {
        for(int i=0; i<4;i++) {
            Point hitPoint = Section.getIntersection(trace, hitSections[i]);
            if (!hitPoint.equals(Point.NaP)) {
                return new HitInfo(hitPoint, hitSections[i]);
            }
        }

        return new HitInfo(Point.NaP, null);
    }

    public static Section getShellSection(Shell shell, double radius) {
        Point p1 = new Point(shell);
        double angle = shell.getAngle();
        Point p2 = new Point(shell.getX() + radius * cos(angle), shell.getY() + radius * sin(angle));
        return  new Section(p1, p2);
    }


}

class HitInfo {
    Point hitPoint;
    Section section;

    public HitInfo(Point hitPoint, Section hitSection) {
        this.section = hitSection;
        this.hitPoint = hitPoint;
    }
}