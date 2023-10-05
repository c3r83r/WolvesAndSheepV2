package com.jakmit.sheeps;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Sheep {
    public Group root;
    public boolean sex;
    public double reproductionCoolDown = 0.1*60, reproductionLoad = 0;
    public double lengthOfLife, maxLengthOfLife = 12*60, minLengthOfLife = 10*60;
    public double refreshingFPS = 100; // number of frames in one second
    public boolean alive = true;
    //public Wolf wolf;
    private Timeline timeline;
    public double x, y;
    public double visual_range = 150, protected_range_squared = 50 * 50;
    public double turn_factor = 0.06, avoid_factor = 0.002, centering_factor = 0.000001, matching_factor = 0.01;
    public double panicZoneAngle = 5;
    public double directionAngle = 0, dDirectionAngle = 0;
    public double speed = 1.2, speedX, speedY;
    public double maxSpeed = 1.2, minSpeed = 0.5;
    private final Circle circle;
    public double r = 1, maxR, minR;
    public double growingUpTime = 1.5*60, pregnancyTime = 0.3*60;
    private double pregnancyTimeLoader = 0;
    private boolean isPregnant;
    public int maxNumberOfChildren = 3, minNumberOfChildren = 1;
    private int numberOfChildren;
    public Collection<Sheep> sheepArrayList = new ArrayList<Sheep>();
    public Collection<Wolf> wolfCollection = new ArrayList<Wolf>();
    private double screenXSize, screenYSize, border;

    public void setScreenSizes(double screenXSize, double screenYSize, double border) {
        this.screenXSize = screenXSize;
        this.screenYSize = screenYSize;
        this.border = border;
    }

    public Sheep(double x, double y) {
        this.x = x;
        this.y = y;
        this.circle = new Circle(x, y, r, Color.WHITE);
        this.lengthOfLife = Math.random() * (maxLengthOfLife - minLengthOfLife) + minLengthOfLife;
        sex = new Random().nextBoolean();
        if (sex){
            circle.setFill(Color.LIGHTGREY);
        }
    }

    public void initSpeed() {
        double randomAngle = Math.random() * 360;
        speedX = speed * Math.sin(Math.toRadians(randomAngle));
        speedY = speed * Math.cos(Math.toRadians(randomAngle));
    }

    public void addAngleToDirection(double angle) {
        this.directionAngle = ((this.directionAngle + angle) % 360 + 360) % 360;
    }

    public double getDirectionAngle() {
        return directionAngle;
    }

    protected void move() {
        if (lengthOfLife <= 0) {
            death();
        } else {
            lengthOfLife -= (1 / refreshingFPS);
        }
        if (reproductionLoad <= reproductionCoolDown && r >= maxR) {
            reproductionLoad += (1 / refreshingFPS);
        }

        if (this.x < border) {
            this.speedX += turn_factor;
        }
        if (this.x > screenXSize - border) {
            this.speedX -= turn_factor;
        }
        if (this.y > screenYSize - border) {
            this.speedY -= turn_factor;
        }
        if (this.y < border) {
            this.speedY += turn_factor;
        }

        double close_dx = 0, close_dy = 0, xpos_avg = 0, ypos_avg = 0, xvel_avg = 0, yvel_avg = 0, neighboring_boids = 0;
        double visual_range_squared = visual_range * visual_range;
        for (Sheep s :
                sheepArrayList) {
            if (s != this && s.alive) {
                double dx = x - s.x;
                double dy = y - s.y;
                if (Math.abs(dx) < visual_range && Math.abs(dy) < visual_range) {
                    double squared_distance = dx * dx + dy * dy;
                    if (squared_distance < protected_range_squared) {

                        //reproduction of sheep
                        if (!sex && s.sex && reproductionLoad > reproductionCoolDown && s.reproductionLoad > s.reproductionCoolDown && !isPregnant && !s.isPregnant && r>=maxR) {
                            s.reproductionLoad = 0;
                            reproductionLoad = 0;
                            /* zmiany, w celu dodania czasu ciazy*/
                            numberOfChildren = ThreadLocalRandom.current().nextInt(minNumberOfChildren, maxNumberOfChildren + 1);
                            //System.out.println(numberOfChildren);
                            this.getCircle().setFill(Color.PINK);
                            this.isPregnant = true;
                        } //end of reproduction

                        close_dx += (this.x - s.x);
                        close_dy += (this.y - s.y);
                    } else if (squared_distance < visual_range_squared) {
                        xpos_avg += s.x;
                        ypos_avg += s.y;
                        xvel_avg += s.speedX;
                        yvel_avg += s.speedY;
                        neighboring_boids += 1;
                    }
                }
            }
        }
        if (isPregnant){
            if (pregnancyTimeLoader<pregnancyTime){
                pregnancyTimeLoader += (1 / refreshingFPS);
            } else {
                pregnancyTimeLoader = 0;
                //rodzimy
                for (int i = 0; i<numberOfChildren; ++i){

                    Sheep babySheep = new Sheep(x+r, y+r);
                    //System.out.println("urodzono " + babySheep);

                    babySheep.r = minR;
                    babySheep.minR = minR;
                    babySheep.maxR = maxR;
                    babySheep.updateViewAndBorder();
                    babySheep.initSpeed();
                    babySheep.setScreenSizes(screenXSize, screenYSize, border);
                    babySheep.sheepArrayList = sheepArrayList;
                    babySheep.startAnimation();
                    babySheep.wolfCollection = wolfCollection;
                    babySheep.root = root;
                    root.getChildren().add(babySheep.getCircle());
                    if (!sheepArrayList.contains(babySheep)) {
                        this.sheepArrayList.add(babySheep);
                    }
                }

                this.getCircle().setFill(Color.WHITE);
                isPregnant = false;
            }
        }

        for (Wolf wolf :
                wolfCollection) {
        double dx = x - wolf.x;
        double dy = y - wolf.y;
        if (Math.abs(dx) < visual_range && Math.abs(dy) < visual_range) {
            double squared_distance = dx * dx + dy * dy;
            if (squared_distance < visual_range_squared) {
                close_dx += (this.x - wolf.x);
                close_dy += (this.y - wolf.y);
            }
        }}

        //neighbour
        if (neighboring_boids > 0) {
            xpos_avg = xpos_avg / neighboring_boids;
            ypos_avg = ypos_avg / neighboring_boids;
            xvel_avg = xvel_avg / neighboring_boids;
            yvel_avg = yvel_avg / neighboring_boids;

            speedX = (speedX +
                    (xpos_avg - x) * centering_factor +
                    (xvel_avg - speedX) * matching_factor);
            speedY = (speedY +
                    (ypos_avg - y) * centering_factor +
                    (yvel_avg - speedY) * matching_factor);
        }

        speedX += close_dx * avoid_factor;
        speedY += close_dy * avoid_factor;

        speed = Math.sqrt(speedX * speedX + speedY * speedY);

        if (speed > maxSpeed) {
            speedX = (speedX / speed) * maxSpeed;
            speedY = (speedY / speed) * maxSpeed;
        }
        if (speed < minSpeed) {
            speedX = (speedX / speed) * minSpeed;
            speedY = (speedY / speed) * minSpeed;
        }

        if (!((x + speedX) > 0 && (x + speedX) < screenXSize)) {
            speedX *= -1;
        }
        if (!((y + speedY) > 0 && (y + speedY) < screenYSize)) {
            speedY *= -1;
        }


        for (Wolf wolf :
                wolfCollection) {
            double dx = x - wolf.x;
            double dy = y - wolf.y;
            double squared_distance = dx * dx + dy * dy;
            if (squared_distance < (r + wolf.r) * (r + wolf.r)) {
                wolf.justAteSheep = true;
                wolf.hungerBar += 10;
                //System.out.println("Mniam!");
                death();
                return;
            }
        }


        x += speedX;
        y += speedY;

        circle.setCenterX(x);
        circle.setCenterY(y);

        //growing up
        if (r < maxR) {
            r += (maxR - minR) / (growingUpTime * refreshingFPS);
            updateViewAndBorder();
        }


        circle.setRadius(r);
    }

    public void death() {
        alive = false;
        circle.setOpacity(0);
        timeline.stop();
        circle.setFill(Color.BLACK);
        sheepArrayList.remove(this);
    }

    public void startAnimation() {
        if (timeline == null) {
            // lazily create timeline
            timeline = new Timeline(new KeyFrame(Duration.millis(1000 / refreshingFPS), event -> move()));
            timeline.setCycleCount(Animation.INDEFINITE);
        }
        // ensure the animation is playing
        timeline.play();
    }

    public Circle getCircle() {
        return circle;
    }

    public double countDistance(Sheep secondSheep) {
        return Math.hypot(secondSheep.x - this.x, secondSheep.y - this.y);
    }

    public Boolean doSheepIntersect(Sheep secondSheep, double borderDistance) {
        return (this.r + secondSheep.r + borderDistance) > countDistance(secondSheep);
    }

    public void updateViewAndBorder() {
        visual_range = 7 * r;
        protected_range_squared = 6 * r * r;
    }

}
