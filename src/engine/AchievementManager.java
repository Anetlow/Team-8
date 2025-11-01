package engine;

import screen.GameScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all game achievements (including their state, unlocking logic, and persistence).
 */
public class AchievementManager {
    /** Stores the single instance of the AchievementManager. */
    private static AchievementManager instance;
    /** Stores the current screen **/
    private GameScreen currentScreen;
    /** List of all achievements in the game. */
    private List<Achievement> achievements;
    /** Counter for the total number of shots fired by the player. */
    private int shotsFired = 0;
    /** Counter for the total number of shots that hit an enemy. */
    private int shotsHit = 0;
    /** Flag to ensure the 'First Blood' achievement is unlocked only once. */
    private boolean firstKillUnlocked = false;
    /** Flag to ensure the 'Bad Sniper' achievement is unlocked only once. */
    private boolean sniperUnlocked = false;
    /** Flag to ensure the 'Bear Grylls' achievement is unlocked only once. */
    private boolean survivorUnlocked = false;

    /** Private constructor (singleton pattern). */
    private AchievementManager() {
        achievements = new ArrayList<>();
        achievements.add(new Achievement("Beginner", "Clear level 1"));
        achievements.add(new Achievement("Intermediate", "Clear level 3"));
        achievements.add(new Achievement("Boss Slayer", "Defeat a boss"));
        achievements.add(new Achievement("Mr. Greedy", "Have more than 2000 coins"));
        achievements.add(new Achievement("First Blood", "Defeat your first enemy"));
        achievements.add(new Achievement("Bear Grylls", "Survive for 60 seconds"));
        achievements.add(new Achievement("Bad Sniper", "Under 80% accuracy"));
        achievements.add(new Achievement("Conqueror", "Clear the final level"));

        loadAchievements();
    }

    public void setCurrentScreen(GameScreen screen) {
        this.currentScreen = screen;
    }

    /** Singleton access */
    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    private String recentlyUnlocked = null;

    public void unlockAchievement(String name) {
        for (Achievement achievement : achievements) {
            if (achievement.getName().equals(name)) {
                // Always set recentlyUnlocked, even if already unlocked
                recentlyUnlocked = name;

                // Only unlock and save if not already unlocked
                if (!achievement.isUnlocked()) {
                    achievement.unlock();
                    saveAchievements();
                }

                // Show the popup directly via GameScreen
                if (currentScreen != null) {
                    currentScreen.showAchievement(name);
                }

                break;
            }
        }
    }

    public String getRecentlyUnlocked() {
        String temp = recentlyUnlocked;
        recentlyUnlocked = null; // clear after reading
        return temp;
    }

    /** Called when an enemy is defeated */
    public void onEnemyDefeated() {
        if (!firstKillUnlocked) {
            unlockAchievement("First Blood");
            firstKillUnlocked = true;
        }

        shotsHit++;

        if (!sniperUnlocked && shotsFired > 5) {
            double accuracy = (shotsHit / (double) shotsFired) * 100.0;
            if (accuracy <= 80.0) {
                unlockAchievement("Bad Sniper");
                sniperUnlocked = true;
            }
        }
    }

    /** Called periodically to track elapsed time */
    public void onTimeElapsedSeconds(int elapsedSeconds) {
        if (!survivorUnlocked && elapsedSeconds >= 60) {
            unlockAchievement("Bear Grylls");
            survivorUnlocked = true;
        }
    }

    /** Called whenever a shot is fired */
    public void onShotFired() {
        shotsFired++;
    }

    /** Load achievements from file */
    public void loadAchievements() {
        try {
            java.util.Map<String, Boolean> unlockedStatus = Core.getFileManager().loadAchievements();
            for (Achievement achievement : achievements) {
                if (unlockedStatus.getOrDefault(achievement.getName(), false)) {
                    achievement.unlock();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load achievement file! Creating a new one.");
            saveAchievements();
        }
    }

    /** Save achievements to file */
    private void saveAchievements() {
        try {
            Core.getFileManager().saveAchievements(achievements);
        } catch (IOException e) {
            System.err.println("Failed to save achievement file!");
            e.printStackTrace();
        }
    }
}