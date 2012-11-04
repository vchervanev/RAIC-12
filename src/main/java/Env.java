import model.*;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.round;

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
    public double moveSpeed = 0;
    public double rotationSpeed = 0;
    public double rotationSpeedMax = 0;

    double oldX;
    double oldY;
    double oldAngle;
    private String tmpOutOld;

    public boolean isTarget(Tank tank) {
        return tank.getCrewHealth() != 0 && tank.getHullDurability() != 0 &&
                !tank.isTeammate() && tank != self;
    }

    public void init(Tank self, World world, Move move) {
        // TODO поддержка нескольких инициализаций за один тик, когда несколько танков во взводе
        this.oldMove = this.move;
        this.oldSelf = this.self;
        this.oldWorld = this.world;

        this.move = move;
        this.self = self;
        this.world = world;

        calcSpeed();

        if (!init)
            return;

//        for(Tank tank : world.getTanks()) {
//            if (tank.getId() != self.getId()) {
//                tankIds.add(tank.getId());
//            }
//        }

        init = false;
    }

    private void calcSpeed() {
        if (world.getTick() < 2) return;
        this.moveSpeed = self.getDistanceTo(oldX, oldY);
        oldX = self.getX();
        oldY = self.getY();
        this.rotationSpeed = abs(oldAngle - self.getAngle());
        oldAngle = self.getAngle();
        if (rotationSpeed > rotationSpeedMax) rotationSpeedMax = rotationSpeed;
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
        final double delta = PI / 4;
        double angle = self.getAngleTo(x, y);
        double distance = self.getDistanceTo(x, y);
        double speedX = 1;

//        world.getWidth()
        if (distance < moveSpeed) {
//            speedX = distance / moveSpeed;
        }
        double leftPower;
        double rightPower;
        if (angle > 0) {
            if (angle > delta) {
                leftPower = 0.75;
                rightPower = -1;
//                rotationSpeedMax = 0;
            } else {
                leftPower = 1;
                rightPower = 1 - (2.25 * angle / rotationSpeedMax);
            }
        } else {
            if (angle < -delta) {
                leftPower = -1;
                rightPower = 0.75;
//                rotationSpeedMax = 0;
            } else {
                leftPower = 1 + (2.25 * angle / rotationSpeedMax);
                rightPower = 1;
            }
        }

        if (leftPower < -1) {
            rightPower = 2 + leftPower;
            leftPower = -1;
        }
        if (rightPower < -1) {
            leftPower = 2 + rightPower;
            rightPower = -1;
        }

        leftPower = leftPower * speedX;
        rightPower = rightPower * speedX;

        move.setLeftTrackPower(leftPower);
        move.setRightTrackPower(rightPower);
        String tmpOut = roundX(angle) + " " + roundX(leftPower) + " " + roundX( rightPower) + " " + isBorder();
        if (!tmpOut.equals(tmpOutOld)) {
            System.out.println(tmpOut);
            tmpOutOld = tmpOut;
        }
    }

    private double roundX(double x) {
        return round(x*100.0)/100.0;
    }


    private boolean isBorder() {
        return (self.getX() < self.getHeight()
                || self.getY() < self.getWidth()
                || world.getWidth() < self.getX() + self.getWidth()
                || world.getHeight() < self.getY() + self.getWidth()
        );
    }

}
