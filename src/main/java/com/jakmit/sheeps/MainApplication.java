package com.jakmit.sheeps;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        Group root = new Group();
        double screenX = 1600, screenY = 900, sheepRadius = 25, babySheepRadius = 5, border = 100;
        Scene scene = new Scene(root, screenX, screenY, Color.LIMEGREEN);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();


        int numberOfSheep = 66;
        ArrayList<Sheep> sheepArrayList = new ArrayList<Sheep>();
        int numberOfWolves = 6;
        ArrayList<Wolf> wolfArrayList = new ArrayList<Wolf>();

        Stats stats = new Stats(sheepArrayList, wolfArrayList);
        stats.start();

        for (int i = 0; i< numberOfWolves; ++i){
            double randomX = 0;
            double randomY = 0;
            randomX = Math.random() * (screenX - (sheepRadius + border) * 2) + sheepRadius + border;
            randomY = Math.random() * (screenY - (sheepRadius + border) * 2) + sheepRadius + border;
            Wolf wolf = new Wolf(randomX, randomY);
            wolf.setScreenSizes(screenX, screenY, border);
            wolfArrayList.add(wolf);
        }

        for (int i = 0; i < numberOfSheep; ++i) {
            double randomX = 0;
            double randomY = 0;
            boolean intersect = true;
            while (intersect) {

                randomX = Math.random() * (screenX - (sheepRadius + border) * 2) + sheepRadius + border;
                randomY = Math.random() * (screenY - (sheepRadius + border) * 2) + sheepRadius + border;

                intersect = false;
                for (Sheep s :
                        sheepArrayList) {
                    double distance = Math.hypot(s.x - randomX, s.y - randomY);
                    if (distance <= 2 * sheepRadius + 2) {
                        intersect = true;
                        break;
                    }
                }
            }
            Sheep sheep = new Sheep(randomX, randomY);
            sheep.r = Math.random() * (sheepRadius - babySheepRadius) + babySheepRadius;
            sheep.minR = babySheepRadius;
            sheep.maxR = sheepRadius;
            sheep.updateViewAndBorder();
            sheep.initSpeed();
            sheep.setScreenSizes(screenX, screenY, border);
            sheepArrayList.add(sheep);
        }

        for (Sheep sheep :
                sheepArrayList) {
            sheep.sheepArrayList = sheepArrayList;
            sheep.startAnimation();
            sheep.wolfCollection = wolfArrayList;
            sheep.root = root;
            root.getChildren().add(sheep.getCircle());
        }
        for (Wolf wolf :
                wolfArrayList) {
            wolf.sheepArrayList = sheepArrayList;
            wolf.wolfCollection = wolfArrayList;
            wolf.moveForwardAndHunt = true;
            wolf.root = root;
            wolf.r = 10;
            wolf.minR = 5;
            wolf.maxR = wolf.r;
            wolf.updateViewAndBorder();
            wolf.startAnimation();
            root.getChildren().add(wolf.getCircle());
        }


    }

    public static void main(String[] args) {
        launch(args);
    }
}
