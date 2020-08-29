package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CarDetection {
    public void detect(String file, ImageView imageVIew) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture videoCapture = new VideoCapture(file);
        double fps = videoCapture.get(Videoio.CAP_PROP_FPS);
        System.out.println("Fps: " + fps);
        CascadeClassifier cascadeClassifier = new CascadeClassifier("cars.xml");
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        Runnable frameGraber = new Runnable() {
            @Override
            public void run() {
                Mat frame = new Mat();
                videoCapture.read(frame);
                Mat grayScaleFrame = new Mat();
                Imgproc.cvtColor(frame, grayScaleFrame, Imgproc.COLOR_BGR2GRAY);
                MatOfRect carDetections = new MatOfRect();
                cascadeClassifier.detectMultiScale(grayScaleFrame, carDetections);
                for (Rect car : carDetections.toArray()) {
                    Imgproc.rectangle(frame, car, new Scalar(0, 255, 0, 255));
                    Size textSize = Imgproc.getTextSize("Car", Imgproc.FONT_HERSHEY_PLAIN, 0.8, 1, null);
                    Imgproc.rectangle(frame, new Point(car.x, car.y - textSize.height-2), new Point(car.x + textSize.width, car.y-1), new Scalar(0, 255, 0, 255), -1);
                    Imgproc.putText(frame, "Car", new Point(car.x, car.y-1), Imgproc.FONT_HERSHEY_PLAIN, 0.8, new Scalar(0, 0, 0, 0), 1);

                }
                imageVIew.setImage(matToJavaFXImage(frame));
            }
        };
        timer.scheduleAtFixedRate(frameGraber, 0, (long) (1000 / fps), TimeUnit.MILLISECONDS);
    }

    private Image matToJavaFXImage(Mat original) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".bmp", original, matOfByte);
        return new Image(new ByteArrayInputStream(matOfByte.toArray()));
    }
}
