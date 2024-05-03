package ija.tool.common;
import ija.common.Obstacle;
import ija.common.Robot;

import java.util.List;
import java.util.Set;

public interface ToolEnvironment {
				int rows();
				int cols();
				boolean obstacleAt(Position p);

				boolean robotAt(Position p);
				List<ToolRobot> robots();
				List<Robot> autorobots();
				Set<Robot> getList();
				Set<Obstacle> getObstacles();
}
