import model.FireType;
import model.Shell;
import model.ShellType;
import model.Tank;

import static java.lang.Math.abs;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 19:32
 * Экшн немедленного выстрела
 */
public class ActionFire extends Action{
    FireType fireType = FireType.NONE;
    @Override
    public void estimate() {
        fireType = FireType.NONE;
        variant = Variant.none;

        if (env.self.getRemainingReloadingTime() != 0) {
            return;
        }

        Tank[] tanks = env.world.getTanks();
        for(Tank tank : tanks) {
            if (!env.isTarget(tank)) {
                continue;
            }
            Shell shell = BulletHelper.simulateShell(env.self, ShellType.REGULAR);
            double ttk1 = BulletHelper.hitTest(shell, tank, true).tickCount;
                    //checkHit(shell, tank, Geo.HitTestMode.minimum);
            shell = BulletHelper.simulateShell(env.self, ShellType.PREMIUM);
            double ttk2 = -1;
            final double distanceToTank = env.self.getDistanceTo(tank);
            if  (env.self.getPremiumShellCount() != 0 &&
                    ((distanceToTank < 600 ) || (distanceToTank < 800 && tank.getCrewHealth() < 40))) {
                ttk2 = BulletHelper.hitTest(shell, tank, true).tickCount;
                        //checkHit(shell, tank, Geo.HitTestMode.minimum);
            }
            if (ttk2 != -1) {
                fireType = FireType.PREMIUM_PREFERRED;
            } else if (ttk1 != -1) {
                fireType = FireType.REGULAR;
            }
        }
    }

    @Override
    public void perform() {
        env.move.setFireType(fireType);
    }

    @Override
    public void tryPerformSecondary() {
        perform();
    }
}
