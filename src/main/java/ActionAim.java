import model.FireType;
import model.Tank;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 20:42
 * Экшн Прицеливания
 */
public class ActionAim extends Action{
    public static final int NOT_A_SPEED = 9999;
    double leftSpeed = NOT_A_SPEED;
    double rightSpeed = NOT_A_SPEED;
    double angleValue = NOT_A_SPEED;


    @Override
    public void estimate() {
        Tank[] tanks = env.world.getTanks();
        Tank target = null;
        double cost = 0;
        for(Tank tank : tanks) {
            if (!Env.isTarget(tank)) {
                continue;
            }
            double delta = abs(env.self.getAngleTo(tank));
            if (delta < 3*PI/180) {
                variant = Variant.aimFast;
            }
        }
    }

    @Override
    public void perform() {
        if(leftSpeed != NOT_A_SPEED) {
            env.move.setLeftTrackPower(leftSpeed);
        }

        if(rightSpeed != NOT_A_SPEED) {
            env.move.setRightTrackPower(rightSpeed);
        }

        if(angleValue != NOT_A_SPEED) {
            env.move.setTurretTurn(angleValue);
        }
    }
}
