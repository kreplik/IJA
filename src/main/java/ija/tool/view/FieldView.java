package ija.tool.view;

import ija.tool.common.Position;

import javax.swing.*;
import java.awt.*;



public class FieldView extends JPanel implements ComponentView {
    private boolean hasRobot;
    private boolean hasObstacle;
    private Graphics graphics;
    private int angle;
	private Position  pos;
	private Color color;
    

    public FieldView( Position pos) {
        this.hasObstacle = false;
        setPreferredSize(new Dimension(50, 50)); // Set preferred size for the field view

        this.graphics = getGraphics();
								this.pos = pos;
								this.hasRobot = false;
        this.angle = 0;
								this.color = Color.GREEN;
    }

				public Position getPos(){
					return this.pos;
				}


    public void setHasObstacle(boolean hasObstacle) {
        this.hasObstacle = hasObstacle;
        repaint(); // Repaint the field view when the obstacle status changes
    }

	public void setHasRobot(boolean hasRobot, int angle,int lol) {
		this.hasRobot = hasRobot;
		this.angle = angle;
		if(lol == 0){
			this.color = Color.GREEN;
		}
		else if(lol == 1){
			this.color = Color.ORANGE;
		}else{
            this.color = Color.WHITE;
        }

		repaint(); // Repaint the field view when the obstacle status changes
	}

    public Graphics getGraphic()
				{
        return this.graphics;
    }



	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the field with different colors based on presence of robot and obstacle
        if (this.hasObstacle) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
		else if (this.hasRobot){
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(this.color);
			g.fillOval(0, 0, 50, 50);
            if(this.angle == 0){
                g.setColor(Color.RED); 
                g.fillOval(15, 0, 20, 20);
            }else if(this.angle == 45){
                g.setColor(Color.RED); 
                g.fillOval(getWidth() - 20, 0, 20, 20);
            }else if(this.angle == 90){
                g.setColor(Color.RED); 
                g.fillOval(getWidth() - 20, (getHeight() - 20) / 2, 20, 20);
            }else if(this.angle == 135){
                g.setColor(Color.RED); 
                g.fillOval(getWidth() - 20, getHeight() - 20, 20, 20);
            }else if(this.angle == 180){
                g.setColor(Color.RED); 
                g.fillOval((getWidth() - 20) / 2, getHeight() - 20, 20, 20);
            }else if(this.angle == 225){
                g.setColor(Color.RED); 
                g.fillOval(0, getHeight() - 20, 20, 20);
            }else if(this.angle == 270){
                g.setColor(Color.RED); 
                g.fillOval(0, (getHeight() - 20) / 2, 20, 20);
            }else if(this.angle == 315){
                g.setColor(Color.RED);
                g.fillOval(0, 0, 20, 20);
            }
        }else {
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }


}
