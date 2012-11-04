import model.Tank;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 04.11.12
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class ActionHide extends Action {
    @Override
    public void estimate() {
        int enemyCount = 0;
        int totalDistance = 0;
        for(Tank tank : env.world.getTanks()) {
            if (env.isTarget(tank)) {
                enemyCount++;
                totalDistance += env.self.getDistanceTo(tank);
            }
        }
        if ((enemyCount == 2 || (enemyCount == 4 && totalDistance <  1200)) && env.self.getDistanceTo(0,0) > 80) {
            variant = Variant.hide;
        }
    }

    @Override
    public void perform() {
        env.directMoveTo(0,0);
    }
}
