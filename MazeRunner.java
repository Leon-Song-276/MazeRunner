public final class MazeRunner {
    private Board board;
    private Player player;
    private boolean gameOver;
    private boolean escaped;
    private boolean gameActive;

    public MazeRunner() {
        generateMaze();
    }

    public boolean playMove(Direction direction) {
        if (!gameActive || gameOver) {
            return false;
        }
        Tile current = board.getTile(player.getRow(), player.getColumn());
        if (current.hasWall(direction)) {
            return false;
        }

        int nextRow = player.getRow() + direction.getRowOffset();
        int nextColumn = player.getColumn() + direction.getColumnOffset();
        if (nextRow < 0 || nextRow >= board.getSize() || nextColumn < 0 || nextColumn >= board.getSize()) {
            escaped = true;
            return true;
        }

        player.move(direction);
        return true;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean hasPlayerEscaped() {
        return escaped;
    }

    public Player getPlayer() {
        return player;
    }

    public Board getBoard() {
        return board;
    }

    public void restart() {
        startNewGame();
    }

    public void startNewGame() {
        generateMaze();
        gameActive = true;
        gameOver = false;
    }

    public void generateNextMaze() {
        if (gameActive && !gameOver) {
            generateMaze();
        }
    }

    public void finishGame() {
        gameActive = false;
        gameOver = true;
    }

    public boolean isGameActive() {
        return gameActive && !gameOver;
    }

    private void generateMaze() {
        board = new BoardModel();
        player = new Player(0, 0);
        escaped = false;
    }
}