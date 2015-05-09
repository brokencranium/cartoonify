package com.buildstuff.vv.ip;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class FacialPrcoessing {
	public final int DETECTION_WIDTH = 320;

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public FacialPrcoessing() {

	}

	public static MatOfRect findFeatures(Mat src,
			CascadeClassifier featureDetector, int scaledWidth, int flags,
			Size minFeatureSize, Size maxFeatureSize, float searchScaleFactor,
			int minNeighbors) {
		// void detectObjectsCustom(const Mat &img, CascadeClassifier &cascade,
		// vector<Rect>
		// &objects, int scaledWidth, int flags, Size minFeatureSize,
		// float searchScaleFactor, int minNeighbors)
		MatOfRect featureDetections = new MatOfRect();
		Mat gray = new Mat();
		Mat smallImg = new Mat();
		Mat equalizedImg = new Mat();

		// Color to gray based on number of channels
		if (src.channels() == 3) {
			Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		} else if (src.channels() == 4) {
			Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGRA2GRAY);
		} else {
			gray = src;
		}

		// Shrinking camera image and keeping the aspect ratio
		float scale = gray.cols() / (float) scaledWidth;

		if (gray.cols() > scaledWidth) {
			int scaledHeight = Math.round(gray.rows() / scale);
			Imgproc.resize(gray, smallImg, new Size(scaledWidth, scaledHeight));
		} else {
			smallImg = gray;
		}

		// Histogram equalization
		Imgproc.equalizeHist(smallImg, equalizedImg);

		featureDetector.detectMultiScale(smallImg, featureDetections,
				searchScaleFactor, minNeighbors, flags, minFeatureSize,
				maxFeatureSize);

		// Draw a bounding box around each face.
		Rect[] featuresRect = featureDetections.toArray();

		for (int i = 0; i < featuresRect.length; ++i) {
			if (src.cols() > scaledWidth) {
				featuresRect[i].y = Math.round(featuresRect[i].y * scaledWidth);
				featuresRect[i].x = Math.round(featuresRect[i].x * scaledWidth);
				featuresRect[i].width = Math.round(featuresRect[i].width
						* scale);
				featuresRect[i].height = Math.round(featuresRect[i].height
						* scale);
			}
			if (featuresRect[i].x < 0)
				featuresRect[i].x = 0;

			if (featuresRect[i].y < 0)
				featuresRect[i].y = 0;

			if ((featuresRect[i].x + featuresRect[i].width) > src.cols())
				featuresRect[i].x = src.cols() - featuresRect[i].width;
			if ((featuresRect[i].y + featuresRect[i].height) > src.rows())
				featuresRect[i].y = src.rows() - featuresRect[i].height;

		}
		return new MatOfRect(featuresRect);
	}

	public static Rect findLargestFeature(Mat src,
			CascadeClassifier featureDetector, int scaledWidth) {

		MatOfRect faceDetections = new MatOfRect();

		// Face detection
		int flags = Objdetect.CASCADE_FIND_BIGGEST_OBJECT;
		Size minFeatureSize = new Size(20, 20);
		Size maxFeatureSize = new Size(80, 80);
		float searchScaleFactor = 1.1f;
		int minNeighbors = 4;

		// findFeatures(Mat src,
		// CascadeClassifier featureDetector, int scaledWidth, int flags,
		// Size minFeatureSize, Size maxFeatureSize, float searchScaleFactor,
		// int minNeighbors) {
		//
		faceDetections = findFeatures(src, featureDetector, scaledWidth, flags,
				minFeatureSize, maxFeatureSize, searchScaleFactor, minNeighbors);

		for (Rect faceRect : faceDetections.toArray()) {
			Imgproc.rectangle(src, new Point(faceRect.x, faceRect.y),
					new Point(faceRect.x + faceRect.width, faceRect.y
							+ faceRect.height), new Scalar(0, 255, 0));
			return faceRect;
		}

		return null;
	}

	public Mat detectFace(Mat src) {

		String resPathXml = getClass().getResource(
				"/resources/lbpcascade_frontalface.xml").getPath();
		System.out.println("Path " + resPathXml);
		CascadeClassifier faceDetector = new CascadeClassifier(resPathXml);

		Rect faceRect = findLargestFeature(src, faceDetector, DETECTION_WIDTH);
		return src.submat(faceRect);
	}

	public void extractEyes(Mat src) {
		final float EYE_SX = 0.16f;
		final float EYE_SY = 02.6F;
		final float EYE_SW = 0.30f;
		final float EYE_SH = 0.28F;

		int leftX = Math.round(src.cols() * EYE_SX);
		int rightX = Math.round(src.cols() * (1.0f - EYE_SX - EYE_SW));

		int topY = Math.round(src.rows() * EYE_SY);
		int widthX = Math.round(src.cols() * EYE_SW);
		int heightY = Math.round(src.rows() * EYE_SH);

		Mat topLeftOfFace = src.submat(new Rect(leftX, topY, widthX, heightY));
		Mat topRightOfFace = src
				.submat(new Rect(rightX, topY, widthX, heightY));

		String resPathXml1 = getClass().getResource(
				"/resources/haarcascade_eye.xml").getPath();
		String resPathXml2 = getClass().getResource(
				"/resources/haarcascade_eye_tree_eyeglasses.xml").getPath();

		CascadeClassifier eyeDetector1 = new CascadeClassifier(resPathXml1);
		CascadeClassifier eyeDetector2 = new CascadeClassifier(resPathXml2);

		// Left eye
		Rect leftEyeRect = findLargestFeature(src, eyeDetector1,
				topLeftOfFace.cols());

		if (leftEyeRect.width <= 0) {
			leftEyeRect = findLargestFeature(src, eyeDetector2,
					topLeftOfFace.cols());
		}

		Point leftEye = new Point(-1, -1);

		if (leftEyeRect.width <= 0) {
			leftEye.x = leftEye.x + leftEyeRect.width / 2 + leftX;
			leftEye.y = leftEye.y + leftEyeRect.height / 2 + topY;
		}

		// Right eye
		Rect rightEyeRect = findLargestFeature(src, eyeDetector1,
				topRightOfFace.cols());
		if (rightEyeRect.width <= 0) {
			rightEyeRect = findLargestFeature(src, eyeDetector2,
					topRightOfFace.cols());
		}

		Point rightEye = new Point(-1, -1);

		if (rightEyeRect.width <= 0) {
			rightEye.x = rightEye.x + rightEyeRect.width / 2 + rightX;
			rightEye.y = rightEye.y + rightEyeRect.width / 2 + topY;
		}
		
		
//		Check if both eyes were detected
		
	}
}
