public interface Board {
    Tile[][] getBoard();

    Tile getTile(int row, int column);

    int getSize();

    boolean isCompletable();
}
