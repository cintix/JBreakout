
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author migo
 */
public class Game extends JComponent implements KeyListener {

    private final BufferedImage bufferedImage = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
    private final Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
    int barSpeed = 30;

    private final SpaceBar bar = new SpaceBar();

    public Game() {
        Location location = new Location();
        location.width = (120);
        location.height = (18);
        location.x = (int) ((600 / 2) - (location.getWidth() / 2));
        location.y = (int) (330);

        bar.setLocation(location);

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D screen = (Graphics2D) g;

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setPaint(Color.BLACK);
        graphics.fillRect(0, 0, getWidth(), getHeight());

        /**
         * Draw bricks
         *
         */
        // Draw bar
        graphics.setPaint(new Color(102,178,255));
        graphics.fill(bar.getShape());
        graphics.setPaint(new Color (173,201,229) );
        graphics.draw(bar.getShape());
        //Update screen
        screen.drawImage(bufferedImage, 0, 0, 600, 400, null);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {

        Location location = bar.getLocation();

        if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
            System.out.println("Left");
            location.x -= barSpeed;
            if (location.x < 0) {
                location.x = 0;
            }
        }

        if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
            System.out.println("RIGHT");
            location.x += barSpeed;
            if (location.x > (600 - bar.getShape().getWidth())) {
                location.x = (int) (600 - bar.getShape().getWidth());
            }
        }

        bar.setLocation(location);
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

}
