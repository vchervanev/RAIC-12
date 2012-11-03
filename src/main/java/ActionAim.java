import model.FireType;
import model.Tank;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.signum;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 20:42
 * Экшн Прицеливания
 */
public class ActionAim extends Action{


    private Tank target;

    @Override
    public void estimate() {
        Tank[] tanks = env.world.getTanks();
        target = null;
        double currentCost = 0;
        for(Tank tank : tanks) {
            if (!env.isTarget(tank)) {
                continue;
            }
            double cost = env.self.getDistanceTo(tank);
            cost *= cost;
            double angleCost = 400*abs(env.self.getTurretAngleTo(tank))/PI;
            angleCost *= angleCost;
            cost += angleCost;

            if (target == null || currentCost > cost) {
                target = tank;
                currentCost = cost;
            }
        }
        // TODO выделить, когда достаточно повернуть пушку
        if (target == null) {
            variant = Variant.none;
        } else if (env.self.getRemainingReloadingTime() > 50){
            variant = Variant.aimSlowest;
        } else {
            if (currentCost < 200000)
                variant = Variant.aimFast;
            else if (currentCost < 400000)
                variant = Variant.aimAverage;
            else
                variant = Variant.aimSlow;
        }
    }

    @Override
    public void perform() {
        assert target != null;
        double angle = env.self.getTurretAngleTo(target);
        if (abs(angle) < env.self.getTurretTurnSpeed()) {
            env.move.setTurretTurn(angle);
            return;
        }  else {
            env.move.setTurretTurn(env.self.getTurretTurnSpeed());
            angle -= signum(angle)*env.self.getTurretTurnSpeed();
        }
        double leftPower = 0;
        double rightPower = 0;
        if(angle>0) {
            leftPower = 1;
            rightPower = -1;
        } else {
            leftPower = -1;
            rightPower = 1;
        }
        env.move.setLeftTrackPower(leftPower);
        env.move.setRightTrackPower(rightPower);
    }
}
