package com.buildstuff.vv.ip;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Cartoonify {
	private int cameraNumber = 0;
	private VideoCapture camera;
	private static int CV_CAP_PROP_FRAME_WIDTH = 3;
	private static int CV_CAP_PROP_FRAME_HEIGHT = 4;
	
	private static int MEDIAN_BLUR_FILTER_SIZE = 7;
	private static int LAPLACIAN_FILTER_SIZE = 5;
	private static int EDGES_THRESHOLD = 75;

	public void run() throws IOException, InterruptedException {
		camera = new VideoCapture(0);

		camera.open(cameraNumber);
		Thread.sleep(500);
		if (!camera.isOpened()) {
			return;
		}
		camera.set(CV_CAP_PROP_FRAME_WIDTH, 640);
		camera.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
		Mat cameraFrame = new Mat();
		int i = 0;
//		Mat displayedFrame;
			camera.read(cameraFrame);
         
	    Mat cartoon = this.cartoonify(cameraFrame);		
		String filename = "cartoonify.png";
		Imgcodecs.imwrite(filename, cartoon);
		Desktop desktop = Desktop.getDesktop();
	    File file = new File(filename);
	    desktop.open(file);

	}

	public Mat cartoonify(Mat src){
	Mat gray = new Mat();	
	Mat sharp = new Mat();		
	Mat edges = new Mat();
	Mat mask = new Mat();
	
//Convert colored image to gray
	Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
// Remove noise by keeping the edges sharp
  Imgproc.medianBlur(gray, sharp, MEDIAN_BLUR_FILTER_SIZE);
//  Produce edges similar to hand sketches
  Imgproc.Laplacian(sharp, edges,CvType.CV_8U,LAPLACIAN_FILTER_SIZE, 1.25, 1);
//  Imgproc.Laplacian(src, dst, ddepth, ksize, scale, delta, borderType);
//  Make edges either black or white
  Imgproc.threshold(edges, mask, EDGES_THRESHOLD, 255,Imgproc.THRESH_BINARY_INV );
	return mask;	
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try {
			new Cartoonify().run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
