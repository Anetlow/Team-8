package engine.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents portal data from the JSON level configuration.
 */
public class PortalData {
    private int portalId;
    private int targetPortalId;
    private int positionX;
    private int positionY;
    private String color;
    private List<Position> possiblePositions;

    /**
     * Simple position class for possible portal locations.
     */
    public static class Position {
        private int x;
        private int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    /**
     * Constructor for creating from a map (e.g., from JSON).
     * @param map The map containing portal data.
     */
    @SuppressWarnings("unchecked")
    public PortalData(Map<String, Object> map) {
        this.portalId = ((Number) map.get("portalId")).intValue();
        this.targetPortalId = ((Number) map.get("targetPortalId")).intValue();
        this.color = (String) map.get("color");
        
        // Support both old format (single position) and new format (multiple positions)
        if (map.get("possiblePositions") != null) {
            // New format: multiple possible positions
            this.possiblePositions = new ArrayList<>();
            List<Map<String, Object>> positionsList = (List<Map<String, Object>>) map.get("possiblePositions");
            for (Map<String, Object> posMap : positionsList) {
                int x = ((Number) posMap.get("x")).intValue();
                int y = ((Number) posMap.get("y")).intValue();
                this.possiblePositions.add(new Position(x, y));
            }
            // Randomly select one position
            if (!this.possiblePositions.isEmpty()) {
                Position selected = this.possiblePositions.get((int) (Math.random() * this.possiblePositions.size()));
                this.positionX = selected.getX();
                this.positionY = selected.getY();
            }
        } else {
            // Old format: single position (backward compatibility)
            this.positionX = ((Number) map.get("positionX")).intValue();
            this.positionY = ((Number) map.get("positionY")).intValue();
        }
    }

    public int getPortalId() {
        return portalId;
    }

    public int getTargetPortalId() {
        return targetPortalId;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public String getColor() {
        return color;
    }

    public List<Position> getPossiblePositions() {
        return possiblePositions;
    }
}

