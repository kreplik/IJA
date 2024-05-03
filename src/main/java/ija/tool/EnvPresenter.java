package ija.tool;

import ija.common.Environment;
import ija.common.Obstacle;
import ija.common.Robot;
import ija.example.Main;
import ija.room.ControlledRobot;
import ija.room.Room;
import ija.tool.common.Observable;
import ija.tool.common.ToolEnvironment;
import ija.tool.common.ToolRobot;
import ija.tool.view.FieldView;
import ija.tool.common.Position;
import ija.tool.view.RobotView;

import javax.swing.*;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class EnvPresenter extends Application implements Observable.Observer,MouseListener {
	private ToolEnvironment environment;
	public Map<Position, FieldView> fieldViews;

	public Map<Position, RobotView> robotViewMap;
	public List<ToolRobot> robots;
	private ToolRobot controlledRobot;
    public Set<Robot> autorobots;

	private GridPane mainPanel;
	private Pane navbar;
	private Button addObstacleButton;

	private static Map<String, Map<String, Integer>> parameters;
	private Set<Obstacle> obstacles;

	public EnvPresenter() {
		this.fieldViews = new HashMap<>();
		this.robotViewMap = new HashMap<>();
		this.autorobots = new HashSet<>();
	}


	// Declare other UI elements as needed

	@Override
	public void start(Stage primaryStage) throws Exception{



		Main.ParametersLoader loader = new Main.ParametersLoader("data/parameters.txt");
		try {
			parameters = loader.loadParameters();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Environment room = Room.create(parameters.get("GameField").get("width"), parameters.get("GameField").get("height"));
		this.robots = room.robots();

		this.environment = room;
		this.obstacles = room.getObstacles();

		primaryStage.setTitle("IJA GAME");

		BorderPane root = new BorderPane();
		root.setStyle("-fx-background-color: #192a40;");
		primaryStage.setScene(new Scene(root, room.cols(), room.rows()));

		GridPane mainPanel = new GridPane();
		root.setCenter(mainPanel);

		try {
			Image backgroundImage = new Image("/data/background.jpg");
			BackgroundImage background = new BackgroundImage(backgroundImage,
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
			root.setBackground(new Background(background));
		}
		catch (Exception ignored)	{
		}


		int autoRobotsIndex = 0;
		for(Map.Entry<String, Map<String, Integer>> entry : parameters.entrySet())
		{
			if(entry.getKey().contains("Obstacle")) {

				room.createObstacleAt(entry.getValue().get("positionX"), entry.getValue().get("positionY"));

				System.out.println("Obstacle has been created on position: <" +  entry.getValue().get("positionX") + "," + entry.getValue().get("positionY") + ">");
			}

			if(entry.getKey().contains("Robot")){

				Position p1 = new Position(entry.getValue().get("positionX"), entry.getValue().get("positionY"));

				this.autorobots.add(ControlledRobot.create(room, p1));


				System.out.println("Autonomous Robot has been created on position: <" +  entry.getValue().get("positionX") + "," + entry.getValue().get("positionY") + ">");
			}
		}

		for(Obstacle obstacle : this.obstacles){
			Position pos = new Position(obstacle.getPosition().getCol(),obstacle.getPosition().getRow());
			Rectangle rectangle = new Rectangle(pos.getCol(),pos.getRow(),50,50);
			rectangle.setFill(Color.BLACK);
			root.getChildren().add(rectangle);
		}

		for(Robot robot : this.autorobots){
			Position pos = new Position(robot.getPosition().getCol(),robot.getPosition().getRow());
			Circle circle = new Circle(pos.getCol(),pos.getRow(),25,Color.RED);

			root.getChildren().add(circle);
		}

		FlowPane navbar = new FlowPane();
		navbar.setPadding(new Insets(10,10,10,10));
		navbar.setHgap(10);

		Button addObstacle = new Button("Add Obstacle");
		addObstacle.setOnAction(e -> {
			// Create input components
			TextField positionInput = new TextField();
			Label positionLabel = new Label("Enter obstacle position as \"x,y\":");
			Button confirmButton = new Button("Confirm");

			// Add action to confirm button
			confirmButton.setOnAction(event -> {
				String obstaclePosition = positionInput.getText();
				String[] positionParameters = obstaclePosition.split(",");
				Position position = new Position(Integer.parseInt(positionParameters[0]), Integer.parseInt(positionParameters[1]));
				Environment env = (Environment) environment;
				if (!env.createObstacleAt(position.getCol(), position.getRow())) {
					// Show error message
				}
			});

			// Create a container to hold input components
			VBox container = new VBox(positionLabel, positionInput, confirmButton);

			// Create a new stage for the container and show it
			Stage stage = new Stage();
			stage.setScene(new Scene(container));
			stage.show();
		});


		navbar.getChildren().add(addObstacle);

		// Add other buttons to the navbar and set their event handlers similarly

		root.setBottom(navbar);

		primaryStage.show();
	}


	@Override
	public void update(Observable o) {

		/*
	    if (o instanceof ToolRobot) {
	        for (int i = 0; i < environment.rows(); i++) {
	            for (int j = 0; j < environment.cols(); j++) {
	                Position newPosition = new Position(j, i);
	                FieldView fieldView = fieldAt(newPosition);
	                boolean foundRobot = false;
	                int color = 1; 

	                for (ToolRobot toolRobot : this.robots) {
	                    if (toolRobot.getPosition().equals(newPosition)) {
	                        int angle = toolRobot.angle();

	                        if (this.controlledRobot != null && this.controlledRobot.equals(toolRobot)) {
	                            color = 2; 
	                        }
	                        else if (isRobotInAutoRobots(toolRobot, this.autorobots)) {
	                            color = 0;
	                        }
	                        fieldView.setHasRobot(environment.robotAt(newPosition), angle, color);
	                        foundRobot = true;
	                        break; 
	                    }
	                }

	                if (!foundRobot) {
	                    fieldView.setHasRobot(false, 0, color); 
	                }

	                fieldView.setHasObstacle(environment.obstacleAt(newPosition));
	            }
	        }
	    }
	    */
		 return;
	}

/*
	public void open() {



		// Create the main JFrame
		JFrame frame = new JFrame("IJA GAME");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());


		for (ToolRobot toolRobot : this.robots) {
			toolRobot.addObserver(this);
		}

		// Create the main panel to hold the field views
		JPanel mainPanel = new JPanel(new GridLayout(environment.rows(), environment.cols()));

		// Create and add FieldViews for each position in the environment
		for (int i = 0; i < environment.rows(); i++) {
			for (int j = 0; j < environment.cols(); j++) {
				Position pos = new Position(j, i);
				FieldView fieldView = new FieldView(pos);

				mainPanel.add(fieldView);
				fieldViews.put(pos, fieldView);

				fieldView.addMouseListener(this);

				fieldView.setHasObstacle(environment.obstacleAt(pos));
				fieldView.setHasRobot(environment.robotAt(pos), 0,0);

			}
		}


		// Create navbar panel
		JPanel navbar = new JPanel(new FlowLayout(FlowLayout.CENTER));


		// Add buttons or other controls to the navbar
		JButton addObstacle = new JButton("Add Obstacle");
		navbar.add(addObstacle);

		addObstacle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Show input dialog to get obstacle parameters
				while (true) {
					String obstaclePosition = JOptionPane.showInputDialog(null, "Enter obstacle position as \"x,y\":");
					String[] positionParameters = obstaclePosition.split(",");
					Position position = new Position(Integer.parseInt(positionParameters[0]), Integer.parseInt(positionParameters[1]));
					Environment env = (Environment) environment;
					if (!env.createObstacleAt(position.getCol(), position.getRow()))	{
						JOptionPane.showMessageDialog(null, "Occupied position\n", "Add obstacle", JOptionPane.INFORMATION_MESSAGE);
					}
					else {
							System.out.println("Obstacle has been created on position: <" +  position.getRow() + "," + position.getCol() + ">");
							return;
					}
				}
			}
		});

		JButton addButton = new JButton("Add Robot");
		navbar.add(addButton);

		addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Loop to keep asking for input until valid or cancelled
                while (true) {
                    String robotPosition = JOptionPane.showInputDialog(frame, "Enter robot position as \"x,y\":");
                    if (robotPosition == null || robotPosition.trim().isEmpty()) {
                        break;
                    }

                    try {
                        String[] positionParameters = robotPosition.split(",");
                        if (positionParameters.length != 2) {
                            JOptionPane.showMessageDialog(frame, "Invalid input. Please enter coordinates as x,y", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                            continue;
                        }

                        int x = Integer.parseInt(positionParameters[0].trim());
                        int y = Integer.parseInt(positionParameters[1].trim());
                        Position pos = new Position(x, y);
                        Environment env = (Environment) environment;

                        if (!env.robotAt(pos) && !env.obstacleAt(pos)) {
                            Robot robot = ControlledRobot.create(env, pos);
                            if (robot != null) {
                                robots.add(robot);
								System.out.println("Controlled Robot has been created on position: <" + x + "," + y + ">");
                                robot.addObserver(EnvPresenter.this);
                                break; // exit the loop after successfully adding a robot
                            } else {
                                JOptionPane.showMessageDialog(frame, "Could not create robot at the specified position.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Position already occupied by another robot or obstacle.", "Position Occupied", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid coordinates. Please enter valid integer coordinates.", "Invalid Coordinates", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });

        JButton move = new JButton("Move");
		navbar.add(move);
            move.addActionListener(new ActionListener() {
		    	@Override
		    	public void actionPerformed(ActionEvent e) {
		    		MoveForward();
					System.out.println("Controlled Robot has moved forward");
		    	}
		    });

		JButton left = new JButton("Left");
		navbar.add(left);
            left.addActionListener(new ActionListener() {
		    	@Override
		    	public void actionPerformed(ActionEvent e) {
		    		MoveLeft();
					System.out.println("Controlled Robot has turned left");
		    	}
		    });

		JButton right = new JButton("Right");
		navbar.add(right);
		    right.addActionListener(new ActionListener() {
		    	@Override
		    	public void actionPerformed(ActionEvent e) {
		    		MoveRight();
					System.out.println("Controlled Robot has moved right");
		    	}
		    });

		// Additional navbar elements can be added here

		// Add the main panel and navbar to the frame
		frame.add(mainPanel, BorderLayout.CENTER);
		frame.add(navbar, BorderLayout.SOUTH);

		// Pack and set visible
		frame.pack();
		frame.setSize(frame.getWidth(), frame.getHeight() + 50); // Adjust height to accommodate navbar
		frame.setVisible(true);
	}
*/

	public FieldView fieldAt(Position pos) {
		return fieldViews.get(pos);
	}

	public void addRobot()	{
		Position pos = new Position(0, 0);
		Robot robot = ControlledRobot.create((Environment) this.environment, pos);
		this.robots.add(robot);
		robot.addObserver(this);
	}

	public void MoveRight(){
		if(this.controlledRobot == null){
			JOptionPane.showMessageDialog(null, "Choose robot", "Move right", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		this.controlledRobot.turn(1);
	}

    public void MoveLeft(){
		if(this.controlledRobot == null){
			JOptionPane.showMessageDialog(null, "Choose robot", "Move right", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		this.controlledRobot.turn(7);
	}

    public void MoveForward(){
		if(this.controlledRobot == null){
			JOptionPane.showMessageDialog(null, "Choose robot", "Move right", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		this.controlledRobot.move();
	}

	public void mouseClicked(MouseEvent e) {
		// Handle mouse click events on FieldViews
		FieldView clickedFieldView = (FieldView) e.getSource();
		Position position = clickedFieldView.getPos();

		for(ToolRobot robot : this.robots)
		{
			if(robot.getPosition().equals(position) && !isRobotInAutoRobots(robot, this.autorobots))
			{
				this.controlledRobot = robot;
				System.out.println("Controlled Robot has been choosed");
			}
		}

	}

    private boolean isRobotInAutoRobots(ToolRobot robot, Set<Robot> autorobots) {
        for (Robot autoRobot : autorobots) {
            if (autoRobot.equals(robot)) { 
                return true;
            }
        }
        return false;
    }



	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}


}





