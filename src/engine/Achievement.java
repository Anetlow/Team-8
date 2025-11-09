package engine;

import java.awt.Color;

/**
 * Represents a single achievement in the game.
 */
public class Achievement {
    /** The name of the achievement. */
    private String name;
    /** A description of how to earn the achievement. */
    private String description;
    /** The unlock status of the achievement. */
    private boolean unlocked;
    /** The reward color for this achievement (null if no reward). */
    private Color rewardColor;

    /**
     * Constructor for the Achievement.
     *
     * @param name        The name of the achievement.
     * @param description A description of the achievement.
     */
    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.unlocked = false;
        this.rewardColor = null;
    }

    /**
     * Constructor for the Achievement with a reward color.
     *
     * @param name        The name of the achievement.
     * @param description A description of the achievement.
     * @param rewardColor The color reward for unlocking this achievement.
     */
    public Achievement(String name, String description, Color rewardColor) {
        this.name = name;
        this.description = description;
        this.unlocked = false;
        this.rewardColor = rewardColor;
    }

    /**
     * Gets the name of the achievement.
     *
     * @return The name of the achievement.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the achievement.
     *
     * @return The description of the achievement.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if the achievement is unlocked.
     *
     * @return True if the achievement is unlocked, false otherwise.
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * Unlocks the achievement.
     */
    public void unlock() {
        this.unlocked = true;
    }

    /**
     * Gets the reward color for this achievement.
     *
     * @return The reward color, or null if there is no reward.
     */
    public Color getRewardColor() {
        return rewardColor;
    }

    /**
     * Checks if this achievement has a reward.
     *
     * @return True if this achievement has a reward color.
     */
    public boolean hasReward() {
        return rewardColor != null;
    }
}
