
import engine.Location;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author migo
 */
public class SpaceBar {

    private Location location;
    private boolean sticky;

    public Location getLocation() {
        return location;
    }

    public RoundRectangle2D getShape() {
        return new RoundRectangle2D.Float(location.x, location.y, location.width, location.height, 15, 15);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    @Override
    public String toString() {
        return "SpaceBar{" + "location=" + location + ", sticky=" + sticky + '}';
    }

}
