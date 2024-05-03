package ija.common;
import ija.tool.common.Position;
import ija.tool.common.ToolEnvironment;

public interface Environment extends ToolEnvironment{
    boolean addRobot(Robot robot);
    boolean createObstacleAt(int col, int row);
    boolean obstacleAt(int row, int col);
    boolean obstacleAt(Position p);
    boolean robotAt(Position p);
    boolean containsPosition(Position pos);
}
