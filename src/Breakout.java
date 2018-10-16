
import java.awt.HeadlessException;
import javax.swing.JFrame;

/**
 *
 * @author migo
 */
public class Breakout extends JFrame {

    private final Game game = new Game();

    public Breakout() throws HeadlessException {
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setName("Breakout clone");
        setTitle("Breakout Clone");
        addKeyListener(game);
        game.setSize(600,400);
        add(game);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Breakout();
    }

}
