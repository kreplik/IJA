package ija.common;

public class Position {
    private int col;
    private int row;

    public Position(int col, int row) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }


    @Override
    public int hashCode() {
        int result = 31 * row + col;
        return result;
    }

    @Override
    public String toString() {
        return "[" + row + ", " + col + "]";
    }
}



