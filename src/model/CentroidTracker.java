package model;

import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Collections;

public class CentroidTracker {
    private ArrayList<ObjectTracker> listOldObject;
    private int maxDisappearFrame;
    private int countObject;

    public CentroidTracker(int maxDisappearFrame) {
        this.countObject = 0;
        this.maxDisappearFrame = maxDisappearFrame;
	    listOldObject = new ArrayList<>();
    }
    
//calculate euclidean distance between 2 objects
    public EuclideanDistance calculateEuclideanDistance(ObjectTracker o1, ObjectTracker o2) {
        double distance = Math.sqrt((o1.getX() - o2.getX()) * (o1.getX() - o2.getX()) + (o1.getY() - o2.getY()) * (o1.getY() - o2.getY()));
	return new EuclideanDistance(o1, o2, distance);
    }
//generate list smallest distance between old object and new object
    public ArrayList<EuclideanDistance> generateListSmallestDistance(ArrayList<ObjectTracker> listNewObject) {
        ArrayList<EuclideanDistance> listDistance = new ArrayList<>();
        for (ObjectTracker oldObject : listOldObject) {
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
            //method reference
            listOldObject.forEach(ObjectTracker::addDisappearFrame);
            return;
        }
        //create list of object detected
        ArrayList<ObjectTracker> listNewObject = new ArrayList<>();
        for (Rect rect : matOfRect.toArray()) {
            double x = rect.x + rect.width / 2.0;
            double y = rect.y + rect.height / 2.0;
            ObjectTracker objectTracker = new ObjectTracker();
            objectTracker.setX(x);
            objectTracker.setY(y);
            listNewObject.add(objectTracker);
        }
        //if there is no old object then register all new object
        if (listOldObject.isEmpty()) {
            for (ObjectTracker objectTracker : listNewObject) {
                countObject++;
                objectTracker.setObjectID(countObject);
                listOldObject.add(objectTracker);
            }
            return;
        }
        //remove old object have disappear frame count = maxDisappearFrame
        //lambad expression
        listOldObject.removeIf(objectTracker -> objectTracker.getDisappearFrame() >= maxDisappearFrame);
        //add disappear frame count for all old object
        listOldObject.forEach(ObjectTracker::addDisappearFrame);
        //create list of smallest distance between old object and new object
        ArrayList<EuclideanDistance> listDistance = generateListSmallestDistance(listNewObject);
        //create list of new object that already updated
        ArrayList<ObjectTracker> lock = new ArrayList<>();
        //update new location for old object
        for(EuclideanDistance euclideanDistance:listDistance){
            if(!lock.contains(euclideanDistance.getNewObject())){
                euclideanDistance.getOldObject().setX(euclideanDistance.getNewObject().getX());
                euclideanDistance.getOldObject().setY(euclideanDistance.getNewObject().getY());
                euclideanDistance.getOldObject().removeDisappearFrame();
                lock.add(euclideanDistance.getNewObject());
            }
        }
        //if there is object detected that not already update then register it
        if(lock.size()!=listNewObject.size()){
            for(ObjectTracker objectTracker:listNewObject){
                if(!lock.contains(objectTracker)){
                    countObject++;
                    objectTracker.setObjectID(countObject);
                    listOldObject.add(objectTracker);
                }
            }
        }

    }
    public int getCountObject(){
        return this.countObject;
    }
}
