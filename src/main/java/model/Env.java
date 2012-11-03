package model;

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


}
