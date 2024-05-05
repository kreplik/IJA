package ija.common;
import ija.tool.common.Position;


public class Obstacle {
    private Position position;

    public Obstacle(Environment env, Position pos) {
        this.position = pos;
    }

    public Position getPosition() {
        return this.position;
    }

}
