package engine;


import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author migo
 */
public abstract class Game extends Thread implements KeyListener, Runnable {

    private final GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private final Canvas canvas;
    private final JFrame frame;
    private BufferStrategy strategy;
    private final BufferedImage background;
    private Graphics2D backgroundGraphics;
    private Graphics2D graphics;
    
    private final int height;
    private final int width;
    private int FPS = 30;
    private volatile boolean running = true;
    private long renderTime;
    private int scale = 1;

    public Game(int width, int height, String title) throws HeadlessException {
        super(title);
        this.height = height;
        this.width = width;
        frame = new JFrame(title);
        frame.addWindowListener(new FrameClose());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);        
        frame.setSize(width * scale, height * scale);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Canvas
        canvas = new Canvas(graphicsConfiguration);
        canvas.setSize(width * scale, height * scale);
        frame.add(canvas, 0);

        // Background & Buffer
        background = create(width, height, false);
        canvas.createBufferStrategy(2);
        do {
            strategy = canvas.getBufferStrategy();
        } while (strategy == null);

    }

    public abstract void updateGameLogic();

    public abstract void renderGame(Graphics2D graphics2D);

    public final BufferedImage create(final int width, final int height, final boolean alpha) {
        return graphicsConfiguration.createCompatibleImage(width, height, alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getFPS() {
        return FPS;
    }

    public void setFPS(int FPS) {
        this.FPS = FPS;
    }

    public long getRenderTime() {
        return renderTime;
    }

    public void setRenderTime(long renderTime) {
        this.renderTime = renderTime;
    }

    @Override
    public void run() {
        backgroundGraphics = (Graphics2D) background.getGraphics();
        long fpsWait = (long) (1.0 / getFPS() * 1000);
        main:
        while (isRunning()) {
            long renderStart = System.nanoTime();
            updateGameLogic();
            do {
                Graphics2D bg = getBuffer();
                if (!isRunning()) {
                    break main;
                }

                renderGame(backgroundGraphics);

                if (scale != 1) {
                    bg.drawImage(background, 0, 0, width * scale, height * scale, 0, 0, width, height, null);
                } else {
                    bg.drawImage(background, 0, 0, null);
                }
                bg.dispose();
            } while (!updateScreen());

            // Better do some FPS limiting here
            renderTime = (System.nanoTime() - renderStart) / 1000000;
            try {
                Thread.sleep(Math.max(0, fpsWait - renderTime));
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
            renderTime = (System.nanoTime() - renderStart) / 1000000;

        }
        frame.dispose();
    }

    private Graphics2D getBuffer() {
        if (graphics == null) {
            try {
                graphics = (Graphics2D) strategy.getDrawGraphics();
            } catch (IllegalStateException e) {
                return null;
            }
        }
        return graphics;
    }

    private boolean updateScreen() {
        graphics.dispose();
        graphics = null;
        try {
            strategy.show();
            Toolkit.getDefaultToolkit().sync();
            return (!strategy.contentsLost());
        } catch (NullPointerException | IllegalStateException e) {
            return true;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private class FrameClose extends WindowAdapter {

        @Override
        public void windowClosing(final WindowEvent e) {
            running = false;
        }
    }

}
