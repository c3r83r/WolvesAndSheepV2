package Sheeps;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 800, Color.LIMEGREEN);
        //stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        //Sheep sheep = new Sheep(100, 100);
        //Circle circle = sheep.getCircle();
        //root.getChildren().add(circle);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
