package sprites;

/**
 *
 * @author migo
 */
public class SpriteImage {

    private String name;
    private int x;
    private int y;
    private int height;
    private int width;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "SpriteImage{" + "name=" + name + ", x=" + x + ", y=" + y + ", height=" + height + ", width=" + width + '}';
    }

}
