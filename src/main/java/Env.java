import model.*;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 11:48
 * Всё окружающее
 */
public class Env {
    boolean init = true;
    public Move oldMove = null;
    public Move move;
    public World oldWorld = null;
    public World world;
    public Tank oldSelf = null;
    public Tank self;

    public static boolean isTarget(Tank tank) {
        return tank.getCrewHealth() != 0 || tank.getHullDurability() != 0 ||
                !tank.isTeammate();
    }

    public void init(Tank self, World world, Move move) {
        // TODO поддержка нескольких инициализаций за один тик, когда несколько танков во взводе
        this.oldMove = this.move;
        this.oldSelf = this.self;
        this.oldWorld = this.world;

        this.move = move;
        this.self = self;
        this.world = world;

        if (!init)
            return;

//        for(Tank tank : world.getTanks()) {
//            if (tank.getId() != self.getId()) {
//                tankIds.add(tank.getId());
//            }
//        }

        init = false;
    }

    private Tank getTank(int index) {
//        long id = tankIds.get(index);
//        for(Tank tank : world.getTanks()) {
//            if (id == tank.getId()) {
//                return tank;
//            }
//        }
        return null;
    }

    public void directMoveTo(Unit unit) {
        if (unit == null) {
            return;
        }
        directMoveTo(unit.getX(), unit.getY());
    }

    public void directMoveTo(double x, double y) {
        final double delta = PI/180;
        double angle = self.getAngleTo(x, y);
        double leftPower;
        double rightPower;
        if (abs(angle) < delta) {
            leftPower = 1;
            rightPower = 1;
        } else if (angle > delta) {
            if (angle > PI/4) {
                leftPower = 0.75;
                rightPower = -1;
            } else {
                leftPower = 1;
                rightPower = 1 - angle/PI;
            }
        } else {
            if (angle < PI/4) {
                leftPower = -1;
                rightPower = 0.75;
            } else {
                leftPower = 1 + angle/PI;
                rightPower = 1;
            }
        }

        move.setLeftTrackPower(leftPower);
        move.setRightTrackPower(rightPower);
    }

}
