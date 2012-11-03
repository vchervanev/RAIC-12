import model.FireType;
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
        if (env.self.getRemainingReloadingTime() != 0) {
            fireType = FireType.NONE;
            variant = Variant.none;
            return;
        }
        Tank[] tanks = env.world.getTanks();
        for(Tank tank : tanks) {
            if (!env.isTarget(tank)) {
                continue;
            }
            // TODO выбрать лучший танк для стрельбы (сейчас - последний)
            if (abs(env.self.getTurretAngleTo(tank)) < PI/180) {
                variant = Variant.fireAndKill;
                fireType = FireType.PREMIUM_PREFERRED;
            }
        }
    }

    @Override
    public void perform() {
        env.move.setFireType(fireType);
    }
}
