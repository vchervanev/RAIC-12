import model.*;

import java.util.ArrayList;

import static java.lang.StrictMath.*;

public final class MyStrategy implements Strategy {

    boolean init = true;
    Move move = null;
    Move oldMove = null;
    World world;
    Tank self;
    ArrayList<Long> tankIds = new ArrayList<Long>();
    @Override
    public void move(Tank self, World world, Move move) {
        init(self, world, move);

        Unit unit = getNearestBonus();
        if (unit == null) {
            unit = getTank(0);
        }
        directMoveTo(unit);
        //move.setTurretTurn(PI);
        //move.setFireType(FireType.PREMIUM_PREFERRED);

    }

    private Tank getTank(int index) {
        long id = tankIds.get(index);
        for(Tank tank : world.getTanks()) {
            if (id == tank.getId()) {
                return tank;
            }
        }
        return null;
    }

    private void init(Tank self, World world, Move move) {
        this.oldMove = this.move;
        this.move = move;
        this.self = self;
        this.world = world;

        if (!init)
            return;

        for(Tank tank : world.getTanks()) {
            if (tank.getId() != self.getId()) {
                tankIds.add(tank.getId());
            }
        }
        init = false;
    }

    @Override
    public TankType selectTank(int tankIndex, int teamSize) {
        return TankType.MEDIUM;
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

    public Bonus getNearestBonus() {
        Bonus result = null;
        double distance = 0;
        for(Bonus bonus : world.getBonuses()) {
            double newDistance = self.getDistanceTo(bonus);
            if (result == null || distance > newDistance) {
                distance = newDistance;
                result = bonus;
            }
        }
        return result;
    }
}
