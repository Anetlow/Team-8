package screen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import engine.Core;
import engine.Cooldown;
import engine.ShipColorManager;

/**
 * Implements the customization screen where players can select their ship color.
 */
public class CustomizationScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;

    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;

    /** Currently selected color index. */
    private int selectedIndex;

    /** List of unlocked colors. */
    private List<Color> unlockedColorsList;

    /**
     * Constructor for the CustomizationScreen.
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second.
     */
    public CustomizationScreen(int width, int height, int fps) {
        super(width, height, fps);
        this.returnCode = 1; // Default return code
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        updateUnlockedColorsList();
        
        // Set selected index to currently selected color
        ShipColorManager colorManager = ShipColorManager.getInstance();
        Color currentColor = colorManager.getSelectedColor();
        this.selectedIndex = findColorIndex(currentColor);
    }

    /**
     * Updates the list of unlocked colors.
     */
    private void updateUnlockedColorsList() {
        ShipColorManager colorManager = ShipColorManager.getInstance();
        Set<Color> unlockedColors = colorManager.getUnlockedColors();
        this.unlockedColorsList = new ArrayList<Color>(unlockedColors);
    }

    /**
     * Finds the index of a color in the unlocked colors list.
     *
     * @param color Color to find.
     * @return Index of the color, or 0 if not found.
     */
    private int findColorIndex(Color color) {
        for (int i = 0; i < unlockedColorsList.size(); i++) {
            if (unlockedColorsList.get(i).equals(color)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Initializes the screen elements.
     */
    @Override
    public void initialize() {
        super.initialize();
        updateUnlockedColorsList();
    }

    /**
     * Runs the screen's main loop.
     *
     * @return The screen's return code.
     */
    @Override
    public int run() {
        super.run();
        return this.returnCode;
    }

    /**
     * Updates the screen's state.
     */
    @Override
    protected void update() {
        super.update();
        draw();
        
        boolean readyForInput = this.selectionCooldown.checkFinished()
                && this.inputDelay.checkFinished();

        if (readyForInput) {
            if (unlockedColorsList.isEmpty()) {
                // No colors unlocked, only allow exit
                if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                    this.isRunning = false;
                }
                return;
            }

            // Navigate up
            if (inputManager.isKeyDown(KeyEvent.VK_UP) || inputManager.isKeyDown(KeyEvent.VK_W)) {
                if (selectedIndex > 0) {
                    selectedIndex--;
                    audio.SoundManager.play("sfx/menu_select.wav");
                } else {
                    selectedIndex = unlockedColorsList.size() - 1;
                    audio.SoundManager.play("sfx/menu_select.wav");
                }
                this.selectionCooldown.reset();
            }
            // Navigate down
            else if (inputManager.isKeyDown(KeyEvent.VK_DOWN) || inputManager.isKeyDown(KeyEvent.VK_S)) {
                if (selectedIndex < unlockedColorsList.size() - 1) {
                    selectedIndex++;
                    audio.SoundManager.play("sfx/menu_select.wav");
                } else {
                    selectedIndex = 0;
                    audio.SoundManager.play("sfx/menu_select.wav");
                }
                this.selectionCooldown.reset();
            }
            // Select color
            else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                ShipColorManager colorManager = ShipColorManager.getInstance();
                Color selectedColor = unlockedColorsList.get(selectedIndex);
                colorManager.setSelectedColor(selectedColor);
                audio.SoundManager.play("sfx/menu_select.wav");
                this.selectionCooldown.reset();
            }
            // Exit
            else if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                this.isRunning = false;
            }
        }
    }

    /**
     * Draws the customization screen.
     */
    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawCustomizationScreen(this, unlockedColorsList, selectedIndex);
        drawManager.completeDrawing(this);
    }
}

