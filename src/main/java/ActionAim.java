import model.FireType;
import model.Shell;
import model.ShellType;
import model.Tank;

import java.util.Comparator;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.StrictMath.abs;
import static java.util.Collections.sort;

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
        target = null;

        List<Tank> targets = env.getTargets();
        sort(targets, new Comparator<Tank>() {
            @Override
            public int compare(Tank o1, Tank o2) {
                return (int)round(Geo.getDistancePow2(env.self, o1) - Geo.getDistancePow2(env.self, o2));
            }
        });

        for(Tank tank : targets) {

            double turretAngleTo = env.self.getTurretAngleTo(tank);
            Shell shell = BulletHelper.simulateShell(env.self, ShellType.REGULAR, env.self.getTurretRelativeAngle() + env.self.getAngle() + turretAngleTo);
            double ttk1 = BulletHelper.hitTest(shell, tank, true).tickCount;
                    //BulletHelper.checkHit(shell, tank, Geo.HitTestMode.minimum);

            if (ttk1 == -1) {
                continue; // раньше уменьшали цену
            }

            target = tank;
            break;
        }
        // TODO выделить, когда достаточно повернуть пушку
        if (target == null || env.self.getRemainingReloadingTime() > 20+60*(1-env.self.getCrewHealth()/env.self.getCrewMaxHealth())) {
            variant = Variant.none;
        } else {
            if (Math.abs(env.self.getTurretAngleTo(target)) > PI / 6)
                variant = Variant.aimUrgent;
            else
                variant = Variant.aimFast;
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
