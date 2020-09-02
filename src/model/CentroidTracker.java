package model;

import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Collections;

public class CentroidTracker {
    private final ArrayList<ObjectTracker> listObject;
    private final int maxDisappearFrame;
    private int countObject;

    public CentroidTracker(int maxDisappearFrame) {
        this.countObject = 0;
        this.maxDisappearFrame = maxDisappearFrame;
        listObject = new ArrayList<>();
    }
//calculate euclidean distance between 2 objects
    public EuclideanDistance calculateEuclideanDistance(ObjectTracker o1, ObjectTracker o2) {
        double distance = Math.sqrt((o1.getX() - o2.getX()) * (o1.getX() - o2.getX()) + (o1.getY() - o2.getY()) * (o1.getY() - o2.getY()));
        return new EuclideanDistance(o1, o2, distance);
    }
//generate list smallest distance between old object and new object
    public ArrayList<EuclideanDistance> generateListDistance(ArrayList<ObjectTracker> listNewObject) {
        ArrayList<EuclideanDistance> listDistance = new ArrayList<>();
        for (ObjectTracker oldObject : listObject) {
            EuclideanDistance smallestDistance = null;
            double distance = Double.MAX_VALUE;
            for (ObjectTracker newObject : listNewObject) {
                EuclideanDistance e = calculateEuclideanDistance(oldObject, newObject);
                if (distance > e.getDistance()) {
                    smallestDistance = e;
                    distance = e.getDistance();
                }
            }
            listDistance.add(smallestDistance);
        }
        Collections.sort(listDistance);
        return listDistance;
    }
//update location for old object, remove disappear object and register new object
    public void update(MatOfRect matOfRect) {
        //return if there is no new object detected
        if(matOfRect.toArray().length==0){
            return;
        }
        //create list of object detected
        ArrayList<ObjectTracker> temp = new ArrayList<>();
        for (Rect rect : matOfRect.toArray()) {
            double x = rect.x + rect.height / 2.0;
            double y = rect.y + rect.width / 2.0;
            ObjectTracker objectTracker = new ObjectTracker();
            objectTracker.setX(x);
            objectTracker.setY(y);
            temp.add(objectTracker);
        }
        //if there is no old object then register all new object
        if (listObject.isEmpty()) {
            for (ObjectTracker objectTracker : temp) {
                countObject++;
                objectTracker.setObjectID(countObject);
                listObject.add(objectTracker);
            }
            return;
        }
        //remove old object have disappear frame count = maxDisappearFrame
        listObject.removeIf(objectTracker -> objectTracker.getDisappearFrame() >= maxDisappearFrame);
        //add disappear frame count for all old object
        listObject.forEach(ObjectTracker::addDisappearFrame);
        //create list of smallest distance between old object and new object
        ArrayList<EuclideanDistance> listDistance = generateListDistance(temp);
        //create list of new object that already updated
        ArrayList<ObjectTracker> lock = new ArrayList<>();
        //update new location for old object
        for(EuclideanDistance euclideanDistance:listDistance){
            if(!lock.contains(euclideanDistance.getNewObject())){
                euclideanDistance.getRegisteredObject().setX(euclideanDistance.getNewObject().getX());
                euclideanDistance.getRegisteredObject().setY(euclideanDistance.getNewObject().getY());
                euclideanDistance.getRegisteredObject().removeDisappearFrame();
                lock.add(euclideanDistance.getNewObject());
            }
        }
        //if there is object detected that not already update then register it
        if(lock.size()!=temp.size()){
            for(ObjectTracker objectTracker:temp){
                if(!lock.contains(objectTracker)){
                    countObject++;
                    objectTracker.setObjectID(countObject);
                    listObject.add(objectTracker);
                }
            }
        }

    }
    public int getCountObject(){
        return this.countObject;
    }
}
