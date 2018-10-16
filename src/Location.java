
import java.awt.Rectangle;

/**
 *
 * @author migo
 */
public class Location extends Rectangle {

    public Location() {
    }

    public Location(Rectangle rctngl) {
        super(rctngl);
    }

    public Location(int i, int i1, int i2, int i3) {
        super(i, i1, i2, i3);
    }

    public Location(int i, int i1) {
        super(i, i1);
    }
    
    public boolean isOverlappingLocation(Location location) {
        return x < location.x + location.width && x + width > location.x && y < location.y + location.height && y + height > location.y;
    }

    @Override
    public String toString() {
        return "Location{" +  super.toString() + '}';
    }
    
    
}
