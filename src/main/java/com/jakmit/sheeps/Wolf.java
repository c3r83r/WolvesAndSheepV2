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

public class Wolf {
    public double hungerBar = 30.0, maxHungerBar = 30;
    public double hungerLoad = 0.01;
    public Group root;
    public boolean sex;
    double protected_range_squared = 20 * 20;
    public double reproductionCoolDown = 0.3 * 60, reproductionLoad = 0;
    public double growingUpTime = 1.5 * 60, pregnancyTime = 0.3 * 60;
    private double pregnancyTimeLoader = 0;
    private boolean isPregnant;
    public int maxNumberOfChildren = 4, minNumberOfChildren = 2;
    private int numberOfChildren;
    public double lengthOfLife, maxLengthOfLife = 12 * 60, minLengthOfLife = 10 * 60;
    public double eatingTime = 0.15 * 30;
    private double eatingTimeLoader = 0;
    public boolean justAteSheep = false;
    public double refreshingFPS = 100;
    public boolean moveForwardAndHunt;
    private Timeline timeline;
    double x, y;
    double speed, speedX, speedY;
    public double maxSpeed, minSpeed;
    double basicSpeed = 1.205, boostSpeed = 3, boostTime = 3, boostLoad, boostCoolDown = 10;
    public double visual_range = 150, protected_range = 50;
    boolean boostLoading = false;
    private final Circle circle;
    public double r = 1, maxR, minR, turn_factor = 2, avoid_factor = 0.002, centering_factor = 0.000001, matching_factor = 0.01;
    ;
    public double directionAngle = 180, autoPilotAngle = 0;
    public Collection<Sheep> sheepArrayList = new ArrayList<Sheep>();
    public Collection<Wolf> wolfCollection = new ArrayList<Wolf>();
    public boolean alive = true;

    private double screenXSize, screenYSize, border;

    public void setScreenSizes(double screenXSize, double screenYSize, double border) {
        this.screenXSize = screenXSize;
        this.screenYSize = screenYSize;
        this.border = border;
    }

    public Wolf(double x, double y) {
        this.x = x;
        this.y = y;
        this.circle = new Circle(x, y, r, Color.RED);
        this.lengthOfLife = Math.random() * (maxLengthOfLife - minLengthOfLife) + minLengthOfLife;
        boostLoad = boostTime;
        speed = basicSpeed;
        maxSpeed = 1.5 * speed;
        minSpeed = 0.5 * speed;
        sex = new Random().nextBoolean();
        if (sex) {
            circle.setFill(Color.BLACK);
        }
    }

    public void addAngleToDirection(double angle) {
        this.directionAngle = ((this.directionAngle + angle) % 360 + 360) % 360;
    }

    private void randomChangeDirectionOfMove() {
        int randomInt = randomWithRange(1, 10);
        if (randomInt <= 2) {
            addAngleToDirection(-turn_factor);
        } else if (randomInt >= 9) {
            addAngleToDirection(turn_factor);
        }

    }

    private double getChangedRandomDirection() {
        int randomInt = randomWithRange(1, 10);
        if (randomInt <= 2) {
            return (directionAngle - turn_factor);
        } else if (randomInt >= 9) {
            return (directionAngle + turn_factor);
        }
        return directionAngle;
    }

