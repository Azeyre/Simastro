package fr.univlille.modelisation.views.animations;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AlphaTest extends Application {

	List<AnimatedList> list = new ArrayList<AnimatedList>();
	GraphicsContext context;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Timeline timeline;
		Canvas canvas = new Canvas(1000, 600);
		context = canvas.getGraphicsContext2D();
			
		StackPane root = new StackPane();
		root.setBackground(new Background(new BackgroundFill(Color.rgb(245, 245, 245), CornerRadii.EMPTY, Insets.EMPTY)));
		root.getChildren().addAll(canvas);
		root.setPrefSize(1000, 600);
		Scene scene = new Scene(root);
		
		primaryStage.setMaxWidth(1920);
		primaryStage.setMaxHeight(1080);
		primaryStage.setTitle("AlphaTest by sadiy");
		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(t -> {
			Platform.exit();
			System.exit(0);
		});
		
		AnimatedList animatedList = new AnimatedList();
		animatedList.add(100, AnimationType.OVAL, 50, 50, 50, 50);
		animatedList.add(250, AnimationType.OVAL, 100, 125, 50, 50);

		list.add(animatedList);
		
		AnimatedList animatedList2 = new AnimatedList();
		animatedList2.add(100, AnimationType.RECT, 100, 125, 50, 50);
		animatedList2.add(250, AnimationType.RECT, 50, 50, 50, 50);

		list.add(animatedList2);
		
		timeline = new Timeline(new KeyFrame(Duration.millis(15), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				display();
			}
		}));

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

	}
	
	public void display() {
		context.clearRect(0, 0, 1000, 600);
		
		for (int i = 0; i < list.size(); i++) {
			AnimatedList animatedList = list.get(i);
			
			if(animatedList.isEmpty()) {
				list.remove(i);
				continue;
			}
			
			Animation animation = (animatedList.last().frameDuration > 0) ? animatedList.last() : animatedList.next();
			animation.apply(context);
		}
	}

}
