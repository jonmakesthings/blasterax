package Blasteraks;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;

public class GameObject {

    private Node view;

    private Point2D centerPoint = new Point2D(0, 0);
    private Point2D velocity = new Point2D(0, 0);
    private double accelerationFactor = .03;

    public Point2D bulletSpawn = new Point2D(530, 505);

    private boolean alive = true;
    private int health = 400;

    private int objTimer;

    //Basic constructor.
    public GameObject(Node view) {
        this.view = view;
    }

    //Constructor for asteroids.
    public GameObject(Node view, Point2D velocity) {
        this.view = view;

    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int delta) {
        health += delta;
    }

    //Updates visual position. Here the velocity variable, though just a point in 2D space, is being treated
    //as a speed because its values are being added directly to those of the GameObject.
    public void updatePos() {
        view.setTranslateX(view.getTranslateX() + velocity.getX());
        view.setTranslateY(view.getTranslateY() + velocity.getY());
    }

    //Uses the object's bounds to determine where its center point is at any particular time. For some reason
    //a correction of -4 is necessary on both axes. I don't know why.
    public void setCenterPoint() {

        Bounds bounds = getView().getBoundsInParent();
        centerPoint = new Point2D(bounds.getMinX() + bounds.getWidth() /2 -4, bounds.getMinY() + bounds.getHeight() /2 -4);
    }

    public Point2D getCenterPoint() {
        return centerPoint;
    }

    public void setVelocity(Point2D velocity ) {
        this.velocity = velocity;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    //AccelerationFactor is used to alter objects' speeds.
    public double getAccelerationFactor() {
        return accelerationFactor;
    }

    public Point2D getBulletSpawn() {
        return bulletSpawn;
    }

    public void setBulletSpawn(Point2D newSpawn) {
        bulletSpawn = newSpawn;
    }

    public void updateBulletSpawn() {
        setCenterPoint();
        System.out.println("player center point is: " + getCenterPoint());
        System.out.println("bullet spawn is at: " + getBulletSpawn());
        double angle = getView().getRotate();
        setBulletSpawn(new Point2D(Math.cos(Math.toRadians(angle)) * 14 + getCenterPoint().getX(), Math.sin(Math.toRadians(angle)) * 14 + getCenterPoint().getY()) );

        /*x = Cos(angle) * radius + CenterX;
        Y = Sin(angle) * radius + CenterY;*/

        //angle = bulletSpawn.angle(centerPoint);
        //angle = centerPoint.angle(bulletSpawn);

        /*double x1 = bulletSpawn.getX() - centerPoint.getX();
        double y1 = bulletSpawn.getY() - centerPoint.getY();

        double x2 = x1 * Math.cos(Math.toRadians(angle)) - y1 * Math.sin(Math.toRadians(angle));
        double y2 = x1 * Math.sin(Math.toRadians(angle)) + y1 * Math.cos(Math.toRadians(angle));

        //setBulletSpawn(new Point2D(x2 + getCenterPoint().getX(), y2 + getCenterPoint().getY()));
        setBulletSpawn(new Point2D(Math.cos(Math.toRadians(getRotate()) + getCenterPoint().getX()), Math.sin(Math.toRadians(getRotate()) + getCenterPoint().getY())));*/


        /*double x1 = point.x - center.x;
        double y1 = point.y - center.y;

        double x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle));
        double y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle));

        point.x = x2 + center.x;
        point.y = y2 + center.y;*/
    }

    public Node getView() {
        return view;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDead() {
        return !alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setObjTimer() {
        objTimer +=1;
    }

    public int getObjTimer() {
        return objTimer;
    }

    //Reflects GameObjects that reach an edge around to the other side of the actionPane.
    public void reflect(double width, double height) {
        if (this.getView().getTranslateX() > width) {
            this.getView().setTranslateX(0);
        }

        if (this.getView().getTranslateX() < 0) {
            this.getView().setTranslateX(width);
        }

        if (this.getView().getTranslateY() > height) {
            this.getView().setTranslateY(0);
        }

        if (this.getView().getTranslateY() < 0) {
            this.getView().setTranslateY(height);
        }
    }

    public double getRotate() {
        return view.getRotate();
    }

    //-----Combat methods.

    //-----Movement methods.

    public void stop () {
        setVelocity(new Point2D(0, 0));
    }

    public void rotateRight () {
        view.setRotate(view.getRotate() + 5);
        //bulletSpawn = new Point2D(velocity.getX() + velocity.distance(bulletSpawn) * Math.cos(Math.toRadians(45)), velocity.getY() + velocity.distance(bulletSpawn) * Math.sin(Math.toRadians(45)));

        //setVelocity(new Point2D(Math.cos(Math.toRadians(rot)), Math.sin(Math.toRadians(rot))));
        //setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
        /*x = cx + r * cos(a)
        y = cy + r * sin(a)
        Where r is the radius, cx,cy the origin, and a the angle.*/
    }

    public void rotateLeft () {
        view.setRotate(view.getRotate() - 5);
        System.out.println(view.getRotate());
        //setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    public void space () {
    }

    public boolean isColliding(GameObject other) {
        return this.getView().getBoundsInParent().intersects(other.getView().getBoundsInParent());
    }
}
