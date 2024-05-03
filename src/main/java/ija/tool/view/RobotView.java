package ija.tool.view;

import javax.swing.*;

import ija.tool.EnvPresenter;
import ija.tool.common.Observable;
import ija.tool.common.Position;
import ija.tool.common.ToolRobot;

import java.awt.*;

public class RobotView extends JPanel implements ComponentView,Observable.Observer{

    private int diameter = 50; // Průměr kolečka
			private Position position;
			private EnvPresenter envPresenter;
			private FieldView fieldView;
			private boolean isVisible;
			private ToolRobot toolRobot;


    public RobotView(EnvPresenter envPresenter, ToolRobot toolRobot) {
        // Nastavení velikosti panelu
        setPreferredSize(new Dimension(diameter, diameter));
								toolRobot.addObserver(this);
								this.envPresenter = envPresenter;
								this.isVisible = false;
								this.toolRobot = toolRobot;
								this.position = toolRobot.getPosition();

    }

							public void setVisible(boolean isVisible) {
								this.isVisible = isVisible;
								envPresenter.robotViewMap.get(this.position);
								repaint(); // Repaint the robot view when the visibility changes
							}

							@Override
							protected void paintComponent(Graphics g) {
								super.paintComponent(g);

								// Draw the robot as a red circle if it's visible
								if (isVisible) {
									int diameter = Math.min(getWidth(), getHeight()); // Use the smaller dimension as the diameter
									int x = (getWidth() - diameter) / 2; // Calculate x-coordinate to center the oval
									int y = (getHeight() - diameter) / 2;
									g.setColor(Color.RED);
									g.fillOval(this.position.getCol(), this.position.getRow(), getWidth(), getHeight());
								}
							}

    @Override
    public void update(Observable o)
    {
					if(o instanceof ToolRobot) {
						ToolRobot robot = (ToolRobot) o;
						fieldView = envPresenter.fieldAt(robot.getPosition());
						this.position = robot.getPosition();
						this.setVisible(true);
					}

					//this.envPresenter.paintComponent(getGraphics());
    }



}
