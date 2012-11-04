/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 03.11.12
 * Time: 22:14
 * To change this template use File | Settings | File Templates.
 */
public class RandomMove extends Action {

    @Override
    public void estimate() {
        variant = Variant.none;
    }

    @Override
    public void perform() {
        env.rotateToAngle(0);
        //directMoveTo(env.self.getHeight(),env.self.getHeight());
    }
}
