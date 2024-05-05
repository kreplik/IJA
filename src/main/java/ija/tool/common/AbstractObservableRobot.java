package ija.tool.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a robot
 */
public abstract class AbstractObservableRobot implements ToolRobot {
    protected List<Observer> observers; // List to hold all observers

    /**
     * Constructor for AbstractObservableRobot. Initializes the list of observers
     */
    public AbstractObservableRobot() {
        this.observers = new ArrayList<>();
    }
}
