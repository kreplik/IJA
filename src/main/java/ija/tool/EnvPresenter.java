package ija.tool;

import ija.common.Environment;
import ija.common.Obstacle;
import ija.common.Robot;
import ija.game.Main;
import ija.room.ControlledRobot;
import ija.room.Room;
import ija.tool.common.Observable;
import ija.tool.common.ToolEnvironment;
import ija.tool.common.ToolRobot;
import ija.tool.common.Position;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.*;
import javafx.geometry.Pos;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.TextInputDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class EnvPresenter extends Application implements Observable.Observer {
	private ToolEnvironment environment;
	public List<ToolRobot> robots;
    public Set<Robot> autorobots;

	private BorderPane root;

	private static Map<String, Map<String, Integer>> parameters;
	private Set<Obstacle> obstacles;

	private Circle activeControlledR;

	private final Set<Circle> robotViews = new HashSet<>();
	private final Set<Circle> controlledRobots = new HashSet<>();
	private Map<Circle, Circle> directionIndicators = new HashMap<>();
	private Map<Circle, Robot> robotMap = new HashMap<>();
	boolean pause;
	private PrintWriter printWriter;


	public EnvPresenter() {
		this.autorobots = new HashSet<>();
		this.pause = false;
	}


	// Declare other UI elements as needed

	@Override
	public void start(Stage primaryStage) throws Exception{
		try {
    	    File logFile = new File("data/logging.txt");
    	    if (logFile.exists()) {
    	        logFile.delete();  // Ensures that the file is fresh each run
    	    }
    	    // Ensure the PrintWriter flushes automatically after each println
    	    printWriter = new PrintWriter(new FileWriter(logFile), true);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}


		Main.ParametersLoader loader = new Main.ParametersLoader("data/parameters.txt");
		try {
			parameters = loader.loadParameters();
		} catch (IOException e) {
			e.printStackTrace();
		}

		root = new BorderPane();

		Environment room = Room.create(parameters.get("GameField").get("width"), parameters.get("GameField").get("height"));

		this.robots = room.robots();

		this.environment = room;
		this.obstacles = room.getObstacles();


		for(Map.Entry<String, Map<String, Integer>> entry : parameters.entrySet())
		{
			if(entry.getKey().contains("Obstacle")) {

				room.createObstacleAt(entry.getValue().get("positionX"), entry.getValue().get("positionY"));

				printWriter.println("Obstacle has been created on position: <" +  entry.getValue().get("positionX") + "," + entry.getValue().get("positionY") + ">"); 
			}

			if(entry.getKey().contains("Robot")){

				Position p1 = new Position(entry.getValue().get("positionX"), entry.getValue().get("positionY"));

				this.autorobots.add(ControlledRobot.create(room, p1));

				printWriter.println("Autonomous Robot has been created on position: <" +  entry.getValue().get("positionX") + "," + entry.getValue().get("positionY") + ">");
			}
		}

		primaryStage.setTitle("IJA GAME");


		Image img = new Image("file:lib/background.jpg");
		ImageView imageView = new ImageView(img);
		imageView.setFitHeight(800);
		imageView.setFitWidth(800);
		//Adding the imageView to the stackPane
		root.getChildren().add(imageView);


		GridPane mainPanel = new GridPane();
		root.setCenter(mainPanel);



		for(Obstacle obstacle : this.obstacles){
			Position pos = new Position(obstacle.getPosition().getCol(),obstacle.getPosition().getRow());
			Rectangle rectangle = new Rectangle(pos.getCol(),pos.getRow(),50,50);
			rectangle.setFill(Color.BLACK);
			rectangle.setStyle("-fx-border-color: #1a16af; -fx-border-width: 1; -fx-border-radius: 5;");

			root.getChildren().add(rectangle);
		}

		for (Robot robot : this.autorobots) {
		    Position pos = new Position(robot.getPosition().getCol(), robot.getPosition().getRow());
		    Circle robotBody = new Circle(pos.getCol(), pos.getRow(), 25, Color.YELLOW);

		    // Direction indicator creation (as you did before)
		    double angleInRadians = Math.toRadians(robot.angle());
		    double offsetX = (25 - 5) * Math.cos(angleInRadians);
		    double offsetY = (25 - 5) * Math.sin(angleInRadians);
		    Circle directionIndicator = new Circle(
		        pos.getCol() + offsetX,
		        pos.getRow() + offsetY,
		        5,
		        Color.BLACK
		    );

		    robot.addObserver(this);
		    this.robotViews.add(robotBody);
		    root.getChildren().addAll(robotBody, directionIndicator);
		    directionIndicators.put(robotBody, directionIndicator);
		    robotMap.put(robotBody, robot);  // Link the Circle to the Robot
		}


		//printWriter.println(robotViews);

		//moveRobot(200, 200,robot);

		FlowPane navbar = new FlowPane();
		navbar.setPadding(new Insets(10,10,10,10));
		navbar.setHgap(10);
		navbar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
		navbar.setAlignment(Pos.CENTER); 
		// Styling buttons uniformly
    	String actionButtonStyle = "-fx-background-color: linear-gradient(#7fcd91, #0f3d0f), " +
    	                           "radial-gradient(center 50% -40%, radius 200%, #5a9f6e 45%, #1e4b23 50%); " +
    	                           "-fx-background-radius: 6; " +
    	                           "-fx-text-fill: white; " +
    	                           "-fx-font-weight: bold; " +
    	                           "-fx-font-size: 14px;";

    	String creationButtonStyle = "-fx-background-color: linear-gradient(#1d1d1d, #424242), " +
    	                             "radial-gradient(center 50% -40%, radius 200%, #686868 45%, #2f2f2f 50%); " +
    	                             "-fx-background-radius: 6; " +
    	                             "-fx-text-fill: #dcdcdc; " +
    	                             "-fx-font-weight: bold; " +
    	                             "-fx-font-size: 14px;";

		Button addObstacle = new Button("Add Obstacle");
		addObstacle.setStyle(creationButtonStyle);

		addObstacle.setOnAction(e -> {

			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Add obstacle");
			dialog.setHeaderText("Enter obstacle's position as \"x,y\"");
			dialog.setContentText("Position:");


			// Show the dialog and wait for the user's response
			Optional<String> result = dialog.showAndWait();

			result.ifPresent(pos -> {

				String[] positionParameters = pos.split(",");
				Position position = new Position(Integer.parseInt(positionParameters[0]), Integer.parseInt(positionParameters[1]));
				Environment env = (Environment) environment;
				if (!env.createObstacleAt(position.getCol(), position.getRow())) {
					// Show error message
				}
				Rectangle obstacle = new Rectangle(position.getCol(),position.getRow(),50,50);
				obstacle.setFill(Color.BLACK);
				obstacle.setStyle("-fx-border-color: #1a16af; -fx-border-width: 5; -fx-border-radius: 5;");
				root.getChildren().add(obstacle);

			});


		});

		Button addRobot = new Button("Add Robot");
		addRobot.setStyle(creationButtonStyle);
		addRobot.setOnAction(e -> {
		    TextInputDialog dialog = new TextInputDialog();
		    dialog.setTitle("Add robot");
		    dialog.setHeaderText("Enter robot's position as \"x,y\"");
		    dialog.setContentText("Position:");
		
		    Optional<String> result = dialog.showAndWait();
		    result.ifPresent(pos -> {
		        String[] positionParameters = pos.split(",");
		        Position position = new Position(Integer.parseInt(positionParameters[0]), Integer.parseInt(positionParameters[1]));
		        Environment env = (Environment) environment;
			
		        ControlledRobot newRobot = ControlledRobot.create(env, position);
		        if (newRobot != null) {
		            newRobot.addObserver(this);
				
		            Circle circle = new Circle(position.getCol(), position.getRow(), 25, Color.RED);
		            Circle directionIndicator = new Circle(position.getCol(), position.getRow(), 5, Color.WHITE);
		            updateDirectionIndicator(directionIndicator, newRobot.angle(), position);
				
		            this.robotViews.add(circle);
		            this.controlledRobots.add(circle);
		            this.directionIndicators.put(circle, directionIndicator);
		            this.robotMap.put(circle, newRobot);  // Ensuring the robot is mapped
				
		            root.getChildren().addAll(circle, directionIndicator);
				
		            circle.setOnMouseClicked(event -> {
		                this.activeControlledR = circle;
		                for (Circle otherCircle : this.controlledRobots) {
		                    if (!otherCircle.equals(circle)) {
		                        otherCircle.setFill(Color.RED);
		                    }
		                }
		                circle.setFill(Color.GREEN);
		            });
		        }
		    });
		});


		Button move = new Button("Move");
		move.setStyle(actionButtonStyle);

		Timeline moveTimeline = new Timeline(new KeyFrame(Duration.millis(20), e -> {
		    if (activeControlledR == null) {
		        showAlert("No robot selected to move.");
		        return;
		    }
		    Position pos = new Position((int) activeControlledR.getCenterX(), (int) activeControlledR.getCenterY());
		    int count = 0;
		    for (Robot ctrlRobot : environment.getList()) {
		        if (ctrlRobot.getPosition().equals(pos)) {
		            if (ctrlRobot.canMove()) {
		                printWriter.println("Controlled robot moved forward.");
		                ctrlRobot.move();
		            } else {
		                printWriter.println("Controlled robot is blocked.");
		                break; // Break out of the loop if the robot is blocked.
		            }
		            if (++count >= 20) break; // Stop after 20 moves.
		        }
		    }
		}));
		moveTimeline.setCycleCount(Timeline.INDEFINITE);

		move.setOnMousePressed(event -> moveTimeline.play());
		move.setOnMouseReleased(event -> moveTimeline.stop());

		Button turnR = new Button("Right");
		turnR.setStyle(actionButtonStyle);
		turnR.setOnAction(event -> {
		    if (activeControlledR == null) {
		        showAlert("No robot selected to turn right.");
		        return;
		    }
		    Position pos = new Position((int) activeControlledR.getCenterX(), (int) activeControlledR.getCenterY());
		    Set<Robot> robotList = environment.getList();
		    for (Robot robot : robotList) {
		        if (robot.getPosition().equals(pos)) {
		            robot.turn(1);
		            printWriter.println("Controlled robot turned right.");
		        }
		    }
		});

		Button turnL = new Button("Left");
		turnL.setStyle(actionButtonStyle);
		turnL.setOnAction(event -> {
		    if (activeControlledR == null) {
		        showAlert("No robot selected to turn left.");
		        return;
		    }
		    Position pos = new Position((int) activeControlledR.getCenterX(), (int) activeControlledR.getCenterY());
		    Set<Robot> robotList = environment.getList();
		    for (Robot robot : robotList) {
		        if (robot.getPosition().equals(pos)) {
		            robot.turn(7);
		            printWriter.println("Controlled robot turned left.");
		        }
		    }
		});


		Button pauseButton = new Button("Pause");
		// Initial color setting based on initial pause state
		updateButtonColor(pauseButton, this.pause);
		pauseButton.setPrefWidth(70);
		pauseButton.setOnAction(event -> {
		    this.pause = !this.pause;  // Toggle the pause state
		    updateButtonColor(pauseButton, this.pause);  // Update button color based on the new state
		});



		Scene scene = new Scene(root, room.cols(), room.rows());
		root.setId("pane");
		//Scene scene = new Scene(container);


		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setScene(scene);

		navbar.setHgap(30);

		navbar.getChildren().add(addObstacle);

		navbar.setHgap(10);
		navbar.getChildren().add(turnL);
		navbar.getChildren().add(move);
		navbar.getChildren().add(turnR);
		navbar.getChildren().add(pauseButton);
		navbar.setHgap(30);
		navbar.getChildren().add(addRobot);



		// Add other buttons to the navbar and set their event handlers similarly

		root.setBottom(navbar);

		primaryStage.show();

		Thread robotThread = new Thread(() -> {
			while (true) {
				// Move the robot
				Platform.runLater(() -> {

						for(Robot autorobot : this.autorobots) {
							if(this.pause){
								continue;
							}else if (!autorobot.canMove()) {
								autorobot.turn();
								printWriter.println(autorobot + " turned");
							}
							else {
								autorobot.move();
								printWriter.println(autorobot + " moved");
							}
						}

				});

				try {
					Thread.sleep(20); // Adjust the delay between movements
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		robotThread.setDaemon(true); // Mark the thread as a daemon to stop it when the application exits
		robotThread.start();


	}

	public void update(Observable o) {
	    if (o instanceof ToolRobot) {
	        ToolRobot robot = (ToolRobot) o;
	        Position newPos = robot.getPosition();
	        Circle robotBody = getRobotsView(new Position(robot.getPrevPosition().getCol(), robot.getPrevPosition().getRow()));
	        if (robotBody != null && robotMap.containsKey(robotBody)) {
	            moveDirection(robotBody, newPos.getCol(), newPos.getRow());
	        }
	    }
	}




	public Circle getRobotsView(Position pos) {
	    for (Circle circle : robotViews) {
	        Position circlePos = new Position((int) circle.getCenterX(), (int) circle.getCenterY());
	        if (circlePos.equals(pos)) {
	            return circle;
	        }
	    }
	    return null;  // Explicitly return null if no robot is found
	}

	private void moveDirection(Circle robotBody, double newX, double newY) {
	    if (robotBody != null) {
	        // Update the robot's main body position
	        robotBody.setCenterX(newX);
	        robotBody.setCenterY(newY);

	        // Assuming `robotMap` maps each Circle to its corresponding Robot object
	        Robot robot = robotMap.get(robotBody);
	        if (robot != null) {

	            Circle directionIndicator = directionIndicators.get(robotBody);
	            if (directionIndicator != null) {
	                updateDirectionIndicator(directionIndicator,robot.angle(),robot.getPosition());

	            }
	        }
	    }
	}

	private void updateDirectionIndicator(Circle indicator, int angle, Position position) {
	    double radius = 25; // main circle radius
	    double angleInRadians = Math.toRadians(angle);
	    double offsetX = radius * Math.sin(angleInRadians);  // Adjusted for GUI
	    double offsetY = -radius * Math.cos(angleInRadians); // Adjusted for GUI y-axis direction
	    indicator.setCenterX(position.getCol() + offsetX);
	    indicator.setCenterY(position.getRow() + offsetY);
	}

	private void updateButtonColor(Button button, boolean isPaused) {
	    if (isPaused) {
			button.setText("Play");
	        button.setStyle("-fx-background-color: linear-gradient(#7fcd91, #0f3d0f), " +
    	                           "radial-gradient(center 50% -40%, radius 200%, #5a9f6e 45%, #1e4b23 50%); " +
    	                           "-fx-background-radius: 6; " +
    	                           "-fx-background-color: green; -fx-text-fill: white;" +
    	                           "-fx-font-weight: bold; " +
    	                           "-fx-font-size: 14px;");
	    } else {
			button.setText("Pause");
	        button.setStyle("-fx-background-color: linear-gradient(#7fcd91, #0f3d0f), " +
    	                           "radial-gradient(center 50% -40%, radius 200%, #5a9f6e 45%, #1e4b23 50%); " +
    	                           "-fx-background-radius: 6; " +
    	                           "-fx-background-color: red; -fx-text-fill: white;" +
    	                           "-fx-font-weight: bold; " +
    	                           "-fx-font-size: 14px;");
	    }
	}

	private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }

}





