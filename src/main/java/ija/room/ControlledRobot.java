package ija.room;

import java.util.ArrayList;
import java.util.List;

import ija.common.Environment;
import ija.tool.common.Observable;
import ija.tool.common.Position;
import ija.common.Robot;
import ija.tool.common.AbstractObservableRobot;

public class ControlledRobot extends AbstractObservableRobot implements Robot{
    private Environment environment;
    private Position position;
	private Position prevPosition;
    private int angle;
    private static List<Observer> observers = new ArrayList<>();
    private static int notified = 0;

    private ControlledRobot(Environment environment, Position position) {
        this.environment = environment;
        this.position = position;
		this.prevPosition = position;
        this.angle = 0;
		observers = new ArrayList<>();
  
    }

    public static ControlledRobot create(Environment env, Position pos) {
        if (env.containsPosition(pos) && !env.obstacleAt(pos,999) && !env.robotAt(pos,null)) {
            ControlledRobot robot = new ControlledRobot(env, pos);

            if (env.addRobot(robot)) {
                notified = observers.size();
                return robot; 
            }
        }
        return null;
    }

    @Override
    public List<Observable.Observer> getObservers(){
        return observers;
    }

    @Override
    public int notifiedObservers()
    {
        return notified;
    }

    @Override
    public boolean move() {
        int newX = this.position.getCol();
        int newY = this.position.getRow();
        switch (this.angle) {
			case 0:
				newY -=1;
				break;
			case 45:
				newY-=1;
				newX+=1;
				break;
			case 90:
				newX+=1;
				break;
			case 135:
				newX+=1;
				newY+=1;
				break;
			case 180:
				newY+=1;
				break;
			case 225:
				newY+=1;
				newX-=1;
				break;
			case 270:
				newX-=1;
				break;
			case 315:
				newX-=1;
				newY-=1;
				break;
            default:
                return false;
        }
    
        if (environment.containsPosition(new Position(newX, newY)) && !environment.obstacleAt(new Position(newX, newY),this.angle) && !environment.robotAt(new Position(newX, newY),this)) {
            Position old = new Position(this.position.getCol(),this.position.getRow());
			this.prevPosition = old;
			this.position = new Position(newX, newY);
            this.notifyObservers();

            return true; 
        }
        return false;
    }
    
    @Override
    public boolean canMove() {
        int newX = this.position.getCol();
        int newY = this.position.getRow();
        switch (this.angle) {
            case 0:
                newY -=1;
                break;
            case 45:
                newY-=1;
                newX+=1;
                break;
            case 90:
                newX+=1;
                break;
            case 135:
                newX+=1;
                newY+=1;
                break;
            case 180:
                newY+=1;
                break;
            case 225:
                newY+=1;
                newX-=1;
                break;
            case 270:
                newX-=1;
                break;
            case 315:
                newX-=1;
                newY-=1;
                break;
            default:
                return false; 
        }

        return environment.containsPosition(new Position(newX, newY)) && !environment.obstacleAt(new Position(newX, newY),this.angle) && !environment.robotAt(new Position(newX, newY),this);
    }


    @Override
    public void turn(int num) {
        this.angle = (this.angle + (45*num)) % 360;
        this.prevPosition = this.position;
        this.notifyObservers();
    }

    @Override
    public void turn() {
        this.angle = (this.angle + 90) % 360;
        this.prevPosition = this.position;
        this.notifyObservers();
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

	@Override
	public Position getPrevPosition(){
		return this.prevPosition;
	}

    @Override
    public int angle() {
        return this.angle;
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

}


