package model;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.tracking.MultiTracker;
import org.opencv.tracking.Tracker;
import org.opencv.tracking.TrackerCSRT;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CarDetection {
    public void detect(String file, ImageView imageView, Label label) {
        //Load libary
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //Get video
        VideoCapture videoCapture = new VideoCapture(file);
        //Get fps of video
        double fps = videoCapture.get(Videoio.CAP_PROP_FPS);
        System.out.println("Fps: " + fps);
        //Get trained file
        CascadeClassifier cascadeClassifier = new CascadeClassifier("cars.xml");
        CentroidTracker centroidTracker = new CentroidTracker(50);
        //Create a runnable that detect car every frame
        Runnable frameGraber = new Runnable() {
            @Override
            public void run() {
                //get a frame from video
                Mat frame = new Mat();
                videoCapture.read(frame);
                // gray scale frame
                Mat grayScaleFrame = new Mat();
                Imgproc.cvtColor(frame, grayScaleFrame, Imgproc.COLOR_BGR2GRAY);
                //detect a car
                MatOfRect carDetections = new MatOfRect();
                cascadeClassifier.detectMultiScale(grayScaleFrame, carDetections);
                //with every car detected draw a rectangle around it
                for (Rect car : carDetections.toArray()) {
                    Imgproc.rectangle(frame, car, new Scalar(0, 255, 0));
                    Size textSize = Imgproc.getTextSize("Car", Imgproc.FONT_HERSHEY_PLAIN, 0.8, 1, null);
                    Imgproc.rectangle(frame, new Point(car.x, car.y - textSize.height - 2), new Point(car.x + textSize.width, car.y), new Scalar(0, 255, 0), -1);
                    Imgproc.putText(frame, "Car", new Point(car.x, car.y - 1), Imgproc.FONT_HERSHEY_PLAIN, 0.8, new Scalar(0, 0, 0), 1);
                }
                centroidTracker.update(carDetections);
                int count = centroidTracker.getCountObject();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        label.setText("Car: " + count);
                    }
                });
                //show the frame on ImageView
                imageView.setImage(matToJavaFXImage(frame));
            }
        };
        //Schedule to run frameGraber
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(frameGraber, 0, (long) (1000 / fps), TimeUnit.MILLISECONDS);
    }
    //Convert Mat to javaFXImage
    private Image matToJavaFXImage(Mat original) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".bmp", original, matOfByte);
        return new Image(new ByteArrayInputStream(matOfByte.toArray()));
    }
}
