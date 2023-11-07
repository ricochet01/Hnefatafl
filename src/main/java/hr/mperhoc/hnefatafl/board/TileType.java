package hr.mperhoc.hnefatafl.board;

public enum TileType {
    NORMAL(0, 0x888888, false),
    ATTACK_SPAWN(1, 0x7e0021, false),
    DEFENDER_SPAWN(2, 0x00487f, false),
    ESCAPE(3, 0x008000, true),
    THRONE(4, 0xff950e, true),
    EMPTY(5, 0x000000, false);

    private final int id;
    // Color of the tile
    private final String color, darkerColor;
    private final boolean hostile;

    TileType(int id, int color, boolean hostile) {
        this.id = id;

        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;

        this.color = String.format("%02x%02x%02x", r, g, b);
        this.darkerColor = String.format("%02x%02x%02x", r / 2, g / 2, b / 2);
        this.hostile = hostile;
    }

    public final int getId() {
        return id;
    }

    public final String getColor() {
        return color;
    }

    // Used to achieve a hover effect
    public final String getDarkerColor() {
        return darkerColor;
    }

    public final boolean isHostile() {
        return hostile;
    }

    public static TileType fromId(int id) {
        for (TileType type : values()) {
            if (type.id == id) return type;
        }

        throw new RuntimeException("Invalid Id!");
    }
}
