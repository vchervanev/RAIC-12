import model.FireType;
import model.Shell;
import model.ShellType;
import model.Tank;

import static java.lang.Math.*;
import static java.lang.StrictMath.abs;

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



            final double hitSize = sqrt(BulletHelper.getHitRadius(env.self, Geo.HitTestMode.maximum));
            double distance = env.self.getDistanceTo(tank);
            double cost = distance;
            double maxAngle = atan(1.2*hitSize/distance);
            if (abs(tank.getTurretAngleTo(env.self)) <= maxAngle ) {
                cost += -600 + tank.getRemainingReloadingTime();
            }

            double newX = tank.getX()+tank.getSpeedX()*8;
            double newY = tank.getY()+tank.getSpeedY()*8;
            double turretAngleTo = env.self.getTurretAngleTo(newX, newY);
            Shell shell = BulletHelper.simulateShell(env.self, ShellType.REGULAR, env.self.getTurretRelativeAngle() + env.self.getAngle() + turretAngleTo);
            double ttk1 = BulletHelper.hitTest(shell, tank, true).tickCount;
                    //BulletHelper.checkHit(shell, tank, Geo.HitTestMode.minimum);

            if (ttk1 == -1) {
                continue; // раньше уменьшали цену
            }


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
            if (health < 21){
                cost -= 300;
            }



            if (target == null || currentCost > cost) {
                target = tank;
                currentCost = cost;
            }
        }
        // TODO выделить, когда достаточно повернуть пушку
        if (target == null) {
            variant = Variant.none;
        } else if (env.self.getRemainingReloadingTime() > 20){
            variant = Variant.none;
        } else {
            if (currentCost < 400)
                variant = Variant.aimUrgent;
            else if (currentCost < 500)
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
        if (target == null)
            return;

        // TODO убрать копипаст из частичного эпляя
        double newX = target.getX()+target.getSpeedX()*8;
        double newY = target.getY()+target.getSpeedY()*8;
        double angle = env.self.getTurretAngleTo(newX, newY);

        final double turretTurnSpeed = env.self.getTurretTurnSpeed()*(0.5 + 0.5*env.self.getCrewHealth()/100);

        if (abs(angle) < turretTurnSpeed) {
            env.move.setTurretTurn(angle);
            return;
        }  else {
            env.move.setTurretTurn(signum(angle)* turretTurnSpeed);
            angle -= signum(angle)* turretTurnSpeed;
        }
        double leftPower = 0;
        double rightPower = 0;
        if(angle>0) {
            leftPower = 1;
            rightPower = -0.5;
        } else {
            leftPower = -0.5;
            rightPower = 1;
        }
        env.move.setLeftTrackPower(leftPower);
        env.move.setRightTrackPower(rightPower);
    }

    @Override
    public void tryPerformSecondary() {
        if (target == null)
            return;
        // TODO сделать проверку, можно ли двигать пушку
        // TODO убрать копипаст сверху
        double newX = target.getX()+target.getSpeedX()*8;
        double newY = target.getY()+target.getSpeedY()*8;
        double angle = env.self.getTurretAngleTo(newX, newY);
        if (abs(angle) < env.self.getTurretTurnSpeed()*(0.5 + 0.5*env.self.getCrewHealth()/100)) {
            env.move.setTurretTurn(angle);
        }  else {
            env.move.setTurretTurn(signum(angle)*env.self.getTurretTurnSpeed());
            //angle -= signum(angle)*env.self.getTurretTurnSpeed();
        }
    }
}
