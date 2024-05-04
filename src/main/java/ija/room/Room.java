package ija.room;

import ija.common.Environment;
import ija.tool.common.Position;
import ija.common.Robot;
import ija.common.Obstacle;
import ija.tool.common.ToolRobot;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class Room implements Environment {
    private int width;
    private int height;
    private Set<Obstacle> obstacles;
    private Set<Robot> robots;
    public  List<Robot> autoRobots;

    private Room(int width, int height) {
        this.width = width;
        this.height = height;
        this.obstacles = new HashSet<>();
        this.robots = new HashSet<>();
        this.autoRobots = new ArrayList<>();
    }

    public static Room create(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Room dimensions must be greater than zero.");
        }
        return new Room(width, height);
    }

    @Override
    public boolean createObstacleAt(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y <= height) {
            Position obstaclePosition = new Position(x, y);
            Obstacle obstacle = new Obstacle(this,obstaclePosition);
			for(Obstacle obs : obstacles){
				if(obs.getPosition().equals(obstaclePosition))
				{
					return false;
				}
			}
			System.out.println(obstacle + " has been created on position: <" + obstacle.getPosition().getCol() + "," + obstacle.getPosition().getRow() + ">");

									return obstacles.add(obstacle);
        }
      return false;
    }

    @Override
    public boolean obstacleAt(int x, int y) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getPosition().getRow() == y && obstacle.getPosition().getCol() == x) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean obstacleAt(Position p) {
        for (Obstacle obstacle : obstacles) {
            if (!obstacle.getPosition().validPosition(p)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean addRobot(Robot robot) {
        if (robot != null && !this.robots.contains(robot) && !obstacleAt(robot.getPosition()) && containsPosition(robot.getPosition())) {
            return this.robots.add(robot);
        }
        return false;
    }



    @Override
    public boolean containsPosition(Position pos) {
        return pos.getRow() >= 0 && pos.getRow() < height && pos.getCol() >= 0 && pos.getCol() < width;
    }

    @Override
    public boolean robotAt(Position pos) {
        final double radius = 25; // Radius of the robot
        for (Robot robot : this.robots) {
            if (robot != this && robot.getPosition().isNear(pos, radius)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int rows(){
        return this.height;
    }

    @Override
    public int cols(){
        return this.width;
    }

    @Override
    public List<ToolRobot> robots() {
        List<ToolRobot> robotList = new ArrayList<>();
        robotList.addAll(this.robots);
        return robotList;
    }

    @Override
    public List<Robot> autorobots() {
        List<Robot> robotList = new ArrayList<>();
        robotList.addAll(this.autoRobots);
        return robotList;
    }

    @Override
    public Set<Robot> getList() {
        return this.robots;
    }

				@Override
				public Set<Obstacle> getObstacles(){return this.obstacles;}

}






