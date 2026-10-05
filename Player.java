public class Player {
	private int row;
	private int column;

	public Player(int startRow, int startColumn) {
		row = startRow;
		column = startColumn;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public void move(Direction direction) {
		row += direction.getRowOffset();
		column += direction.getColumnOffset();
	}
}