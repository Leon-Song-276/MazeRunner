public enum Direction {
    UP('w', -1, 0),
    DOWN('s', 1, 0),
    LEFT('a', 0, -1),
    RIGHT('d', 0, 1);

    private final char input;
    private final int rowOffset;
    private final int columnOffset;

    Direction(char input, int rowOffset, int columnOffset) {
        this.input = input;
        this.rowOffset = rowOffset;
        this.columnOffset = columnOffset;
    }

    public char getInput() {
        return input;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public int getColumnOffset() {
        return columnOffset;
    }

    public Direction opposite() {
        if (this == UP) return DOWN;
        if (this == DOWN) return UP;
        if (this == LEFT) return RIGHT;
        return LEFT;
    }

    public static Direction fromInput(char input) {
        char normalized = Character.toLowerCase(input);
        for (Direction direction : values()) {
            if (direction.input == normalized) {
                return direction;
            }
        }
        return null;
    }
}
