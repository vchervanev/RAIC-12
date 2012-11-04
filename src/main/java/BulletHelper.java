import model.Bonus;
import model.Shell;
import model.ShellType;
import model.Tank;

import static java.lang.Math.*;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 04.11.12
 * Time: 22:54
 * Утилитные классы рассчета баллистики
 */
public class BulletHelper {
    public static int checkHit(Shell shell, Tank tank) {
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

        double hitRadius = pow(min(tank.getHeight(), tank.getWidth())/2, 2);

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
                if (Geo.getDistancePow2(bonus, x1, y1) < pow(bonus.getHeight()/2,2)) {
                    System.out.print("bonus stops bullet\n");
                    return -1;
                }
            }
            // мой танк портит все
            for(Tank aTank :  MyStrategy.env.world.getTanks()) {
                if (    aTank != tank &&
                        !aTank.getPlayerName().equals(tank.getPlayerName())
                        && aTank.getCrewHealth() != 0 && aTank.getHullDurability() != 0 &&
                        Geo.getDistancePow2(aTank, x1, y1) < pow(min(aTank.getHeight(), aTank.getWidth())/2, 2))
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
        double angle = tank.getTurretRelativeAngle() + tank.getAngle();
        double speed = shellType == ShellType.REGULAR ? 16.7 : 13.3;
        double length = tank.getVirtualGunLength();
        Shell shell = new Shell(newId++, "", 5, 5, tank.getX() + length*cos(angle), tank.getY() + length*sin(angle), speed*cos(angle), speed*sin(angle), angle, 0, shellType);
        return shell;
    }
}
