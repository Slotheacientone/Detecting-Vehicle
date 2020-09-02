package model;

public class EuclideanDistance implements Comparable<EuclideanDistance>{
    private ObjectTracker oldObject;
    private ObjectTracker newObject;
    private double distance;

    public EuclideanDistance(ObjectTracker registeredObject, ObjectTracker newObject, double distance) {
        this.oldObject = registeredObject;
        this.newObject = newObject;
        this.distance = distance;
    }

    public ObjectTracker getOldObject() {
        return oldObject;
    }

    public ObjectTracker getNewObject() {
        return newObject;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(EuclideanDistance o) {
        return Double.compare(distance, o.getDistance());
    }
}
