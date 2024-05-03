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


    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "[" + row + ", " + col + "]";
    }


}