    protected void move() {
        if (lengthOfLife <= 0 || hungerBar<=0) {
            death();
        } else {
            lengthOfLife -= (1 / refreshingFPS);
            hungerBar-=hungerLoad;
        }
        if (reproductionLoad <= reproductionCoolDown && r >= maxR) {
            reproductionLoad += (1 / refreshingFPS);
        }
        if (justAteSheep) {
            moveForwardAndHunt = false;
            //eatingTimeLoader += (1 / refreshingFPS);

            if (sex) {
                //A male wolf is looking for a female
                speed = 1.2 * basicSpeed;
                double closestDistanceToSheWolf = Double.POSITIVE_INFINITY;
                Wolf closestSheWolf = null;
                for (Wolf w : wolfCollection) {
                    if (!w.sex && !w.isPregnant && w.justAteSheep && w.r >= w.maxR && r >= maxR && reproductionLoad > reproductionCoolDown && w.reproductionLoad > w.reproductionCoolDown) {
                        double tempDist = Math.hypot(w.x - this.x, w.y - this.y);
                        if (tempDist < closestDistanceToSheWolf) {
                            closestDistanceToSheWolf = tempDist;
                            closestSheWolf = w;
                        }
                    }
                }
                if (closestSheWolf != null) {
                    autoPilotAngle = 360 + Math.toDegrees(Math.atan2((closestSheWolf.x - x), (closestSheWolf.y - y))) % 360;
                    double angleDistance = Math.abs(autoPilotAngle - directionAngle);

                    if (angleDistance != 0) {
                        if (angleDistance != 180) {
                            if (isLeft(x, y, x + speed * Math.sin(Math.toRadians(directionAngle)), y + speed * Math.cos(Math.toRadians(directionAngle)), x + speed * Math.sin(Math.toRadians(autoPilotAngle)), y + speed * Math.cos(Math.toRadians(autoPilotAngle)))) {
                                addAngleToDirection(-turn_factor);
                            } else {
                                addAngleToDirection(+turn_factor);
                            }
                        } else addAngleToDirection(turn_factor);
                    }
                } else {
                    // :--(
                    speed = 0.4 * basicSpeed;
                    randomChangeDirectionOfMove();
                }
            } else {
                //the female wolf is waiting
                speed = 0.4 * basicSpeed;
                randomChangeDirectionOfMove();
            }

            speedX = speed * Math.sin(Math.toRadians(directionAngle));
            speedY = speed * Math.cos(Math.toRadians(directionAngle));

            if (!((x + speedX) > 0 && (x + speedX) < screenXSize)) {
                speedX *= -1;
            }
            if (!((y + speedY) > 0 && (y + speedY) < screenYSize)) {
                speedY *= -1;
            }
            x += speedX;
            y += speedY;
            directionAngle = Math.toDegrees(Math.atan2(speedX, speedY));


            if (/*eatingTimeLoader >= eatingTime &&*/ hungerBar<=maxHungerBar) {
                //eatingTimeLoader = 0;
                justAteSheep = false;
            }
        } else {
            moveForwardAndHunt = true;
            speed = basicSpeed;
        }
        if (boostLoad <= 0) {
            boostLoading = true;
        }
        if (boostLoad >= boostCoolDown) {
            boostLoading = false;
            boostLoad = boostTime;
        }
        if (!boostLoading && boostLoad >= boostTime) {
            boostLoad = boostTime;
        }
        if (moveForwardAndHunt && isAnySheepInNeighborhood() && !boostLoading) {
            speed = boostSpeed;
            boostLoad -= (1 / refreshingFPS);
        } else {
            boostLoad += (1 / refreshingFPS);
            speed = basicSpeed;
        }

        if (moveForwardAndHunt) {
            autopilot();
            double angleDistance = Math.abs(autoPilotAngle - directionAngle);

            if (angleDistance != 0) {
                if (angleDistance != 180) {
                    if (isLeft(x, y, x + speed * Math.sin(Math.toRadians(directionAngle)), y + speed * Math.cos(Math.toRadians(directionAngle)), x + speed * Math.sin(Math.toRadians(autoPilotAngle)), y + speed * Math.cos(Math.toRadians(autoPilotAngle)))) {
                        addAngleToDirection(-turn_factor);
                    } else {
                        addAngleToDirection(+turn_factor);
                    }
                } else addAngleToDirection(turn_factor);
            }

            speedX = speed * Math.sin(Math.toRadians(directionAngle));
            speedY = speed * Math.cos(Math.toRadians(directionAngle));

            double close_dx = 0, close_dy = 0, xpos_avg = 0, ypos_avg = 0, xvel_avg = 0, yvel_avg = 0, neighboring_boids = 0;
            double visual_range_squared = visual_range * visual_range;

            for (Wolf w :
                    wolfCollection) {
                if (w != this && w.alive) {
                    double dx = x - w.x;
                    double dy = y - w.y;
                    if (Math.abs(dx) < visual_range && Math.abs(dy) < visual_range) {
                        double squared_distance = dx * dx + dy * dy;
                        if (squared_distance < protected_range_squared) {

                            //reproduction of wolves
                            if (!sex && w.sex && reproductionLoad > reproductionCoolDown && w.reproductionLoad > w.reproductionCoolDown && !isPregnant && !w.isPregnant && w.r >= w.maxR && r >= maxR) {
                                w.reproductionLoad = 0;
                                reproductionLoad = 0;
                                /* zmiany, w celu dodania czasu ciazy*/
                                numberOfChildren = ThreadLocalRandom.current().nextInt(minNumberOfChildren, maxNumberOfChildren + 1);
                                //System.out.println(numberOfChildren);
                                this.getCircle().setFill(Color.VIOLET);
                                this.isPregnant = true;
                            } //end of reproduction

                            close_dx += (this.x - w.x);
                            close_dy += (this.y - w.y);
                        } else if (squared_distance < visual_range_squared) {
                            xpos_avg += w.x;
                            ypos_avg += w.y;
                            xvel_avg += w.speedX;
                            yvel_avg += w.speedY;
                            neighboring_boids += 1;
                        }
                    }
                }
            }

            if (isPregnant) {
                if (pregnancyTimeLoader < pregnancyTime) {
                    pregnancyTimeLoader += (1 / refreshingFPS);
                } else {
                    pregnancyTimeLoader = 0;
                    //rodzimy
                    for (int i = 0; i < numberOfChildren; ++i) {
                        Wolf babyWolf;
                        babyWolf = new Wolf(x + r, y + r);
                        //System.out.println("urodzono " + babyWolf);

                        babyWolf.r = minR;
                        babyWolf.minR = minR;
                        babyWolf.maxR = maxR;
                        babyWolf.updateViewAndBorder();
                        babyWolf.initSpeed();
                        babyWolf.setScreenSizes(screenXSize, screenYSize, border);
                        babyWolf.sheepArrayList = sheepArrayList;
                        babyWolf.startAnimation();
                        babyWolf.wolfCollection = wolfCollection;
                        babyWolf.root = root;
                        root.getChildren().add(babyWolf.getCircle());
                        if (!wolfCollection.contains(babyWolf)) {
                            this.wolfCollection.add(babyWolf);
                        }
                    }

                    this.getCircle().setFill(Color.RED);
                    isPregnant = false;
                }
            }

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

            x += speedX;
            y += speedY;
            directionAngle = Math.toDegrees(Math.atan2(speedX, speedY));
        }

        circle.setCenterX(x);
        circle.setCenterY(y);

        //growing up
        if (r < maxR) {
            r += (maxR - minR) / (growingUpTime * refreshingFPS);
            updateViewAndBorder();
        }

        circle.setRadius(r);
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

    public void autopilot() {
        Sheep closestSheep = getClosestVisibleSheep();
        if (closestSheep == null) {
            autoPilotAngle = getChangedRandomDirection();
            //randomChangeDirectionOfMove();
            return;
        }
        autoPilotAngle = 360 + Math.toDegrees(Math.atan2((closestSheep.x - x), (closestSheep.y - y))) % 360;
    }

    private boolean isAnySheepInNeighborhood() {
        return getClosestVisibleSheep() != null;
    }

    //counitng distance between two sheeps
    public double countDistance(Sheep sheep) {
        return Math.hypot(sheep.x - this.x, sheep.y - this.y);
    }

    public Sheep getClosestSheep() {
        double distance = Double.POSITIVE_INFINITY;
        Sheep nearestSheep = null;
        for (Sheep sheep :
                sheepArrayList) {
            double actualDistance = countDistance(sheep);
            if (actualDistance < distance) {
                nearestSheep = sheep;
                distance = actualDistance;
            }
        }
        return nearestSheep;
    }

    public Sheep getClosestVisibleSheep() {
        double distance = Double.POSITIVE_INFINITY;
        Sheep nearestSheep = null;
        for (Sheep sheep :
                sheepArrayList) {
            double actualDistance = countDistance(sheep);
            if (actualDistance < visual_range && actualDistance < distance) {
                nearestSheep = sheep;
                distance = actualDistance;
            }
        }
        return nearestSheep;
    }

    public Circle getCircle() {
        return circle;
    }

    public boolean isLeft(double aX, double aY, double bX, double bY, double cX, double cY) {
        return ((bX - aX) * (cY - aY) - (bY - aY) * (cX - aX)) > 0;
    }

    private int randomWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    public void death() {
        alive = false;
        circle.setOpacity(0);
        timeline.stop();
        circle.setFill(Color.BLACK);
        wolfCollection.remove(this);
    }

    public void updateViewAndBorder() {
        visual_range = 7 * r * 2;
        protected_range_squared = 6 * r * r;
    }

    public void initSpeed() {
        double randomAngle = Math.random() * 360;
        speedX = speed * Math.sin(Math.toRadians(randomAngle));
        speedY = speed * Math.cos(Math.toRadians(randomAngle));
    }
}
