import java.util.EnumSet;
import java.util.Set;

public class Tile {
    private final int row;
    private final int column;
    private final Set<Direction> walls = EnumSet.allOf(Direction.class);

    public Tile(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean hasWall(Direction direction) {
        return walls.contains(direction);
    }

    void openWall(Direction direction) {
        walls.remove(direction);
    }
}