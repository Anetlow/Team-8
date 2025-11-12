package screen;

import engine.Core;
import engine.DrawManager;
import engine.InputManager;

/**
 * Pause screen display when user presses P.
 */
public class PauseScreen extends Screen {

    private final DrawManager drawManager;
    private final InputManager inputManager;

    public PauseScreen(int width, int height) {
        super(width, height, 60);
        this.drawManager = Core.getDrawManager();
        this.inputManager = Core.getInputManager();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public int run() {
        boolean running = true;

        // Prevent instant unpause
        while (inputManager.isKeyDown(java.awt.event.KeyEvent.VK_P)){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Pause Screen
        while (running) {
            drawManager.initDrawing(this);
            drawManager.drawPauseOverlay(this);
            drawManager.completeDrawing(this);

            // Resume when P is pressed again
            if (inputManager.isKeyDown(java.awt.event.KeyEvent.VK_P)) {
                running = false;
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }
}
