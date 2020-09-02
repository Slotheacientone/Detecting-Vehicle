package model;

import java.util.Objects;

public class ObjectTracker {
    private int objectID;
    private double x;
    private double y;
    private int disappearFrame;

    public ObjectTracker(int objectID) {
        this.objectID = objectID;
        disappearFrame = 0;
    }
    public ObjectTracker() {
        disappearFrame = 0;
    }

    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

    public int getObjectID() {
        return objectID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDisappearFrame() {
        return disappearFrame;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void addDisappearFrame() {
        disappearFrame++;
    }
    public void removeDisappearFrame() {
        disappearFrame--;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ObjectTracker)){
            return false;
        }
        ObjectTracker that = (ObjectTracker) o;
        return (x== that.getX()&&y== that.getY());
    }

}
