package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

/**
 * Implements a portal entity that can teleport the player ship.
 * 
 * @author Team-8
 */
public class Portal extends Entity {

    /** Portal ID to identify which portal this is (for pairing). */
    private int portalId;
    /** Target portal ID (the portal this one teleports to). */
    private int targetPortalId;
    /** Cooldown to prevent immediate re-teleportation. */
    private long lastTeleportTime;
    /** Minimum time between teleportations (in milliseconds). */
    private static final long TELEPORT_COOLDOWN = 1000;

    /**
     * Constructor, establishes the portal's properties.
     * 
     * @param positionX
     *            Initial position of the portal in the X axis.
     * @param positionY
     *            Initial position of the portal in the Y axis.
     * @param portalId
     *            Unique ID for this portal.
     * @param targetPortalId
     *            ID of the target portal to teleport to.
     * @param color
     *            Color of the portal.
     */
    public Portal(final int positionX, final int positionY, final int portalId, 
                  final int targetPortalId, final Color color) {
        super(positionX, positionY, 16, 16, color);
        this.portalId = portalId;
        this.targetPortalId = targetPortalId;
        this.lastTeleportTime = 0;
        // Use EnemyShipSpecial sprite temporarily until Portal sprite is added to graphics file
        this.spriteType = SpriteType.EnemyShipSpecial;
    }

    /**
     * Getter for the portal ID.
     * 
     * @return Portal ID.
     */
    public int getPortalId() {
        return portalId;
    }

    /**
     * Getter for the target portal ID.
     * 
     * @return Target portal ID.
     */
    public int getTargetPortalId() {
        return targetPortalId;
    }

    /**
     * Checks if the portal can be used (cooldown expired).
     * 
     * @return True if the portal can be used.
     */
    public boolean canTeleport() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastTeleportTime) >= TELEPORT_COOLDOWN;
    }

    /**
     * Marks that a teleportation just occurred.
     */
    public void markTeleportUsed() {
        this.lastTeleportTime = System.currentTimeMillis();
    }

    /**
     * Updates the portal's state (for animation or effects).
     */
    public void update() {
        // Portal can have animation logic here if needed
    }
}

