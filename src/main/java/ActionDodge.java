import model.Shell;
import model.Unit;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 05.11.12
 * Time: 0:11
 * To change this template use File | Settings | File Templates.
 */
public class ActionDodge extends Action {

    Shell shell;
    @Override
    public void estimate() {

        if (env.world.getTick() < 150) {
            variant = Variant.none;
            shell = null;
            return;
        }
        Shell[] shells = env.world.getShells();
        double ticks = -1;
        for (Shell aShell : shells) {
            String name = aShell.getPlayerName();
            if (!env.myName.equals(name)) {
                //анализ полёта без учета скорости
                double newTicks = BulletHelper.hitTest(aShell, env.self, env.self.getX(), env.self.getY(), env.self.getAngle(), false).tickCount;
                        //BulletHelper.checkHit(shell, env.self, Geo.HitTestMode.maximum);
                if (newTicks > ticks ) {
                    ticks = newTicks;
                    shell = aShell;
                }
            }
        }
        if (ticks != -1 ) {
            variant = Variant.dodge;
        } else
            variant = Variant.none;
    }

    @Override
    public void perform() {
        if (shell != null) {
            double  selfAngle = env.self.getAngleTo(shell);

            double leftPower;
            double rightPower;
            if (abs(selfAngle) > PI/6 && abs(selfAngle) < PI-PI/6) {
                leftPower = selfAngle > 0 ? 1 : -1;
                rightPower = leftPower;
            } else {
                if ((selfAngle > 0 && selfAngle < PI/6 ) || (selfAngle < 0 && selfAngle < PI/6-PI)) {
                    leftPower = -1;
                    rightPower = 1;
                } else {
                    leftPower = 1;
                    rightPower = -1;
                }
            }

            env.move.setLeftTrackPower(leftPower);
            env.move.setRightTrackPower(rightPower);
        }
    }
}
