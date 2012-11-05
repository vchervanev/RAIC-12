import model.FireType;
import model.Shell;
import model.ShellType;
import model.Tank;

import static java.lang.Math.PI;
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
//            if (abs(env.self.getTurretAngleTo(tank)) < PI/180) {
//                variant = Variant.fireAndKill;
//                fireType = FireType.PREMIUM_PREFERRED;
//            }

            Shell shell = BulletHelper.simulateShell(env.self, ShellType.REGULAR);
            double ttk1 = BulletHelper.checkHit(shell, tank);
            shell = BulletHelper.simulateShell(env.self, ShellType.PREMIUM);
            double ttk2 = BulletHelper.checkHit(shell, tank);
            if (ttk2 != -1) {
                variant = Variant.fireAndKill;
                fireType = FireType.PREMIUM_PREFERRED;
            } else if (ttk1 != -1) {
                variant = Variant.fireAndKill;
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
