import model.Tank;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 04.11.12
 * Time: 17:43
 * Экшен ныканья по углам
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
        if ((enemyCount == 5 || (enemyCount == 4 && totalDistance <  1200))) {
            variant = Variant.hide;
        } else {
            variant = Variant.none;
        }
    }

    @Override
    public void perform() {
        env.moveToCorner();
    }
}
