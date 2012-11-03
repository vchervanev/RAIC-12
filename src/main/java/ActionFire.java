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
            if (tank.getCrewHealth() == 0 || tank.getHullDurability() == 0 ||
                    tank.isTeammate()) {
                continue;
            }
            if (abs(env.self.getAngleTo(tank)) < PI/180) {
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
