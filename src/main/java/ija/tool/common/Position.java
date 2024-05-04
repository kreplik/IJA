package ija.tool.common;

public class Position {
    private final int col;
    private final int row;

    public Position(int col, int row) {
						this.col = col;
						this.row = row;

    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return this.row == position.row && this.col == position.col;
    }

	public boolean validPosition(Position other) {
		int dx = this.col - other.col;
		int dy = this.row - other.row;
		int distanceSquared = dx * dx + dy * dy;
		int maxDistanceSquared = 25 * 25;

		int dx2 = this.col - other.col;
		int dy2 = this.row + 50 - other.row;
		int distanceSquared2 = dx2 * dx2 + dy2 * dy2;

		int dx3 = this.col + 50 - other.col;
		int dy3 = this.row - other.row;
		int distanceSquared3 = dx3 * dx3 + dy3 * dy3;

		int dx4 = this.col + 50 - other.col;
		int dy4 = this.row + 50 - other.row;
		int distanceSquared4 = dx4 * dx4 + dy4 * dy4;

		int min =  Math.min(Math.min(distanceSquared,Math.min(distanceSquared2,distanceSquared3)),distanceSquared4);
		return min > maxDistanceSquared;
	}


    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "[" + row + ", " + col + "]";
    }

}
