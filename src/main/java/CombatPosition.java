import model.Tank;

import java.util.List;

import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.signum;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 08.11.12
 * Time: 22:12
 * Разворот к противнику оптимальным образом
 */
public class CombatPosition extends Action {

    private double minReloadTime;
    private Tank worstEnemy;

    @Override
    public void estimate() {
        // ищем врага, который целится в нас и перезарядка которого закончится скорее всего
        List<Tank> enemies = env.getTargets();
        minReloadTime = 0;
        worstEnemy = null;

        final double hitSize = sqrt(BulletHelper.getHitRadius(env.self, Geo.HitTestMode.maximum));
        for(Tank enemy : enemies) {
            double distance = enemy.getDistanceTo(env.self);
            double maxAngle = atan(1.2*hitSize/distance);
            if (abs(enemy.getTurretAngleTo(env.self)) <= maxAngle ) {
                double reloadTime = enemy.getRemainingReloadingTime() + BulletHelper.getTickCountToHitSimple(distance);
                if (worstEnemy == null || minReloadTime > reloadTime ) {
                    minReloadTime = reloadTime;
                    worstEnemy = enemy;
                }
            }
        }

        if (worstEnemy != null) {
            variant = minReloadTime < 100 ? Variant.combatPosition : Variant.combatPositionSoon;
        } else
            variant = Variant.none;

    }

    //Tank oldWorstEnemy = null;

    @Override
    public void perform() {
        if (worstEnemy == null) {
            return;
        }
        double angleToEnemy = env.self.getAngleTo(worstEnemy);
        double distance = env.self.getDistanceTo(worstEnemy);
//        if (oldWorstEnemy == null || oldWorstEnemy.getId() != worstEnemy.getId()) {
//            oldWorstEnemy = worstEnemy;
//        }
        final double value = distance < 400 ? PI/6 : PI/2;
        final double delta = abs(angleToEnemy) - value;
        if (abs(delta) > PI/64) {

            if ((angleToEnemy > value || (angleToEnemy < 0 && angleToEnemy > - value))){
                env.move.setLeftTrackPower(minReloadTime > 30 ? 0.75 : 1);
                env.move.setRightTrackPower(-1);
            } else {
                env.move.setLeftTrackPower(-1);
                env.move.setRightTrackPower(minReloadTime > 30 ? 0.75 : 1);
            }
        }
    }
    public void tryPerformSecondary() {
        if (env.inTheCorner) {
            // при необходимости - резвернемся
            perform();
        }

    }

}
