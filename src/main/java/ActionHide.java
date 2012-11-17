import model.Tank;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 04.11.12
 * Time: 17:43
 * Экшен ныканья по углам
 */
public class ActionHide extends Action {

    enum CornerStatus { Free, Occupied, Friendly};

    public Point[] nooks = new Point[4];
    private double minDist;
    private Point minNook;
    CornerStatus cornerStatus;

    @Override
    public void estimate() {
        env.inTheCorner = false;
        nooks[0] = new Point(env.dimention, env.dimention);
        nooks[1] = new Point(env.world.getWidth() - env.dimention, env.dimention);
        nooks[2] = new Point(env.dimention, env.world.getHeight() - env.dimention);
        nooks[3] = new Point(env.world.getWidth() - env.dimention, env.world.getHeight() - env.dimention);

        int enemyCount = env.getTargets().size();
        if ( enemyCount >= 4 ) {
            variant = Variant.hide;
        } else {
            variant = Variant.none;
            return;
        }

        minDist = 0;
        minNook = null;
        for (Point nook : nooks) {
            double dist = env.getDistanceTo(nook);
            if (dist < minDist || minNook == null) {
                minDist = dist;
                minNook = nook;
            }
        }

        if (minDist > 150)
            return;

        // проверка, что угол занят
        cornerStatus = CornerStatus.Free;


        for(Tank tank : env.world.getTanks()) {
            if (tank.getId() != env.self.getId()) {
                if (tank.getDistanceTo(minNook.x, minNook.y) < minDist) {
                    if (tank.isTeammate()) {
                        cornerStatus = CornerStatus.Friendly;
                    } else {
                        cornerStatus = CornerStatus.Occupied;
                        break;
                    }
                }
            }
        }

        if (cornerStatus == CornerStatus.Occupied) {
            minNook.x = minNook.x < env.world.getWidth()/2 ? 200 : env.world.getWidth() - 200;
            minNook.y = minNook.y < env.world.getHeight()/2 ? 50 : env.world.getHeight() - 50;
        } else if (cornerStatus == CornerStatus.Friendly) {
            variant = Variant.none;
        }


    }

    @Override
    public void perform() {
        moveToCorner();
    }

    public void moveToCorner() {
        if (minDist > env.dimention / 2) {
            env.inTheCorner = false;
            env.directMoveTo(minNook.x, minNook.y);
        }
        env.inTheCorner = true;
        if (cornerStatus == CornerStatus.Free) {
             // если что - разворот перекроет боевая стойка
            // ищем координаты ближайшего угла, чтобы встать к нему "задом"
            double x = env.self.getX() < env.world.getWidth()/2 ? 0 : env.world.getWidth();
            double y = env.self.getY() < env.world.getHeight()/2 ? 0: env.world.getHeight();
            double angleToNook = env.self.getAngleTo(x, y);

            if (abs(angleToNook) < PI - env.DELTA) {
                if (angleToNook < 0) {
                    env.move.setLeftTrackPower(0.75);
                    env.move.setRightTrackPower(-1);
                } else {
                    env.move.setLeftTrackPower(-1);
                    env.move.setRightTrackPower(0.75);
                }
            }
        }
    }



}
