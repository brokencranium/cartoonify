package com.buildstuff.vv.ip;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class DetectFaceDemo {
	public void run() throws IOException {
	    System.out.println("\nRunning DetectFaceDemo");

	    // Create a face detector from the cascade file in the resources
	    // directory.
	     String resPathXml = getClass().getResource("/resources/lbpcascade_frontalface.xml").getPath();
	     System.out.println("Path " + resPathXml);
	    CascadeClassifier faceDetector = new CascadeClassifier(resPathXml);
	    Mat image = Imgcodecs.imread(getClass().getResource("/resources/lena.png").getPath());

	    // Detect faces in the image.
	    // MatOfRect is a special container class for Rect.
	    MatOfRect faceDetections = new MatOfRect();
	    faceDetector.detectMultiScale(image, faceDetections);

	    System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

	    // Draw a bounding box around each face.
	    for (Rect rect : faceDetections.toArray()) {
	        Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	        System.out.println("Rectangle" + "Height " + rect.height + "Width " +  rect.width + "X " + rect.x + "Y " + rect.y);
	    }

	    // Save the visualized detection.
	    String filename = "faceDetection.png";
	    System.out.println(String.format("Writing %s", filename));
	    Imgcodecs.imwrite(filename, image);
	    Desktop desktop = Desktop.getDesktop();
	    File file = new File(filename);
	    desktop.open(file);
	  }
	}

	 class HelloOpenCV {
	  public static void main(String[] args) {
	    System.out.println("Hello, OpenCV");

	    // Load the native library.
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	    try {
			new DetectFaceDemo().run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
}
