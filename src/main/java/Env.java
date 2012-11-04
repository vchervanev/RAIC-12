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
    public double rotationSpeedMax = 0.062;
    public double rotationAcceleration = 0;

    double oldX;
    double oldY;
    double oldAngle;
    private String tmpOutOld;
    private double oldRotationSpeed;

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
        if (world.getTick() > 1) {
            this.moveSpeed = self.getDistanceTo(oldX, oldY);
            this.rotationSpeed = abs(oldAngle - abs(self.getAngle()));
            rotationAcceleration = rotationSpeed - oldRotationSpeed;
//            if (rotationSpeed > rotationSpeedMax) rotationSpeedMax = rotationSpeed;
        }
        oldX = self.getX();
        oldY = self.getY();
        oldAngle = abs(self.getAngle());
        oldRotationSpeed = rotationSpeed;
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

    public void rotateToAngle(double angle) {
        move.setLeftTrackPower(-1);
        move.setRightTrackPower(0.75);
        String tmpOut = roundX(self.getAngle()) + " " + " " + rotationSpeedMax+ " " + rotationAcceleration;
        if (!tmpOut.equals(tmpOutOld)) {
            System.out.println(tmpOut);
            tmpOutOld = tmpOut;
        }
    }


    public void directMoveTo(Unit unit) {
        if (unit == null) {
            return;
        }
        directMoveTo(unit.getX(), unit.getY());
    }

    public void directMoveTo(double x, double y) {
        final double delta = PI / 6;
        double angle = self.getAngleTo(x, y);
        double distance = self.getDistanceTo(x, y);

        double leftPower;
        double rightPower;

        if (angle > delta) {         // если угол сильно положительный,
            leftPower = 0.75;
            rightPower = -1;
        } else if (angle < -delta) {  // если угол сильно отрицательный,
            leftPower = -1;
            rightPower = 0.75;
        } else {
            leftPower = 1;
            rightPower = 1;
        }

        move.setLeftTrackPower(leftPower);
        move.setRightTrackPower(rightPower);
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
