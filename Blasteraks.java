/*
Some notes on structure to avoid confusion:

The Stage object is the top-level UI container; it is a type of window. Scene comes after it, then Pane.

Near the end of the main Java file we use stage.setScene() and
pass it a new Scene whose argument is an HBox, which is a type of Pane. Within this HBox are two Panes, one
called actionPane and the other called dataPane. These represent the two main sections of the game's UI.
*/

package Blasteraks;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Screen;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;

//This base class will always extend Application in JFX. This is the starting point for everything else.
public class Blasteraks extends Application {

    //outerPane is where graphics and objects are stored. actionPane has test data.
    private HBox outerPane = new HBox();
    private Pane actionPane = new Pane();
    private Pane dataPane = new Pane();

    int stageWidth = 1000;
    int timerCycles = 0;

    //dataPane stuff.


    //--------------------------------------Text stuff..
    //private int pHP = 0;
    private double spd = 0;

    //--------------------------------------Game objects.
    //These lists hold bullets and asteroids, which are both instances of the class GameObject.
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> asteroids = new ArrayList<>();

    //We don't need a list to store the player, since there's only one.
    private GameObject player;
    //--------------------------------------Input.
    //Flags used to determine whether the player is currently hitting an input key.
    boolean keyLeft = false;
    boolean keyRight = false;
    boolean keyUp = false;
    boolean keyF = false;
    boolean keyDown = false;
    boolean keySpace = false;
    //--------------------------------------Difficulty.
    //Stores game difficulty, which is used later in various things that affect difficulty.
    int difficulty = 1;
    //--------------------------------------Panes: outerPane and actionPane.
    //Creates and returns a Pane called outerPane.
    private Parent createMainContent() {

        //Define some qualities of the outer Pane.
        outerPane.setPrefSize(1000, 800);

        //Define some qualities of the action Pane.
        actionPane.setPrefSize(800, outerPane.getPrefHeight());
        actionPane.setStyle("-fx-background-color: black;");

        //Define some qualities of the data Pane.
        dataPane.setPrefSize(200, (outerPane.getPrefHeight()/2));
        dataPane.setStyle("-fx-background-color: yellow;");

        //Create player object.
        player = new Player();
        player.setVelocity(new Point2D(0,0));

        //Define some information to be shown in dataPane.
        Text playerHealth = new Text(0, 20, "Health: " + player.getHealth());
        playerHealth.setFill(Color.RED);

        Text playerLocation = new Text(0, 40,"Location: " + player.getCenterPoint());
        playerLocation.setFill(Color.BLACK);


        Text playerSpeed = new Text(0, 60,"Speed: " + player.getVelocity().magnitude());
        playerSpeed.setTranslateY(10);

        dataPane.getChildren().addAll(playerHealth, playerLocation, playerSpeed);

        //Set player model to existing model (FXML).
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("rocket"));
        //Group graphic = loader.load(rocket);

        //Add player object as a child of actionPane. Sets its location to the center of the preferred dimensions.
        addGameObject(player, actionPane.getPrefWidth()/2, actionPane.getPrefHeight()/2);

