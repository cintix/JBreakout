
/**
 *
 * @author migo
 */
public enum Direction {

    UP(1), DOWN(2), LEFT(4), RIGHT(8);

    private final int value;

    private Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
   
    public static boolean goingInDirection(int directions, Direction direction) {
        return ((directions & direction.value) == direction.value);
    }
}
