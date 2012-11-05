import model.FireType;
import model.Shell;
import model.ShellType;
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
//            double newX = tank.getX()+tank.getSpeedX();
//            double newY = tank.getY()+tank.getSpeedY();
//
//            double turretAngleTo = env.self.getTurretAngleTo(newX, newY);
//
//            Shell shell = BulletHelper.simulateShell(env.self, ShellType.REGULAR, env.self.getTurretRelativeAngle() + env.self.getAngle() + turretAngleTo);
//            double ttk1 = BulletHelper.checkHit(shell, tank, Geo.HitTestMode.minimum);
//
//            if (ttk1 == -1 && env.self.getPremiumShellCount() != 0) {
//                shell = BulletHelper.simulateShell(env.self, ShellType.PREMIUM, env.self.getTurretRelativeAngle() + env.self.getAngle() + turretAngleTo);
//                ttk1 = BulletHelper.checkHit(shell, tank, Geo.HitTestMode.minimum);
//                if (ttk1 == -1) {
//                    currentCost = 3000;
//                }
//            }


//            double angleCost = 400*abs(turretAngleTo)/PI;
//            angleCost *= angleCost;
//            cost += angleCost;

            //бонус за дохликов
            double health = min(tank.getCrewHealth(), tank.getHullDurability());
//            cost -= 600*(1 - health/100.0);
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
        } else if (env.self.getRemainingReloadingTime() > 15){
            variant = Variant.none;
        } else {
            if (currentCost < 500)
                variant = Variant.aimFast;
            else if (currentCost < 800)
                variant = Variant.aimAverage;
            else if (currentCost < 2000)
                variant = Variant.aimSlow;
            else
                variant = Variant.none;
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
