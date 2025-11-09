package engine;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages ship colors unlocked through gacha system.
 * Colors are stored in session only (not persistent).
 * 
 * @author Gacha System Team
 */
public class ShipColorManager {
    
    /** Singleton instance. */
    private static ShipColorManager instance;
    
    /** Set of unlocked colors. */
    private Set<Color> unlockedColors;
    
    /** Currently selected color for the ship. */
    private Color selectedColor;
    
    /** Default color (green). */
    public static final Color DEFAULT_COLOR = Color.GREEN;
    
    /** Available colors for gacha. */
    public static final Color[] GACHA_COLORS = {
        Color.RED,
        Color.BLUE,
        Color.CYAN,
        Color.MAGENTA,
        Color.ORANGE,
        Color.PINK,
        Color.YELLOW,
        Color.WHITE,
        new Color(255, 20, 147), // Deep Pink
        new Color(0, 255, 127),  // Spring Green
        new Color(138, 43, 226), // Blue Violet
        new Color(255, 165, 0),  // Orange
        new Color(0, 191, 255),  // Deep Sky Blue
        new Color(255, 215, 0),  // Gold
        new Color(50, 205, 50)   // Lime Green
    };
    
    /** Color names for display. */
    public static final String[] COLOR_NAMES = {
        "Red",
        "Blue",
        "Cyan",
        "Magenta",
        "Orange",
        "Pink",
        "Yellow",
        "White",
        "Deep Pink",
        "Spring Green",
        "Blue Violet",
        "Orange Red",
        "Deep Sky Blue",
        "Gold",
        "Lime Green"
    };
    
    /**
     * Private constructor for singleton.
     */
    private ShipColorManager() {
        this.unlockedColors = new HashSet<Color>();
        this.selectedColor = DEFAULT_COLOR;
        // Add default color to unlocked colors
        this.unlockedColors.add(DEFAULT_COLOR);
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return ShipColorManager instance.
     */
    public static ShipColorManager getInstance() {
        if (instance == null) {
            instance = new ShipColorManager();
        }
        return instance;
    }
    
    /**
     * Resets all unlocked colors (for new session).
     */
    public static void reset() {
        instance = new ShipColorManager();
    }
    
    /**
     * Unlocks a color.
     * 
     * @param color Color to unlock.
     */
    public void unlockColor(final Color color) {
        unlockedColors.add(color);
    }
    
    /**
     * Checks if a color is unlocked.
     * 
     * @param color Color to check.
     * @return True if unlocked.
     */
    public boolean isColorUnlocked(final Color color) {
        return unlockedColors.contains(color);
    }
    
    /**
     * Gets all unlocked colors.
     * 
     * @return Set of unlocked colors.
     */
    public Set<Color> getUnlockedColors() {
        return new HashSet<Color>(unlockedColors);
    }
    
    /**
     * Sets the selected color for the ship.
     * 
     * @param color Color to select.
     */
    public void setSelectedColor(final Color color) {
        if (unlockedColors.contains(color)) {
            this.selectedColor = color;
        }
    }
    
    /**
     * Gets the currently selected color.
     * 
     * @return Selected color.
     */
    public Color getSelectedColor() {
        return selectedColor;
    }
    
    /**
     * Gets a random color from the gacha pool.
     * 
     * @return Random color.
     */
    public Color getRandomGachaColor() {
        int index = (int) (Math.random() * GACHA_COLORS.length);
        return GACHA_COLORS[index];
    }
    
    /**
     * Gets the name of a color.
     * 
     * @param color Color to get name for.
     * @return Color name.
     */
    public static String getColorName(final Color color) {
        for (int i = 0; i < GACHA_COLORS.length; i++) {
            if (GACHA_COLORS[i].equals(color)) {
                return COLOR_NAMES[i];
            }
        }
        if (DEFAULT_COLOR.equals(color)) {
            return "Green";
        }
        return "Unknown";
    }
    
    /**
     * Gets the index of a color in the gacha pool.
     * 
     * @param color Color to find.
     * @return Index or -1 if not found.
     */
    public static int getColorIndex(final Color color) {
        for (int i = 0; i < GACHA_COLORS.length; i++) {
            if (GACHA_COLORS[i].equals(color)) {
                return i;
            }
        }
        return -1;
    }
}

