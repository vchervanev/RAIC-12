import model.FireType;
import model.Tank;

import static java.lang.Math.*;

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
            double newX = tank.getX()+tank.getSpeedX();
            double newY = tank.getY()+tank.getSpeedY();

            double angleCost = 400*abs(env.self.getTurretAngleTo(newX, newY))/PI;
            angleCost *= angleCost;
            cost += angleCost;

            //бонус за дохликов
            double health = min(tank.getCrewHealth(), tank.getHullDurability());
            cost -= 600*(1 - health/100.0);
            if (health < 20){
                cost -= 200;
            }

            if (target == null || currentCost > cost) {
                target = tank;
                currentCost = cost;
            }
        }
        // TODO выделить, когда достаточно повернуть пушку
        if (target == null) {
            variant = Variant.none;
        } else if (env.self.getRemainingReloadingTime() > 10){
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
        // TODO убрать копипаст из частичного эпляя
        double newX = target.getX()+target.getSpeedX()*1.2;
        double newY = target.getY()+target.getSpeedY()*1.2;
        double angle = env.self.getTurretAngleTo(newX, newY);
        if (abs(angle) < env.self.getTurretTurnSpeed()) {
            env.move.setTurretTurn(angle);
            return;
        }  else {
            env.move.setTurretTurn(signum(angle)*env.self.getTurretTurnSpeed());
            angle -= signum(angle)*env.self.getTurretTurnSpeed();
        }
        double leftPower = 0;
        double rightPower = 0;
        if(angle>0) {
            leftPower = 1;
            rightPower = -0.3;
        } else {
            leftPower = -0.3;
            rightPower = 1;
        }
        env.move.setLeftTrackPower(leftPower);
        env.move.setRightTrackPower(rightPower);
    }

    @Override
    public void tryPerformSecondary() {
        // TODO сделать проверку, можно ли двигать пушку
        // TODO убрать копипаст сверху
        double newX = target.getX()+target.getSpeedX();
        double newY = target.getY()+target.getSpeedY();
        double angle = env.self.getTurretAngleTo(newX, newY);
        if (abs(angle) < env.self.getTurretTurnSpeed()) {
            env.move.setTurretTurn(angle);
        }  else {
            env.move.setTurretTurn(signum(angle)*env.self.getTurretTurnSpeed());
            //angle -= signum(angle)*env.self.getTurretTurnSpeed();
        }
    }
}
