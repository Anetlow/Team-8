package screen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.ShipColorManager;
import entity.Bullet;
import entity.BulletPool;
import entity.Entity;
import entity.Ship;

/**
 * Duel PvP screen: two human players face each other with three lives.
 */
public class PvPScreen extends Screen {

    private static final int MAX_LIVES = 3;
    private static final int ROUND_END_DELAY = 2000;

    /** Player at the bottom (uses P1 controls). */
    private Ship playerBottom;
    /** Player at the top (uses P2 controls). */
    private Ship playerTop;

    private int livesBottom = MAX_LIVES;
    private int livesTop = MAX_LIVES;

    private Set<Bullet> bullets;

    private boolean matchOver;
    private String winnerText;
    private Cooldown roundEndCooldown;

    /** Vertical boundaries so each ship stays in its half. */
    private int topZoneBottom;
    private int bottomZoneTop;

    /**
     * Constructor.
     */
    public PvPScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        this.returnCode = 1; // Return to title screen when finished.
    }

    @Override
    public void initialize() {
        super.initialize();
        this.bullets = new HashSet<>();
        this.matchOver = false;
        this.roundEndCooldown = Core.getCooldown(ROUND_END_DELAY);

        // Define the halves of the arena.
        this.topZoneBottom = this.height / 2 - 40;
        this.bottomZoneTop = this.height / 2 + 40;

        ShipColorManager colorManager = ShipColorManager.getInstance();

        this.playerBottom = new Ship(this.width / 2 - 26, this.height - 100, colorManager.getSelectedColor());
        this.playerBottom.setPlayerId(1);
        this.playerBottom.setShootsUpwards(true);

        this.playerTop = new Ship(this.width / 2 - 26, 80, Color.PINK);
        this.playerTop.setPlayerId(2);
        this.playerTop.setShootsUpwards(false); // Fire downwards.
        
        // Reset shooting cooldowns to prevent immediate shots when entering PvP mode
        // This is done by attempting a shoot (which will reset the cooldown) but the inputDelay
        // will prevent any actual shooting until the delay is finished
    }

    @Override
    public int run() {
        super.run();
        return this.returnCode;
    }

    @Override
    protected void update() {
        super.update();

        if (matchOver) {
            draw();
            if (this.roundEndCooldown.checkFinished()) {
                this.isRunning = false;
            }
            return;
        }

        // Only handle input after the input delay to prevent accidental shots when entering PvP mode
        boolean readyForInput = this.inputDelay.checkFinished();
        if (readyForInput) {
            handlePlayerInput();
        }
        updateBullets();
        draw();
    }

    private void handlePlayerInput() {
        // Bottom player (P1)
        if (this.livesBottom > 0 && !this.playerBottom.isDestroyed()) {
            boolean right = inputManager.isP1KeyDown(KeyEvent.VK_D);
            boolean left = inputManager.isP1KeyDown(KeyEvent.VK_A);
            boolean up = inputManager.isP1KeyDown(KeyEvent.VK_W);
            boolean down = inputManager.isP1KeyDown(KeyEvent.VK_S);
            boolean fire = inputManager.isP1KeyDown(KeyEvent.VK_SPACE);

            if (right && this.playerBottom.getPositionX() + this.playerBottom.getWidth() + this.playerBottom.getSpeed() < this.width - 10) {
                this.playerBottom.moveRight();
            }
            if (left && this.playerBottom.getPositionX() - this.playerBottom.getSpeed() > 10) {
                this.playerBottom.moveLeft();
            }
            if (up && this.playerBottom.getPositionY() - this.playerBottom.getSpeed() > this.bottomZoneTop) {
                this.playerBottom.moveUp();
            }
            if (down && this.playerBottom.getPositionY() + this.playerBottom.getHeight() + this.playerBottom.getSpeed() < this.height - 30) {
                this.playerBottom.moveDown();
            }
            if (fire) {
                this.playerBottom.shoot(this.bullets);
            }
        }

        // Top player (P2)
        if (this.livesTop > 0 && !this.playerTop.isDestroyed()) {
            boolean right = inputManager.isP2KeyDown(KeyEvent.VK_RIGHT);
            boolean left = inputManager.isP2KeyDown(KeyEvent.VK_LEFT);
            boolean up = inputManager.isP2KeyDown(KeyEvent.VK_UP);
            boolean down = inputManager.isP2KeyDown(KeyEvent.VK_DOWN);
            boolean fire = inputManager.isP2KeyDown(KeyEvent.VK_ENTER);

            if (right && this.playerTop.getPositionX() + this.playerTop.getWidth() + this.playerTop.getSpeed() < this.width - 10) {
                this.playerTop.moveRight();
            }
            if (left && this.playerTop.getPositionX() - this.playerTop.getSpeed() > 10) {
                this.playerTop.moveLeft();
            }
            if (up && this.playerTop.getPositionY() - this.playerTop.getSpeed() > 30) {
                this.playerTop.moveUp();
            }
            if (down && this.playerTop.getPositionY() + this.playerTop.getHeight() + this.playerTop.getSpeed() < this.topZoneBottom) {
                this.playerTop.moveDown();
            }
            if (fire) {
                this.playerTop.shoot(this.bullets);
            }
        }

        this.playerBottom.update();
        this.playerTop.update();
    }

    private void updateBullets() {
        Set<Bullet> recyclable = new HashSet<>();

        for (Bullet bullet : this.bullets) {
            bullet.update();

            if (bullet.getPositionY() < 0 || bullet.getPositionY() > this.height) {
                recyclable.add(bullet);
                continue;
            }

            Integer owner = bullet.getOwnerId();
            if (owner != null && owner == 1) {
                // Shot by bottom player; can hit top.
                if (this.livesTop > 0 && !this.playerTop.isDestroyed() && checkCollision(bullet, this.playerTop)) {
                    handleHitOnTop(bullet, recyclable);
                }
            } else if (owner != null && owner == 2) {
                if (this.livesBottom > 0 && !this.playerBottom.isDestroyed() && checkCollision(bullet, this.playerBottom)) {
                    handleHitOnBottom(bullet, recyclable);
                }
            }
        }

        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);
    }

    private void handleHitOnTop(final Bullet bullet, final Set<Bullet> recyclable) {
        if (!this.playerTop.isInvincible()) {
            this.playerTop.destroy();
            this.livesTop--;
            if (this.livesTop <= 0) {
                declareWinner("Player 1 gagne !");
            }
        }
        if (!bullet.penetration()) {
            recyclable.add(bullet);
        }
    }

    private void handleHitOnBottom(final Bullet bullet, final Set<Bullet> recyclable) {
        if (!this.playerBottom.isInvincible()) {
            this.playerBottom.destroy();
            this.livesBottom--;
            if (this.livesBottom <= 0) {
                declareWinner("Player 2 gagne !");
            }
        }
        if (!bullet.penetration()) {
            recyclable.add(bullet);
        }
    }

    private void declareWinner(final String text) {
        if (this.matchOver) return;
        this.winnerText = text;
        this.matchOver = true;
        this.roundEndCooldown.reset();
    }

    private void draw() {
        drawManager.initDrawing(this);

        // Arena divider.
        drawManager.drawHorizontalLine(this, this.height / 2);

        if (this.livesBottom > 0) {
            drawManager.drawEntity(this.playerBottom, this.playerBottom.getPositionX(), this.playerBottom.getPositionY());
        }

        if (this.livesTop > 0) {
            drawManager.drawEntity(this.playerTop, this.playerTop.getPositionX(), this.playerTop.getPositionY());
        }

        for (Bullet bullet : this.bullets) {
            drawManager.drawEntity(bullet, bullet.getPositionX(), bullet.getPositionY());
        }

        drawManager.drawLives(this, this.livesBottom);
        drawManager.drawLivesP2(this, this.livesTop);

        drawManager.drawCenteredRegularString(this, "Mode PvP - WASD + SPACE vs. Fleches + ENTREE", 40);

        if (this.matchOver && this.winnerText != null) {
            drawManager.drawGameOver(this, false, false);
            drawManager.drawCenteredRegularString(this, this.winnerText, this.getHeight() / 2 + 60);
        }

        drawManager.completeDrawing(this);
    }

    private boolean checkCollision(final Entity a, final Entity b) {
        int centerAX = a.getPositionX() + a.getWidth() / 2;
        int centerAY = a.getPositionY() + a.getHeight() / 2;
        int centerBX = b.getPositionX() + b.getWidth() / 2;
        int centerBY = b.getPositionY() + b.getHeight() / 2;

        int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
        int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;

        int distanceX = Math.abs(centerAX - centerBX);
        int distanceY = Math.abs(centerAY - centerBY);

        return distanceX < maxDistanceX && distanceY < maxDistanceY;
    }
}


