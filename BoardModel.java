import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BoardModel implements Board {
    public static final int SIZE = 10;

    private final Tile[][] board;

    public BoardModel() {
        this(new Random());
    }

    BoardModel(Random random) {
        board = new Tile[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                board[row][column] = new Tile(row, column);
            }
        }

        carveMaze(random);
        board[SIZE - 1][SIZE - 1].openWall(Direction.DOWN);
        if (!isCompletable()) {
            throw new IllegalStateException("Generated maze has no path to the exit");
        }
    }

    private void carveMaze(Random random) {
        boolean[][] visited = new boolean[SIZE][SIZE];
        List<Tile> path = new ArrayList<>();
        Tile start = board[0][0];
        path.add(start);
        visited[0][0] = true;

        while (!path.isEmpty()) {
            Tile current = path.get(path.size() - 1);
            List<Direction> available = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                int nextRow = current.getRow() + direction.getRowOffset();
                int nextColumn = current.getColumn() + direction.getColumnOffset();
                if (isInBounds(nextRow, nextColumn) && !visited[nextRow][nextColumn]) {
                    available.add(direction);
                }
            }

            if (available.isEmpty()) {
                path.remove(path.size() - 1);
                continue;
            }

            Direction direction = available.get(random.nextInt(available.size()));
            int nextRow = current.getRow() + direction.getRowOffset();
            int nextColumn = current.getColumn() + direction.getColumnOffset();
            Tile next = board[nextRow][nextColumn];
            current.openWall(direction);
            next.openWall(direction.opposite());
            visited[nextRow][nextColumn] = true;
            path.add(next);
        }
    }

    private boolean isInBounds(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }

    @Override
    public Tile[][] getBoard() {
        Tile[][] copy = new Tile[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(board[row], 0, copy[row], 0, SIZE);
        }
        return copy;
    }

    @Override
    public Tile getTile(int row, int column) {
        if (!isInBounds(row, column)) {
            throw new IndexOutOfBoundsException("Tile position is outside the board");
        }
        return board[row][column];
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public boolean isCompletable() {
        if (board[SIZE - 1][SIZE - 1].hasWall(Direction.DOWN)) {
            return false;
        }
        return depthFirstSearch(0, 0, new boolean[SIZE][SIZE]);
    }

    private boolean depthFirstSearch(int row, int column, boolean[][] visited) {
        if (row == SIZE - 1 && column == SIZE - 1) {
            return true;
        }
        visited[row][column] = true;

        Tile current = board[row][column];
        for (Direction direction : Direction.values()) {
            int nextRow = row + direction.getRowOffset();
            int nextColumn = column + direction.getColumnOffset();
            if (!isInBounds(nextRow, nextColumn) || visited[nextRow][nextColumn]
                    || current.hasWall(direction)
                    || board[nextRow][nextColumn].hasWall(direction.opposite())) {
                continue;
            }
            if (depthFirstSearch(nextRow, nextColumn, visited)) {
                return true;
            }
        }
        return false;
    }
}