package model;

import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Collections;

public class CentroidTracker {
    private ArrayList<ObjectTracker> listObject;
    private int maxDisappearFrame;
    private int countObject;

    public CentroidTracker(int maxDisappearFrame) {
        this.countObject = 0;
        this.maxDisappearFrame = maxDisappearFrame;
        listObject = new ArrayList<>();
    }

    public EuclideanDistance calculateEuclideanDistance(ObjectTracker o1, ObjectTracker o2) {
        double distance = Math.sqrt((o1.getX() - o2.getX()) * (o1.getX() - o2.getX()) + (o1.getY() - o2.getY()) * (o1.getY() - o2.getY()));
        return new EuclideanDistance(o1, o2, distance);
    }

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

    public void update(MatOfRect matOfRect) {
        if(matOfRect.toArray().length==0){
            return;
        }
        ArrayList<ObjectTracker> temp = new ArrayList<>();
        for (Rect rect : matOfRect.toArray()) {
            double x = rect.x + rect.height / 2.0;
            double y = rect.y + rect.width / 2.0;
            ObjectTracker objectTracker = new ObjectTracker();
            objectTracker.setX(x);
            objectTracker.setY(y);
            temp.add(objectTracker);
        }
        if (listObject.isEmpty()) {
            for (ObjectTracker objectTracker : temp) {
                countObject++;
                objectTracker.setObjectID(countObject);
                listObject.add(objectTracker);
            }
            return;
        }
        listObject.removeIf(objectTracker -> objectTracker.getDisappearFrame() == maxDisappearFrame);
        listObject.forEach(ObjectTracker::addDisappearFrame);
        ArrayList<EuclideanDistance> listDistance = generateListDistance(temp);
        ArrayList<ObjectTracker> lock = new ArrayList<>();
        for(EuclideanDistance euclideanDistance:listDistance){
            if(!lock.contains(euclideanDistance.getNewObject())){
                euclideanDistance.getRegisteredObject().setX(euclideanDistance.getNewObject().getX());
                euclideanDistance.getRegisteredObject().setY(euclideanDistance.getNewObject().getY());
                euclideanDistance.getRegisteredObject().removeDisappearFrame();
                lock.add(euclideanDistance.getNewObject());
            }
        }
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
