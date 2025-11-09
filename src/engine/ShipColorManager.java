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
    
    /** Available colors for achievements (different from gacha colors). */
    public static final Color[] ACHIEVEMENT_COLORS = {
        new Color(255, 0, 255),  // Magenta (Vibrant)
        new Color(0, 255, 255),  // Cyan (Bright)
        new Color(255, 140, 0),  // Dark Orange
        new Color(148, 0, 211),  // Dark Violet
        new Color(255, 20, 20),  // Bright Red
        new Color(0, 250, 154),  // Medium Spring Green
        new Color(255, 105, 180), // Hot Pink
        new Color(75, 0, 130)    // Indigo
    };
    
    /** Achievement color names for display. */
    public static final String[] ACHIEVEMENT_COLOR_NAMES = {
        "Vibrant Magenta",
        "Bright Cyan",
        "Dark Orange",
        "Dark Violet",
        "Bright Red",
        "Spring Green",
        "Hot Pink",
        "Indigo"
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
        // Check gacha colors
        for (int i = 0; i < GACHA_COLORS.length; i++) {
            if (GACHA_COLORS[i].equals(color)) {
                return COLOR_NAMES[i];
            }
        }
        // Check achievement colors
        for (int i = 0; i < ACHIEVEMENT_COLORS.length; i++) {
            if (ACHIEVEMENT_COLORS[i].equals(color)) {
                return ACHIEVEMENT_COLOR_NAMES[i];
            }
        }
        // Check default color
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
    
    /**
     * Gets a list of all available colors (gacha + achievement + default).
     * 
     * @return List of all colors.
     */
    public static java.util.List<Color> getAllAvailableColors() {
        java.util.List<Color> allColors = new java.util.ArrayList<Color>();
        allColors.add(DEFAULT_COLOR);
        for (Color color : GACHA_COLORS) {
            allColors.add(color);
        }
        for (Color color : ACHIEVEMENT_COLORS) {
            allColors.add(color);
        }
        return allColors;
    }
    
    /**
     * Checks if a color is from the achievement pool.
     * 
     * @param color Color to check.
     * @return True if it's an achievement color.
     */
    public static boolean isAchievementColor(final Color color) {
        for (Color achievementColor : ACHIEVEMENT_COLORS) {
            if (achievementColor.equals(color)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a color is from the gacha pool.
     * 
     * @param color Color to check.
     * @return True if it's a gacha color.
     */
    public static boolean isGachaColor(final Color color) {
        for (Color gachaColor : GACHA_COLORS) {
            if (gachaColor.equals(color)) {
                return true;
            }
        }
        return false;
    }
}

