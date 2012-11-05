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

    public static double getHitRadius(Unit unit, Geo.HitTestMode hitTestMode) {
        if (hitTestMode == Geo.HitTestMode.minimum) {
            return pow(min(unit.getWidth(), unit.getHeight())/2.0, 2);
        } else {
            return pow(unit.getHeight()/2.0, 2) + pow(unit.getWidth()/2.0, 2);
        }
    }

    public static int checkHit(Shell shell, Tank tank, Geo.HitTestMode hitTestMode) {
        int ticks = 0;
        double distance = Geo.getDistancePow2(shell, tank);
        double newDistance = distance;

        double x1 = shell.getX();
        double y1 = shell.getY();

        double x2 = tank.getX();
        double y2 = tank.getY();

        double vx1 = shell.getSpeedX();
        double vy1 = shell.getSpeedY();

        double a1 = 1 - (shell.getType() == ShellType.REGULAR ? 1.0/99 : 1.0/199.0);
        double a2 = 1;

        double hitRadius = getHitRadius(tank, hitTestMode);


        do {
            distance = newDistance;

            x1 += vx1;
            vx1 *= a1;
            y1 += vy1;
            vy1 *= a1;

            x2 += tank.getSpeedX();
            y2 += tank.getSpeedY();

            newDistance = pow(x1 - x2, 2) + pow(y1 - y2, 2);
            for(Bonus bonus : MyStrategy.env.world.getBonuses()) {
                if (Geo.getDistancePow2(bonus, x1, y1) < pow(bonus.getHeight(),2)/2.0) {
                    //System.out.print("bonus stops bullet\n");
                    return -1;
                }
            }

            for(Tank aTank :  MyStrategy.env.world.getTanks()) {
                // в минимальном режиме максимальные помехи (проверка на попадание)
                // в максимальном режиме минимальные помехи (проверка на защиту)
                double aHitRadius = getHitRadius(aTank, hitTestMode);

                if (    aTank != tank &&
                        !aTank.getPlayerName().equals(tank.getPlayerName())
                        && aTank.getCrewHealth() != 0 && aTank.getHullDurability() != 0 &&
                        Geo.getDistancePow2(aTank, x1, y1) < aHitRadius)
                    return -1;
            }

            if (newDistance < hitRadius)
                return ticks;
            ticks++;
        } while (newDistance < distance);
        return -1;
    }
    static long newId = 99999;

    /** Метод создает пулю, словно ее выстрелил танк*/
    public static Shell simulateShell(Tank tank, ShellType shellType) {
        return simulateShell(tank, shellType, tank.getTurretRelativeAngle() + tank.getAngle());
    }

    public static Shell simulateShell(Tank tank, ShellType shellType, double angle) {
        double speed = shellType == ShellType.REGULAR ? 16.7 : 13.3;
        double length = tank.getVirtualGunLength();
        Shell shell = new Shell(newId++, "", 5, 5, tank.getX() + length*cos(angle), tank.getY() + length*sin(angle),
                /*tank.getSpeedX() + */speed*cos(angle), /*tank.getSpeedY() + */speed*sin(angle), angle, 0, shellType);
        return shell;
    }
}
