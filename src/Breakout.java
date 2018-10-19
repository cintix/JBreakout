
import engine.Game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import sprites.Atlas;
import sprites.SpriteImage;
import sprites.SpritesSheet;

/**
 *
 * @author migo
 */
public class Breakout extends Game {

    private int color = 0;
    private boolean invert;
    private int imageIndex = 0;
    private Atlas images;
    
    private BufferedImage spriteMap;
    
    public Breakout() {
        super(600, 400, "Breakout clone");

        try {
            images = SpritesSheet.readSprites("Sheets/Breakout_Tile_Free.xml");
            spriteMap = ImageIO.read(new File("Sheets/Breakout_Tile_Free.png"));
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
        
        setFPS(30);
        start();
    }

    public static void main(String[] args) {
        new Breakout();
    }

    @Override
    public void updateGameLogic() {

        if (invert) {
            color--;
        } else {
            color++;
        }

        if (color > 254) {
            invert = true;
        }
        if (color == 0) {
            invert = false;
        }

        
        if (imageIndex == images.getSubtexture().size()) imageIndex = 0;

    }

    @Override
    public void renderGame(Graphics2D graphics2D) {
        graphics2D.setColor(new Color(color, color, color));
        graphics2D.fillRect(0, 0, 600, 400);
        
        
        SpriteImage spriteImage = images.getSubtexture().get(imageIndex);  
        BufferedImage subimage = spriteMap.getSubimage(spriteImage.getX(), spriteImage.getY(), spriteImage.getWidth(), spriteImage.getHeight());
        
        graphics2D.drawImage(subimage.getScaledInstance(spriteImage.getWidth()/4, spriteImage.getHeight()/4, Image.SCALE_SMOOTH), 20, 20, null);
        
        imageIndex++;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
