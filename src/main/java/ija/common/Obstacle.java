package ija.common;
import ija.tool.common.Position;

import java.awt.*;
import javafx.scene.shape.Rectangle;

public class Obstacle {
    private Environment environment;
    private Position position;

				private Rectangle rectangle;

    public Obstacle(Environment env, Position pos) {
        this.environment = env;
        this.position = pos;
								this.rectangle = new Rectangle(this.position.getCol(),this.position.getRow(),50,50);
    }

    public Position getPosition() {
        return this.position;
    }
				public Rectangle getRectangle(){return this.rectangle;}
}
