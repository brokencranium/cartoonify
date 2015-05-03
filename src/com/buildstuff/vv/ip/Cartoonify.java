package com.buildstuff.vv.ip;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
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

	public void run(char inp) throws IOException, InterruptedException {
		camera = new VideoCapture(0);

		camera.open(cameraNumber);
		Thread.sleep(500);
		if (!camera.isOpened()) {
			return;
		}
		camera.set(CV_CAP_PROP_FRAME_WIDTH, 640);
		camera.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
		Mat cameraFrame = new Mat();
//		Mat displayedFrame;
			camera.read(cameraFrame);
	   Mat cartoon = null;
       if (inp == 'B')  
	     cartoon = this.cartoonifyBW(cameraFrame);
       else if (inp == 'C')
    	 cartoon = this.cartoonifyClr(cameraFrame);  
    	   
		String filename = "cartoonify.png";
		Imgcodecs.imwrite(filename, cartoon);
		Desktop desktop = Desktop.getDesktop();
	    File file = new File(filename);
	    desktop.open(file);
	}
   
	public Mat cartoonifyBW(Mat src){
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
	
	public Mat cartoonifyClr(Mat src){
		Size size = src.size();
		Size smallSize = new Size();
		smallSize.width = size.width/2;
		smallSize.height = size.height/2;
		Mat smallImg = new Mat(smallSize,CvType.CV_8UC3);
		Imgproc.resize(src, smallImg, smallSize,0,0,Imgproc.INTER_LINEAR);
		Mat tmp = new Mat(smallSize, CvType.CV_8UC3);
		int repitions = 7; // Repetitions for cartoon affect 
		
		for(int i = 0; i< repitions; ++i){
			int ksize = 9; //Filter size 
			double sigmaColor = 9; //Filter color strength 
			double sigmaSpace = 7; //Spatial strength
			Imgproc.bilateralFilter(smallImg, tmp, ksize, sigmaColor, sigmaSpace);
			Imgproc.bilateralFilter(tmp, smallImg, ksize, sigmaColor, sigmaSpace);
		}
		
		Mat bigImg = new Mat();
		Imgproc.resize(smallImg, bigImg, size,0,0,Imgproc.INTER_LINEAR);
		Mat dest = new Mat(size,CvType.CV_8UC3);
		dest.setTo(new Scalar(0));
		
		Mat sharp = new Mat();	
		Mat edges = new Mat();
		Mat mask = new Mat();

		// Remove noise by keeping the edges sharp
		  Imgproc.medianBlur(bigImg, sharp, MEDIAN_BLUR_FILTER_SIZE);		
	   //  Produce edges similar to hand sketches
		  Imgproc.Laplacian(sharp, edges,CvType.CV_8U,LAPLACIAN_FILTER_SIZE, 0.50, 1);
		//  Imgproc.Laplacian(src, dst, ddepth, ksize, scale, delta, borderType);
		//  Make edges either black or white
		  Imgproc.threshold(edges, mask, EDGES_THRESHOLD, 255,Imgproc.THRESH_BINARY_INV );
		
		
		bigImg.copyTo(dest,mask);
	
		return dest;
	}
	
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try {
			new Cartoonify().run('C');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
