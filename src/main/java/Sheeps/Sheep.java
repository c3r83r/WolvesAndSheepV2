package Sheeps;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Sheep {
    public double x,y;
    public double speed;
    private Circle circle;
    private double r = 30;

    public Sheep(double x, double y) {
        this.x = x;
        this.y = y;
        this.circle = new Circle(x, y, r, Color.WHITE);
    }

    public Circle getCircle() {
        return circle;
    }
}
