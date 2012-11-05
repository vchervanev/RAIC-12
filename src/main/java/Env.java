import model.*;

import static java.lang.StrictMath.*;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 11:48
 * Всё окружающее
 */
public class Env {
    public int tickId = -1;
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
    public double rotationAcceleration = 0;

    public Point[] nooks = new Point[4];

    private String tmpOutOld;
    private double oldRotationSpeed;
    public double dimention;
    public String myName;

    public static final double NO_ROTATE = PI / 2;
    public static final double DELTA = PI / 6;

    public boolean isTarget(Tank tank) {
        return tank.getCrewHealth() != 0 && tank.getHullDurability() != 0 &&
                !tank.isTeammate() && tank != self;
    }

    public void init(Tank self, World world, Move move) {
        tickId++;

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

        dimention = max(self.getWidth(), self.getHeight())*1.3;


        nooks[0] = new Point(dimention, dimention);
        nooks[1] = new Point(world.getWidth() - dimention, dimention);
        nooks[2] = new Point(dimention, world.getHeight() - dimention);
        nooks[3] = new Point(world.getWidth() - dimention, world.getHeight() - dimention);
        myName = self.getPlayerName();

//        for(Tank tank : world.getTanks()) {
//            if (tank.getId() != self.getId()) {
//                tankIds.add(tank.getId());
//            }
//        }

        init = false;
    }

    private void calcSpeed() {
        if (init) return;
        moveSpeed = self.getDistanceTo(oldSelf);
        rotationSpeed = abs(oldSelf.getAngle() - self.getAngle());
        if (rotationSpeed > PI) rotationSpeed = 2 * PI - rotationSpeed;
        rotationAcceleration = rotationSpeed - oldRotationSpeed;
        if (rotationSpeed > rotationSpeedMax) rotationSpeedMax = rotationSpeed;
        oldRotationSpeed = rotationSpeed;
    }

    public double getSpeed(Unit in) {
        double x = in.getSpeedX();
        double y = in.getSpeedY();
        return Math.sqrt(x*x+y*y);
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
        String tmpOut = roundX(self.getAngle()) + " " + " " + rotationSpeedMax + " " + rotationAcceleration;
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

    public double getDistanceTo(Point point) {
        return self.getDistanceTo(point.x, point.y);
    }

    public void moveToCorner() {
        double minDist = 0;
        Point minNook = null;
        for (Point nook : nooks) {
            double dist = getDistanceTo(nook);
            if (dist < minDist || minNook == null) {
                minDist = dist;
                minNook = nook;
            }
        }
        if (minDist > dimention / 2)
            directMoveTo(minNook.x, minNook.y);
        else {

            double angleToNook = self.getAngleTo(minNook.x, minNook.y);

            if (abs(angleToNook) < PI - DELTA) {
                if (angleToNook < 0) {
                    move.setLeftTrackPower(-1);
                    move.setRightTrackPower(0.75);
                } else {
                    move.setLeftTrackPower(0.75);
                    move.setRightTrackPower(-1);
                }
            }
        }
    }



    public void directMoveTo(double x, double y) {
        double leftPower;
        double rightPower;

        double angle = self.getAngleTo(x, y);
        double distance = self.getDistanceTo(x, y);

        if (isBehind(angle, distance)) {
            if (abs(angle) > PI- DELTA) {
                leftPower = -1;
                rightPower = -1;
            } else if (angle > 0) {
                leftPower = -1;
                rightPower = 0.75;
            } else {
                leftPower = 0.75;
                rightPower = -1;
            }
        } else {
            if (angle > DELTA) {         // если угол сильно положительный,
                leftPower = 0.75;
                rightPower = -1;
            } else if (angle < -DELTA) {  // если угол сильно отрицательный,
                leftPower = -1;
                rightPower = 0.75;
            } else {
                leftPower = 1;
                rightPower = 1;
            }
        }

        move.setLeftTrackPower(leftPower);
        move.setRightTrackPower(rightPower);

//        String tmpOut = roundX(self.getX()) + " " + " " + roundX(self.getY()) + " " + rotationAcceleration;
//        if (!tmpOut.equals(tmpOutOld)) {
//            System.out.println(tmpOut);
//            tmpOutOld = tmpOut;
//        }

    }

    public boolean isBehind(Unit unit) {
        double angle = self.getAngleTo(unit);
        double distance = self.getDistanceTo(unit);
        return isBehind(angle, distance);
    }

    public boolean isBehind(double angle, double distance) {
        return abs(angle) > NO_ROTATE && distance < 600*abs(angle)/PI;
    }

    private double roundX(double x) {
        return round(x * 100.0) / 100.0;
    }


    private boolean isBorder() {
        return (self.getX() < self.getHeight()
                || self.getY() < self.getWidth()
                || world.getWidth() < self.getX() + self.getWidth()
                || world.getHeight() < self.getY() + self.getWidth()
        );
    }

    private boolean isInNook() {
        for (Point nook : nooks)
            if (getDistanceTo(nook) < dimention / 2) return true;
        return false;
    }

}
