package screen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import engine.Cooldown;
import engine.Core;
import engine.GameState;
import engine.ShipColorManager;

/**
 * Implements the gacha screen where players can roll for ship colors.
 * 
 * @author Gacha System Team
 */
public class GachaScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;

    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;

    /** Current game state containing player's coin balance. */
    private GameState gameState;

    /** Price for one gacha roll. */
    private static final int GACHA_PRICE = 100;

    /** Ship color manager. */
    private ShipColorManager colorManager;

    /** Current roulette state. */
    private boolean isSpinning;
    
    /** Roulette animation variables. */
    private int rouletteIndex;
    private int targetIndex; // The final result index
    private int spinFrames;
    private static final int SPIN_DURATION = 120; // frames for spin animation
    private static final int SPIN_SLOWDOWN_START = 80; // when to start slowing down
    
    /** Result color after spin. */
    private Color resultColor;
    private String resultMessage;
    private Cooldown resultDisplayCooldown;
    private static final int RESULT_DISPLAY_TIME = 3000;

    /** Indicates if the gacha was opened between levels (true) or from the main menu (false) */
    public boolean betweenLevels;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param gameState
     *            Current game state with player's coin balance.
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     * @param betweenLevels
     *            Whether opened between levels or from menu.
     */
    public GachaScreen(final GameState gameState, final int width,
                      final int height, final int fps,
                      final boolean betweenLevels) {
        super(width, height, fps);

        this.gameState = gameState;
        this.betweenLevels = betweenLevels;
        this.colorManager = ShipColorManager.getInstance();

        // If opened between levels : back to game, otherwise : back to menu
        this.returnCode = betweenLevels ? 2 : 1;

        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();

        this.isSpinning = false;
        this.rouletteIndex = 0;
        this.targetIndex = 0;
        this.spinFrames = 0;
        this.resultColor = null;
        this.resultMessage = "";
        this.resultDisplayCooldown = Core.getCooldown(RESULT_DISPLAY_TIME);

        this.logger.info("Gacha screen initialized with " +
                gameState.getCoin() + " coins. BetweenLevels=" + betweenLevels);
    }

    /**
     * Starts the action.
     *
     * @return Next screen code (1 = main menu, 2 = game).
     */
    public final int run() {
        super.run();
        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        // Update roulette animation
        if (isSpinning) {
            updateRoulette();
        }

        draw();

        if (this.selectionCooldown.checkFinished()
                && this.inputDelay.checkFinished()) {
            handleInput();
        }
    }

    /**
     * Updates the roulette animation.
     */
    private void updateRoulette() {
        spinFrames++;
        
        if (spinFrames < SPIN_SLOWDOWN_START) {
            // Fast spin - change every frame
            rouletteIndex = (rouletteIndex + 1) % ShipColorManager.GACHA_COLORS.length;
        } else if (spinFrames < SPIN_DURATION) {
            // Slow down - gradually approach target
            int slowdownFrames = spinFrames - SPIN_SLOWDOWN_START;
            int framesRemaining = SPIN_DURATION - spinFrames;
            
            // Calculate distance to target (wrapping around)
            int distanceToTarget = (targetIndex - rouletteIndex + ShipColorManager.GACHA_COLORS.length) 
                    % ShipColorManager.GACHA_COLORS.length;
            
            // Slow down based on frames remaining
            int slowFactor = Math.max(2, (slowdownFrames / 3) + 2);
            
            if (framesRemaining <= distanceToTarget && distanceToTarget > 0) {
                // In final frames, move directly toward target
                if (spinFrames % 2 == 0) {
                    rouletteIndex = (rouletteIndex + 1) % ShipColorManager.GACHA_COLORS.length;
                }
            } else if (spinFrames % slowFactor == 0) {
                // Continue spinning but slower
                rouletteIndex = (rouletteIndex + 1) % ShipColorManager.GACHA_COLORS.length;
            }
        } else {
            // Ensure we're exactly on target
            rouletteIndex = targetIndex;
            // Spin finished
            finishSpin();
        }
    }

    /**
     * Finishes the spin and unlocks the color.
     */
    private void finishSpin() {
        isSpinning = false;
        // Ensure rouletteIndex is exactly on target
        rouletteIndex = targetIndex;
        resultColor = ShipColorManager.GACHA_COLORS[rouletteIndex];
        
        boolean wasUnlocked = colorManager.isColorUnlocked(resultColor);
        colorManager.unlockColor(resultColor);
        colorManager.setSelectedColor(resultColor);
        
        if (wasUnlocked) {
            resultMessage = "You got " + ShipColorManager.getColorName(resultColor) + "! (Already owned)";
        } else {
            resultMessage = "NEW COLOR: " + ShipColorManager.getColorName(resultColor) + "!";
        }
        
        resultDisplayCooldown.reset();
        logger.info("Gacha roll result: " + ShipColorManager.getColorName(resultColor) + 
                " (Unlocked: " + !wasUnlocked + ")");
    }

    /**
     * Handles user input.
     */
    private void handleInput() {
        // Cannot interact during spin
        if (isSpinning) {
            return;
        }

        // Roll gacha
        if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
            rollGacha();
            this.selectionCooldown.reset();
        }

        // Exit
        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
            this.isRunning = false;
        }
    }

    /**
     * Performs a gacha roll.
     */
    private void rollGacha() {
        // Check if player has enough coins
        if (gameState.getCoin() < GACHA_PRICE) {
            resultMessage = "Not enough coins! Need " + GACHA_PRICE + " coins.";
            resultDisplayCooldown.reset();
            resultColor = null; // Clear result color when error
            isSpinning = false; // Make sure not spinning
            logger.info("Not enough coins for gacha. Need " + GACHA_PRICE +
                    ", have " + gameState.getCoin());
            return;
        }

        // Deduct coins
        gameState.deductCoins(GACHA_PRICE);

        // Set random result
        targetIndex = (int) (Math.random() * ShipColorManager.GACHA_COLORS.length);
        
        // Start roulette animation from a random position
        rouletteIndex = (int) (Math.random() * ShipColorManager.GACHA_COLORS.length);
        isSpinning = true;
        spinFrames = 0;
        resultColor = null; // Clear previous result
        resultMessage = ""; // Clear previous message
        
        logger.info("Starting gacha roll. Target: " + ShipColorManager.getColorName(
                ShipColorManager.GACHA_COLORS[targetIndex]) + ". Remaining coins: " + gameState.getCoin());
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        // Determine if we should show the roulette
        // Show roulette only if spinning or if we have a result to show
        boolean showRoulette = isSpinning || (resultColor != null && !resultDisplayCooldown.checkFinished());
        
        drawManager.drawGachaScreen(this, gameState.getCoin(), GACHA_PRICE,
                isSpinning, rouletteIndex, resultColor, resultMessage,
                !resultDisplayCooldown.checkFinished(), showRoulette);

        drawManager.completeDrawing(this);
    }

    /**
     * Gets the current roulette index (for drawing).
     * 
     * @return Current roulette index.
     */
    public int getRouletteIndex() {
        return rouletteIndex;
    }

    /**
     * Gets whether the roulette is spinning.
     * 
     * @return True if spinning.
     */
    public boolean isSpinning() {
        return isSpinning;
    }
}

