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

import java.io.IOException;
import java.util.*;

import javafx.scene.input.MouseEvent;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class EnvPresenter extends Application implements Observable.Observer {
	private ToolEnvironment environment;
	public Map<Position, FieldView> fieldViews;

	public Map<Position, RobotView> robotViewMap;
	public List<ToolRobot> robots;
	private ToolRobot controlledRobot;
    public Set<Robot> autorobots;

	private GridPane mainPanel;
	private Pane navbar;
	private BorderPane root;

	private static Map<String, Map<String, Integer>> parameters;
	private Set<Obstacle> obstacles;

	private Circle activeRobot;
	private Circle activeControlledR;

	private final Set<Circle> robotViews = new HashSet<>();
	private final Set<Circle> controlledRobots = new HashSet<>();

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

		primaryStage.setTitle("IJA GAME");

		root = new BorderPane();
		//root.setStyle("-fx-background-color: #192a40;");

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
			root.getChildren().add(rectangle);
		}

		for(Robot robot : this.autorobots){
			Position pos = new Position(robot.getPosition().getCol(),robot.getPosition().getRow());
			Circle circle = new Circle(pos.getCol(),pos.getRow(),25,Color.YELLOW);
			robot.addObserver(this);
			this.robotViews.add(circle);
			root.getChildren().add(circle);
		}

		System.out.println(robotViews);
		Circle robot = new Circle(50, 50, 10, Color.YELLOW);
		root.getChildren().add(robot);


		//moveRobot(200, 200,robot);

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
				Rectangle obstacle = new Rectangle(position.getCol(),position.getRow(),50,50);
				obstacle.setFill(Color.BLACK);
				root.getChildren().add(obstacle);

			});

			// Create a container to hold input components
			VBox container = new VBox(positionLabel, positionInput, confirmButton);
			Scene scene = new Scene(container);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();


		});

		Button addRobot = new Button("Add Robot");
		addRobot.setOnAction(e -> {
			// Create input components
			TextField positionInput = new TextField();
			Label positionLabel = new Label("Enter robot's position as \"x,y\":");
			Button confirmButton = new Button("Confirm");

			// Add action to confirm button
			confirmButton.setOnAction(event -> {
				String robotsPosition = positionInput.getText();
				String[] positionParameters = robotsPosition.split(",");
				Position position = new Position(Integer.parseInt(positionParameters[0]), Integer.parseInt(positionParameters[1]));
				Environment env = (Environment) environment;


				ControlledRobot newRobot = ControlledRobot.create(env,position);
				if(newRobot != null){
					newRobot.addObserver(this);
					Circle circle = new Circle(position.getCol(), position.getRow(), 25, Color.RED);
					root.getChildren().add(circle);
					this.controlledRobots.add(circle);
					circle.setOnMouseClicked(event1 ->{
						//Circle newcircle = new Circle(circle.getCenterX(), circle.getCenterY(), 25, Color.BLUE);
						this.activeControlledR = circle;
						//root.getChildren().add(circle);

						this.robotViews.add(circle);
					});

				}


			});


			// Create a container to hold input components
			VBox container = new VBox(positionLabel, positionInput, confirmButton);
			Scene scene = new Scene(container);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();


		});

		Button move = new Button("Move");

		move.setOnAction(event ->{
			Position pos = new Position((int) this.activeControlledR.getCenterX(), (int) this.activeControlledR.getCenterY());
			for(Robot ctrlRobot : environment.getList()){

				if(ctrlRobot.getPosition().equals(pos)){
					System.out.println("ctrl moved");
					ctrlRobot.move();
				}
			}
		});



		Scene scene = new Scene(root, room.cols(), room.rows());
		root.setId("pane");
		//Scene scene = new Scene(container);


		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setScene(scene);


		navbar.getChildren().add(addObstacle);
		navbar.getChildren().add(addRobot);
		navbar.getChildren().add(move);


		// Add other buttons to the navbar and set their event handlers similarly

		root.setBottom(navbar);

		primaryStage.show();

		Thread robotThread = new Thread(() -> {
			while (true) {
				// Move the robot
				Platform.runLater(() -> {

						for(Robot autorobot : this.autorobots) {
							if (!autorobot.canMove()) {
								autorobot.turn();
								System.out.println(autorobot+" turned");
							}
							if(autorobot.move()) {
								System.out.println(autorobot + " moved");
							}
						}

				});

				try {
					Thread.sleep(200); // Adjust the delay between movements
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		robotThread.setDaemon(true); // Mark the thread as a daemon to stop it when the application exits
		robotThread.start();


	}

	private void moveRobot(double newX, double newY) {
		// Create a translate transition
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), this.activeRobot);
		transition.setToX(newX - this.activeRobot.getCenterX());
		transition.setToY(newY - this.activeRobot.getCenterY());


		try {

			transition.setOnFinished(event -> {
				this.activeRobot.setTranslateX(0);
				this.activeRobot.setTranslateY(0);
				this.activeRobot.setCenterX(newX);
				this.activeRobot.setCenterY(newY);
			});

			this.robotViews.remove(this.activeRobot);
			root.getChildren().remove(this.activeRobot);

			if (controlledRobots.contains(this.activeRobot)) {
				Circle circle = new Circle(newX, newY, 25, Color.RED);
				root.getChildren().add(circle);
				this.controlledRobots.add(circle);
				this.controlledRobots.remove(this.activeRobot);


				this.activeControlledR = circle;
				this.robotViews.add(circle);

			}	else {
				Circle newRobot = new Circle(newX, newY, 25, Color.YELLOW);
				this.robotViews.add(newRobot);
				root.getChildren().add(newRobot);
			}

			// Play the animation
			transition.play();
		}
		catch (Exception ignored){}
	}


	public void update(Observable o) {
    if (o instanceof ToolRobot) {
        Position PrevPos = ((ToolRobot) o).getPrevPosition();
        Position newPos = ((ToolRobot) o).getPosition();

        this.activeRobot = getRobotsView(PrevPos);
        if (this.activeRobot != null) {
            moveRobot(newPos.getCol(), newPos.getRow());
        } else {
            System.out.println("Robot not found for previous position: " + PrevPos);
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


}





