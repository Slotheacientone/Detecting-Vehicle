package model;

public class EuclideanDistance implements Comparable<EuclideanDistance>{
    private ObjectTracker registeredObject;
    private ObjectTracker newObject;
    private double distance;

    public EuclideanDistance(ObjectTracker registeredObject, ObjectTracker newObject, double distance) {
        this.registeredObject = registeredObject;
        this.newObject = newObject;
        this.distance = distance;
    }

    public ObjectTracker getRegisteredObject() {
        return registeredObject;
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
