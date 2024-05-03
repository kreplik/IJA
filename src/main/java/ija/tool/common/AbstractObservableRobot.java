package ija.tool.common;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractObservableRobot implements ToolRobot{
    public List<Observer> observers;

    public AbstractObservableRobot() {
        this.observers = new ArrayList<>();
    
    }
    
}
