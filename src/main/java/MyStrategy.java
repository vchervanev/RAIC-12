import model.*;

import java.util.ArrayList;

import static java.lang.StrictMath.*;
import static java.util.Collections.sort;

public final class MyStrategy implements Strategy {

    public static Env env = new Env();

    int id = 0;
//    ArrayList<Long> tankIds = new ArrayList<Long>();
    ArrayList<Action> actions = new ArrayList<Action>();
    {
        actions.add(new ActionBonus());
        actions.add(new ActionFire());
        actions.add(new ActionAim());
    }

    @Override
    public void move(Tank self, World world, Move move) {
        env.init(self, world, move);

        Unit unit = getNearestBonus();
//        if (unit == null) {
//            unit = getTank(0);
//        }
        //directMoveTo(unit);

//        analyzeAccelerate();
//        analyzeRotation();

        //move.setTurretTurn(PI);
        //move.setFireType(FireType.PREMIUM_PREFERRED);

        for(Action action : actions) {
            action.estimate();
        }
        sort(actions);

        int index = 0;
        for(Action action : actions) {
            if (index++ == 0){
                action.perform();
            } else {
                action.tryPerformSecondary();
            }
        }

    }

    private void analyzeRotation() {
        env.move.setLeftTrackPower(.75);
        env.move.setRightTrackPower(-1);
        double angle = env.self.getAngle();
        if (angle < 0)
            angle = 2*PI + angle;
        System.out.println(String.format("%s\t%s\t%s", id++, angle/2/PI*360, env.self.getCrewHealth()));
    }

    private void analyzeAccelerate() {
//        if (id < 100) {
//            id++;
//            return;
//        }
        env.move.setLeftTrackPower(1);
        env.move.setRightTrackPower(1);
        System.out.println(String.format("%d\t%f\t%f\t%f\t%f\t%d",
                id++,
                env.self.getX(), env.self.getY(),
                env.self.getSpeedX(), env.self.getSpeedY(),
                env.self.getCrewHealth()));
    }


    @Override
    public TankType selectTank(int tankIndex, int teamSize) {
        return TankType.MEDIUM;
    }


    public Bonus getNearestBonus() {
        Bonus result = null;
        double distance = 0;
        for(Bonus bonus : env.world.getBonuses()) {
            double newDistance = env.self.getDistanceTo(bonus);
            if (result == null || distance > newDistance) {
                distance = newDistance;
                result = bonus;
            }
        }
        return result;
    }
}