        /*
        Creates and starts an AnimationTimer, an abstract class. The handle() method simply calls the onUpdate()
        method every 1/60 of a second. We can .start() it or .stop() it.
        */

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        outerPane.getChildren().addAll(actionPane, dataPane);
        return outerPane;
    }

    private Parent createSecondaryContent() {
        actionPane.setPrefSize(300, 800);
        /*object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        outerPane.getChildren().add(object.getView());*/


        return actionPane;
    }



    //Spawns bullets and adds them to their list. Uses method addGameObject.
    private void addBullet(GameObject bullet, double x, double y) {
        bullets.add(bullet);
        addGameObject(bullet, x, y);
    }

    //Spawns asteroids and adds them to their list. Uses method addGameObject.
    private void addAsteroid(GameObject asteroid, double x, double y) {
        asteroids.add(asteroid);
        addGameObject(asteroid, x, y);
    }

    //Adds objects to the action Pane.
    private void addGameObject(GameObject object, double x, double y) {

        //Sets the GameObject's location within the scene graph.
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);

        //Adds the GameObject's node to actionPane.
        actionPane.getChildren().add(object.getView());
    }

    private void addSecondaryGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        actionPane.getChildren().add(object.getView());
    }

    //Performs various functions that need to happen frequently.
    private void onUpdate() {

        // Ensures that timerCycles, which is used for timing certain things, is always going between 0 and 59.
        if (timerCycles == 60) {

            timerCycles = 0;
        }

        // Updates number of times we've been through the onUpdate() function in order to time certain things, like
        // decay of bullets and bullet spawn speed.
        timerCycles++;

        // Updates the player health, location, and speed displays in the data pane. Uses a DecimalFormat for
        // formatting.
        DecimalFormat tempdf = new DecimalFormat("#.#");

        Text currentHealth = new Text(0, 20,"Health: " + player.getHealth());
        dataPane.getChildren().set(0, currentHealth);

        double x = player.getCenterPoint().getX();
        double y = player.getCenterPoint().getY();
        Text currentLocation = new Text(0, 40,"Location: " + tempdf.format(x) + ", " + tempdf.format(y));
        dataPane.getChildren().set(1, currentLocation);

        tempdf = new DecimalFormat("#.##");

        double speed = player.getVelocity().magnitude();
        Text currentSpeed = new Text(0, 60,"Speed: " + tempdf.format(speed));
        dataPane.getChildren().set(2, currentSpeed);

        //Check if the player's at an edge and move it if it is.
        player.reflect(actionPane.getPrefWidth(), actionPane.getPrefHeight());

        //Using the flags created earlier, check if the player is hitting a key and act accordingly.
        if (keyLeft) {
            player.rotateLeft();
        }

        if (keyRight) {
            player.rotateRight();
        }

        if (keyUp) {




            if(player.getVelocity().equals(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))))){
                player.setVelocity((player.getVelocity().multiply(1.01)));
            } else if(player.getVelocity().equals(new Point2D(0.0, 0.0))) {
                player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))));
                player.setVelocity((player.getVelocity().multiply(0.10)));
            } else {
                Point2D temp = new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate())));
                temp = temp.multiply(player.getAccelerationFactor());
                player.setVelocity(player.getVelocity().add(temp));
            }


        }

        if (keyF) {
            if (player.getVelocity().equals(new Point2D(0, 0))) {
                player.setVelocity(player.getVelocity());
            } else {
                player.setVelocity(player.getVelocity().multiply(.99));
            }
        }

        if (keyDown) {
            if(player.getVelocity().equals(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))))){
                player.setVelocity((player.getVelocity().multiply(-1.01)));
            } else if(player.getVelocity().equals(new Point2D(0.0, 0.0))) {
                player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))));
                player.setVelocity((player.getVelocity().multiply(-0.10)));
            } else {
                Point2D temp = new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate())));
                temp = temp.multiply(-player.getAccelerationFactor());
                player.setVelocity(player.getVelocity().add(temp));
            }
        }

        if(keySpace) {

            //Allows the player to fire every 1/10 of a second.
            if(timerCycles % 6 == 0) {
                Bullet bullet = new Bullet();
                Point2D adjustedVelocity = new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate())));
                adjustedVelocity = adjustedVelocity.multiply(10.0);
                adjustedVelocity = adjustedVelocity.add(player.getVelocity().getX(), player.getVelocity().getY());
                bullet.setVelocity(adjustedVelocity);

                player.updateBulletSpawn();

                addBullet(bullet, player.getBulletSpawn().getX(), player.getBulletSpawn().getY());
            }


            /*
            The following code is useful for debugging issues with the center point. It makes the center point of
            the player object leave a trail of bullets.
            player.setCenterPoint();
            Bullet bullet2 = new Bullet();
            addBullet(bullet2, player.getCenterPoint().getX(), player.getCenterPoint().getY());
            */
        }

        /*
        Each bullet is checked for collision. When a bullet collides with an asteroid, both the
        asteroid and the bullet are set to dead and are removed from the game.
        Note: loop will not be executed unless a bullet currently exists.
        */
        for (GameObject bullet : bullets) {
            bullet.reflect(actionPane.getPrefWidth(), actionPane.getPrefHeight());
            for (GameObject asteroid : asteroids) {
                //Removes from the view all bullets that have collided with asteroids and those asteroids they collided with.
                if(bullet.isColliding(asteroid)) {
                    bullet.setAlive(false);
                    asteroid.setAlive(false);

                    //Removes from actionPane any bullets and asteroids involved in collisions.
                    actionPane.getChildren().removeAll(bullet.getView(), asteroid.getView());
                }
            }

            //Limits lifetime of bullets to 90 seconds.
            bullet.setObjTimer();
            if (bullet.getObjTimer() >= 90) {
                bullet.setAlive(false);
                actionPane.getChildren().remove(bullet.getView());
            }

                //Removes out of bounds bullets.
            /*if((bullet.getView().getTranslateX() > outerPane.getPrefWidth()) || (bullet.getView().getTranslateY() > outerPane.getPrefHeight()) || bullet.getView().getTranslateX() < 0 || bullet.getView().getTranslateY() < 0) {
                bullet.setAlive(false);
                outerPane.getChildren().remove(bullet.getView());
            }*/

        }

        //Checks for and reflects out of bounds asteroids.
        for (GameObject asteroid : asteroids) {
            asteroid.reflect(actionPane.getPrefWidth(), actionPane.getPrefHeight());
        }



        //Removes from the model all bullets and asteroids set to dead (those involved in collisions or out of bounds).
        bullets.removeIf(GameObject::isDead);
        asteroids.removeIf(GameObject::isDead);

        //Update positions for each GameObject.
        bullets.forEach(GameObject::updatePos);
        asteroids.forEach(GameObject::updatePos);
        player.updatePos();

        //1% chance (each tick) that an asteroid will be spawned in a random location.
        switch(difficulty) {
            case 1:
                if (Math.random() <= .01) {
                    Asteroid ast = new Asteroid();
                    ast.getView().setRotate(Math.random() * (Math.random() * 100000));
                    ast.setVelocity(new Point2D(Math.cos(Math.toRadians(ast.getRotate())), Math.sin(Math.toRadians(ast.getRotate()))));
                    ast.setVelocity((ast.getVelocity().multiply(Math.random() * 2.4 )));

                    //Add the asteroid at a random location in actionPane.
                    addAsteroid(ast, Math.random() * actionPane.getPrefWidth(), Math.random() * actionPane.getPrefHeight());
                }
        }

    }





    public double getSpd(){
        return spd;
    }

    public void setSpd(double sp) {
        spd = sp;
    }

    private static class Player extends GameObject {
        Player() {
            super(new Rectangle(40,20, Color.BLUE));
        }
    }

    private static class Asteroid extends GameObject {
        Asteroid() {
            super(new Circle(15,15, 15, Color.SADDLEBROWN));
        }
    }

    private static class Bullet extends GameObject {
        Bullet() {
            super(new Circle(4,4, 4, Color.WHITESMOKE));
        }
    }

    //Start method, which begins the game and handles events.
    @Override
    public void start(Stage primaryStage) throws Exception {

        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Dev Data");
        secondaryStage.setScene(new Scene(createSecondaryContent(), Color.BLACK));

        //Specifies the scene to be used on the stage. A new Scene is created and loaded with the basic
        //play elements.
        Scene mainScene = new Scene(createMainContent(), 800, 800);
        //Scene mainScene = new Scene(createMainContent(), 800, 800, Color.BLACK);
        //mainScene.setFill(Color.BLACK);
        //mainScene.get
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Blasteraks");





        //Handles input.
        primaryStage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                keyLeft = true;
            }

            if (e.getCode() == KeyCode.RIGHT) {
                keyRight = true;
            }

            if (e.getCode() == KeyCode.UP) {
                keyUp = true;
            }

            if (e.getCode() == KeyCode.F) {
                keyF = true;
            }

            if (e.getCode() == KeyCode.DOWN) {
                keyDown = true;
            }

            if (e.getCode() == KeyCode.SPACE) {
                keySpace = true;
            }
        });

        primaryStage.getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                keyLeft = false;
            }

            if (e.getCode() == KeyCode.RIGHT) {
                keyRight = false;
            }

            if (e.getCode() == KeyCode.UP) {
                keyUp = false;
            }

            if (e.getCode() == KeyCode.F) {
                keyF = false;
            }

            if (e.getCode() == KeyCode.DOWN) {
                keyDown = false;
            }

            if (e.getCode() == KeyCode.SPACE) {
                keySpace = false;
            }

        });


        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setWidth(stageWidth);

        //set Stage boundaries to the lower right corner of the visible bounds of the main screen.
        /*primaryStage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - outerPane.getPrefWidth());
        primaryStage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - (outerPane.getPrefHeight() +30));

        secondaryStage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - (outerPane.getPrefWidth()+ actionPane.getPrefWidth()));
        secondaryStage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() -(outerPane.getPrefHeight()+30));*/


        /*This can be used to set the game to full screen.
        Rectangle2D screenBoundary = Screen.getPrimary().getVisualBounds();

        stage.setX(screenBoundary.getMinX());
        stage.setY(screenBoundary.getMinY());
        stage.setWidth(screenBoundary.getWidth());
        stage.setHeight(screenBoundary.getHeight());*/

        //Display the primaryStage last so that it'll have focus when the program starts.
        //secondaryStage.show();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
