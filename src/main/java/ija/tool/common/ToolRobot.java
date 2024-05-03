package ija.tool.common;

import java.util.List;

public interface ToolRobot extends Observable{
	int angle();
	Position getPosition();
	void turn(int num);
	List<Observable.Observer> getObservers();
	int notifiedObservers();

	boolean move();
}
