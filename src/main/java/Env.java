import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.StrictMath.*;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 11:48
 * Всё окружающее
 */
public class Env {
    // адский флаг, показывает, что мы сидим в углу, но можно вращать корпусом
    public boolean inTheCorner = false;
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

        dimention = max(self.getWidth(), self.getHeight())*1.0;

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

    public List<Tank> getTargets() {
        ArrayList<Tank> tmp = new ArrayList<Tank>(Arrays.asList(world.getTanks()));
        ArrayList<Tank> result = new ArrayList<Tank>();
        for(Tank tank : tmp) {
            if (isTarget(tank)){
                result.add(tank);
            }
        }
        return result;
    }

    public boolean isOnTheWay(Unit u1, double x, double y) {
        Point p1 = new Point(x, y);
        double angle = u1.getAngle() + PI / 2;
        double dx = cos(angle)*u1.getWidth()/2.0;
        double dy = sin(angle)*u1.getHeight()/2.0;
        Point p2 = new Point(u1.getX() + dx, u1.getY() + dy);
        Point p3 = new Point(u1.getX() - dx, u1.getY() - dy);
        return !Geo.isObtuse(p1, p2, p3);
    }

    public double getDistanceTo(Point point) {
        return self.getDistanceTo(point.x, point.y);
    }


    public void directMoveTo(double x, double y) {
        double leftPower;
        double rightPower;

        double angle = self.getAngleTo(x, y);
        double distance = self.getDistanceTo(x, y);
        // задний ход
        if (isBehind(angle, distance)) {
            // abs(angle) > PI/2 - тупой угол
            if (abs(angle) > PI- DELTA || distance < 25) { // > 5/6 PI
                leftPower = -1;
                rightPower = -1;
            } else if (angle > 0) {
                leftPower = -1;
                rightPower = 0.75;
            } else {
                leftPower = 0.75;
                rightPower = -1;
            }
        } else { // передний ход
            // пробуем плавно приехать
//            if (abs(angle) < PI/3 && distance > 200 ) {
//                double targetDx = cos(angle);
//                double targetDy = sin(angle);
//                double dx = self.getSpeedX();
//                double dy = self.getSpeedY();
//
//
//            }else
            // едем топорно
            if (angle > DELTA && distance > 25) {         // правее дельты
                leftPower = 0.75;
                rightPower = -1;
            } else if (angle < -DELTA && distance > 25) {  // левее дельты
                leftPower = -1;
                rightPower = 0.75;
            } else { // внутри дельты
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

}
