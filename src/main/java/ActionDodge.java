import model.Shell;

import static java.lang.Math.*;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 05.11.12
 * Time: 0:11
 * To change this template use File | Settings | File Templates.
 */
public class ActionDodge extends Action {
    final int DELTA = 60;
    final double ROTATE = PI/18;

    static DodgeVariant[] dodgeVariants = {
            new DodgeVariant(+1, +0),
            new DodgeVariant(+0.5, +1),
            new DodgeVariant(+0.5, -1),
            new DodgeVariant(+0, +1),
            new DodgeVariant(+0, -1),
            new DodgeVariant(-0.75, +0),
            new DodgeVariant(-0.5, +1),
            new DodgeVariant(-0.5, -1),
    };

    Shell shell;
    DodgeVariant dodge = null;

    @Override
    public void estimate() {

        if (env.world.getTick() < 150) {
            dodge = null;
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
        if (ticks < 3 ) {
            variant = Variant.none;
            return;
        }
        // к-т здоровья 0..1
        double k = env.self.getCrewHealth()/(double)env.self.getCrewMaxHealth();
        // замедляем 0.5..1
        k = 0.5 + 0.5*k;
        // кратно тикам (норматив - 35)
        k *= ticks/(double)35;

        //продолжаем, ищем нычку
        for(DodgeVariant dodgeVariant : dodgeVariants) {
            double len = dodgeVariant.power * DELTA;
            double angle = env.self.getAngle();
            double x = env.self.getX() + len*cos(angle)*k;
            double y = env.self.getY() + len*sin(angle)*k;
            angle = env.self.getAngle() + dodgeVariant.rotate * ROTATE * k;

            if (!BulletHelper.positionTest(env.self, x, y, angle)) {
                continue;
            }
            HitTestResult htr = BulletHelper.hitTest(shell, env.self, x, y, angle, false);

            if (htr.tickCount == -1) {
                dodge = dodgeVariant;
                break;
            }
        }

        variant = dodge == null ? Variant.none : Variant.dodge;
    }



    @Override
    public void perform() {
//        if (shell != null) {
//            double  selfAngle = env.self.getAngleTo(shell);
//
//            double leftPower;
//            double rightPower;
//            if (abs(selfAngle) > PI/6 && abs(selfAngle) < PI-PI/6) {
//                leftPower = selfAngle > 0 ? 1 : -1;
//                rightPower = leftPower;
//            } else {
//                if ((selfAngle > 0 && selfAngle < PI/6 ) || (selfAngle < 0 && selfAngle < PI/6-PI)) {
//                    leftPower = -1;
//                    rightPower = 1;
//                } else {
//                    leftPower = 1;
//                    rightPower = -1;
//                }
//            }
//
//            env.move.setLeftTrackPower(leftPower);
//            env.move.setRightTrackPower(rightPower);
//        }
//    }
        if (dodge == null)
            return;
        double left;
        double right;
        if (dodge.rotate == 0) {
            left = 1;
            right = 1;
            if (dodge.power < 0) {
                left *= -1;
                right *= -1;
            }
        } else {
            // поворот
            if (dodge.power == 0) {
                if (dodge.rotate > 0) {
                    left = 1;
                    right = - 0.75;
                } else {
                    left = - 0.75;
                    right = 1;
                }
            } else {
                if (dodge.power > 0) {
                    left = 1;
                    right = 0.3;
                } else {
                    left = -0.3;
                    right = -1;
                }
                if (dodge.rotate < 0) {
                    double tmp = left;
                    left = right;
                    right = tmp;
                }
            }
        }
        env.move.setLeftTrackPower(left);
        env.move.setRightTrackPower(right);

    }
}

class DodgeVariant {
    double power;
    double rotate;

    public DodgeVariant(double power, double rotate) {
        this.rotate = rotate;
        this.power = power;
    }
}
