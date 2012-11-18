import model.FireType;
import model.Shell;
import model.ShellType;
import model.Tank;

import java.util.Comparator;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.tan;
import static java.util.Collections.sort;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 20:42
 * Экшн Прицеливания
 */
public class ActionAim extends Action{

//    static Movement[] movements = new Movement[] {
//        new Movement(25, 0),
//        new Movement(50, 0),
//        new Movement(15, PI),
//        new Movement(35, PI),
//        new Movement(30, PI/5),
//        new Movement(30, -PI/5),
//    };

    private Tank target;
    private double attackAngle;
//    private Movement moving;

    @Override
    public void estimate() {
        Tank oldTarget = target;
        target = null;

        List<Tank> targets = env.getTargets();
        sort(targets, new Comparator<Tank>() {
            @Override
            public int compare(Tank o1, Tank o2) {
                return (int)round(Geo.getDistancePow2(env.self, o1) - Geo.getDistancePow2(env.self, o2));
            }
        });

        Tank first = null; // замена Target если попасть ни в кого не можем
//        moving = null;

        for(Tank tank : targets) {
            if (first == null) {
                first = tank;
            }

            double ttk1;
            AimInfo aimInfo = BulletHelper.getAimInfo(env.self, tank);

            attackAngle = env.self.getAngle() + env.self.getTurretAngleTo(tank);
            //прицеливаемся прямой наводкой
            ttk1 = hitTest(tank);

            if (ttk1 == -1) {
                //пробуем бить по старому углу
                if (oldTarget != null && tank.getId() == oldTarget.getId()) {
                    ttk1 = hitTest(tank);
                }
            }

            if (ttk1 == -1 && attackAngle < aimInfo.maxAngle && attackAngle > aimInfo.minAngle) {
                attackAngle = env.self.getAngle();
                // по прямой
                ttk1 = hitTest(tank);
            }

            double viewAngle = aimInfo.maxAngle - aimInfo.minAngle;
            if (ttk1 == -1) {
                // +1/3
                attackAngle = env.self.getAngle() + aimInfo.minAngle + viewAngle/3.0;
                ttk1 = hitTest(tank);
            }

            if (ttk1 == -1) {
                // -1/3
                attackAngle = env.self.getAngle() + aimInfo.maxAngle - viewAngle/3.0;
                ttk1 = hitTest(tank);
            }

            if (ttk1 == -1) {
                // +1/4
                attackAngle = env.self.getAngle() + aimInfo.minAngle + viewAngle/4.0;
                ttk1 = hitTest(tank);
            }

            if (ttk1 == -1) {
                // -1/4
                attackAngle = env.self.getAngle() + aimInfo.minAngle - viewAngle/4.0;
                ttk1 = hitTest(tank);
            }

            if (ttk1 == -1) {
                // ++1/3
                attackAngle = env.self.getAngle() + aimInfo.maxAngle + viewAngle/3.0;
                ttk1 = hitTest(tank);
            }

            if (ttk1 == -1) {
                // --1/3
                attackAngle = env.self.getAngle() + aimInfo.minAngle - viewAngle/3.0;
                ttk1 = hitTest(tank);
            }


//            // тестируем маневры
//            for(Movement movement : movements) {
//                double x = env.self.getX() + movement.distance*cos(env.self.getAngle() + movement.angle);
//                double y = env.self.getY() + movement.distance*sin(env.self.getAngle() +movement.angle);
//                if (!BulletHelper.positionTest(env.self, x, y, movement.angle))
//                    continue;
//                if (hitTest(tank, x, y) != -1) {
//                    moving = movement;
//                    break;
//                }
//            }

            if (ttk1 == -1 /*&& moving == null*/) {
                continue;
            }

            target = tank;
            break;
        }
        // TODO выделить, когда достаточно повернуть пушку
        if (target == null || env.self.getRemainingReloadingTime() > 20+60*(1-env.self.getCrewHealth()/env.self.getCrewMaxHealth())) {
            variant = Variant.none;
        } else {
            if (Math.abs(env.self.getAngle() + env.self.getTurretRelativeAngle() - attackAngle) > PI / 6)
                variant = Variant.aimUrgent;
            else
                variant = Variant.aimFast;
        }

        if (target == null)
            target = first;
    }

//    private double hitTest(Tank tank, double x, double y) {
//        double angle = 2*PI - tank.getAngleTo(x, y); // прямой наводкой
//        Shell shell = BulletHelper.simulateShell(env.self, ShellType.REGULAR, x, y, angle);
//        return (double) BulletHelper.hitTest(shell, tank, true).tickCount;
//    }

    private double hitTest(Tank tank) {
        Shell shell = BulletHelper.simulateShell(env.self, ShellType.REGULAR, attackAngle);
        return (double) BulletHelper.hitTest(shell, tank, true).tickCount;
    }

    @Override
    public void perform() {
        if (target == null/* && moving == null*/)
            return;

//        if (moving != null) {
//            if (moving.angle == 0) {
//                env.move.setLeftTrackPower(1);
//                env.move.setRightTrackPower(1);
//            } else if (moving.angle > 0) {
//                env.move.setLeftTrackPower(0.75);
//                env.move.setRightTrackPower(-1);
//            } else {
//                env.move.setLeftTrackPower(-1);
//                env.move.setRightTrackPower(0.75);
//            }
//            return;
//        }

        double angle = attackAngle - env.self.getAngle() - env.self.getTurretRelativeAngle();

        final double turretTurnSpeed = env.self.getTurretTurnSpeed()*(0.5 + 0.5*env.self.getCrewHealth()/100);

        if (abs(angle) < turretTurnSpeed) {
            env.move.setTurretTurn(angle);
            return;
        }  else {
            env.move.setTurretTurn(signum(angle)* turretTurnSpeed);
            angle -= signum(angle)* turretTurnSpeed;
        }
        double leftPower;
        double rightPower;
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
        double angle = env.self.getTurretAngleTo(target); //attackAngle - env.self.getAngle() - env.self.getTurretRelativeAngle();
        if (abs(angle) < env.self.getTurretTurnSpeed()*(0.5 + 0.5*env.self.getCrewHealth()/100)) {
            env.move.setTurretTurn(angle);
        }  else {
            env.move.setTurretTurn(signum(angle)*env.self.getTurretTurnSpeed());
        }
    }
}

//class Movement {
//    double distance;
//    double angle;
//
//    public Movement(double distance, double angle) {
//        this.angle = angle;
//        this.distance = distance;
//    }
//}