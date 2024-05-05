package ija.tool.common;

public class Position {
    private final int col;
    private final int row;

    public Position(int col, int row) {
								this.col = col;
								this.row = row;

    }

	/**
		* @return Returns object's row number
		*/
	public int getRow() {
        return this.row;
    }

	/**
		* @return Returns object's column number
		*/
    public int getCol() {
        return this.col;
    }

	/**
		* Compares two objects
		* @param o Object to compare with
		* @return result of comparison
		*/
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return this.row == position.row && this.col == position.col;
    }

	/**
		* Checks if there is a robot in area
		* @param other Robot's next position
		* @param radius Robot's radius
		* @return true, if there is another robot
		*/
	public boolean isNear(Position other, double radius) {
        double dx = this.col - other.col;
        double dy = this.row - other.row;
        return Math.sqrt(dx * dx + dy * dy) <= radius + radius;
    }

	/**
		* Checks if robot's next position is valid
		* @param other Robot's position
		* @param angle Robot's angle
		* @return true, if position is valid
		*/
	public boolean validPosition(Position other,int angle) {
					// Distance between obstacle and robot
		int dx = this.col - other.col;
		int dy = this.row - other.row;


		// Check new position according to robot's angle
		switch (angle){
			case 0:
				if(this.col < other.col) {
					if (Math.abs(dx) > 75) {
						return true;
					}	else {
						return this.row < other.row - 75 || this.row > other.row -25;
					}
				}

				if(this.col > other.col){
					if(Math.abs(dx) > 25){
						return true;
					}
					else {
						return this.row < other.row - 75 || this.row > other.row -25;
					}
				}

			case 90:
				if(this.row < other.row) {
					if (Math.abs(dy) > 75) {
						return true;
					}	else {
						return this.col > other.col + 25 || this.col < other.col -25;
					}
				}

				if(this.row > other.row){
					if(Math.abs(dy) > 25){
						return true;
					}
					else {
						return this.col > other.col + 25 || this.col < other.col -25;
					}
				}
				break;
			case 180:
				if(this.col < other.col) {
					if (Math.abs(dx) > 75) {
						return true;
					}	else {
						return this.row > other.row + 25 || this.row < other.row -25;
					}
				}

				if(this.col > other.col){
					if(Math.abs(dx) > 25){
						return true;
					}
					else {
						return this.row > other.row + 25 || this.row < other.row -25;
					}
				}
			case 270:
				if(this.row < other.row) {
					if (Math.abs(dy) > 75) {
						return true;
					}	else {
						return this.col < other.col - 75 || this.col > other.col + 25;
					}
				}

				if(this.row > other.row){
					if(Math.abs(dy) > 25){
						return true;
					}
					else {
						return this.col < other.col - 75 || this.col > other.col + 25;
					}
				}

		}
		return true;
	}


    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "[" + col + ", " + row + "]";
    }

}
