package com.buildstuff.vv.ip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MyFaceFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;
	private JPanel facePane;
	private JFrame frame;
	private JButton button;
	private Mat matOutline;
	private BufferedImage buffImg;
	private VideoCap videoCap = new VideoCap();

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public MyFaceFrame() {
		setTitle("Facial Recognition");
		setLayout(new BorderLayout());
		setSize(1280, 1024);

		button = new JButton("Capture Me");
		add(button, BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {

			// @Override
			public void actionPerformed(ActionEvent e) {
                   
			}
		});

		CamPanel facePanel = new CamPanel();
		// JFrames methods
		add(facePanel, BorderLayout.CENTER);
		// revalidate();
		// repaint();

		setLocationRelativeTo(null); // This is for centering the frame to your
										// screen.
		setDefaultCloseOperation(EXIT_ON_CLOSE); // This for closing your
													// application after you
													// closing the window.

	}

	class CamPanel extends JPanel {
		private static final long serialVersionUID = 7355955185899862102L;

		public CamPanel() {
			setBorder(BorderFactory.createLineBorder(Color.black, 2));
			new MyThread().start();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			matOutline = drawFaceOutline(videoCap.getOneMatFrame());
			
			buffImg = videoCap.getOneBuffFrame(matOutline);
			g.drawImage(buffImg, 0, 0, this);
		}

	}

	class MyThread extends Thread {
		@Override
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public Mat drawFaceOutline(Mat src) {
		Mat dest = new Mat(src.size(), CvType.CV_8UC3);
		Mat faceOutline = new Mat(src.size(), CvType.CV_8UC3);
		faceOutline.setTo(new Scalar(0));

		Scalar color = new Scalar(255, 255, 0, 0);

		int thickness = 4;
		int sw = (int) src.size().width;
		int sh = (int) src.size().height;
		int faceH = Math.round((sh/2 * 70/100)); // radius of the ellipse

		// Scale the width to the same shape for any screen width
		int faceW =   Math.round(faceH * 72/100);

		// Draw the face outline
		Imgproc.ellipse(faceOutline, new Point(sw/2, sh/2), new Size(faceW,
				faceH), 0, 0, 360, color, thickness);
		// Imgproc.ellipse(faceOutline, new Point(sw/2, sh/2), new Size(faceW,
		// faceH),0,0,360, color,thickness,Core.LINE_AA);
//		// Draw the eye outlines, as 2 arcs per eye
		int eyeW = faceW * 23/100;
		int eyeH = faceH * 11/100;
		int eyeX = faceW * 48 / 100;
		int eyeY = faceH * 13 / 100;
		Size eyeSize = new Size(eyeW, eyeH);
//
//		// Set the angle and shift for the eye half ellipse
		int eyeA = 15; // angle in degrees
		int eyeYshift = 11;
//
//		// Draw the top of the right eye
		Imgproc.ellipse(faceOutline, new Point(sw/2 - eyeX, sh/2 - eyeY),
				eyeSize, 0, 180 + eyeA, 360 - eyeA, color, thickness);
//		// Imgproc.ellipse(faceOutline, new Point(sw/2 - eyeX, sh/2 - eyeY),
//		// eyeSize, 0, 180 + eyeA, 360 - eyeA, color, thickness,Core.Line_AA);
//
//		// Draw the bottom of the right eye
		Imgproc.ellipse(faceOutline, new Point(sw/2 - eyeX, sh/2 - eyeY
				- eyeYshift), eyeSize, 0, 0 + eyeA, 180 - eyeA, color,
				thickness);
//
		// Draw the top of the left eye
		Imgproc.ellipse(faceOutline, new Point(sw/2 + eyeX, sh/2 - eyeY),
				eyeSize, 0, 180 + eyeA, 360 - eyeA, color, thickness);
//		// Imgproc.ellipse(faceOutline, new Point(sw/2 - eyeX, sh/2 - eyeY),
//		// eyeSize, 0, 180 + eyeA, 360 - eyeA, color, thickness,Core.Line_AA);
//
//		// Draw the bottom of the left eye
		Imgproc.ellipse(faceOutline, new Point(sw/2 + eyeX, sh/2 - eyeY
				- eyeYshift), eyeSize, 0, 0 + eyeA, 180 - eyeA, color,
				thickness);
//
//		// Draw the bottom lip of the mouth
		int mouthY = faceH * 48/100;
//
		int mouthW = faceW * 45/100;
		int mouthH = faceH * 6 / 100;
		Imgproc.ellipse(faceOutline, new Point(sw/2, sh/2 + mouthY),
				new Size(mouthW, mouthH), 0, 0, 180, color, thickness);

		// Draw anti aliased text
		int fontFace = Core.FONT_HERSHEY_COMPLEX;
		float fontScale = 1.0f;
		int fontThickness = 2;
		String text = "Put your face over here";
		Imgproc.putText(faceOutline, text, new Point(sw * 23/100,
				sh * 10 / 100), fontFace, fontScale, color, fontThickness);
		// Imgproc.putText(faceOutline, text, new Point(sw * 23/100, sh *
		// 10/100), fontFace, fontScale, color,fontThickness,Core.LINE_AA);

		// Overlay the outline onto the displayed image using alpha bending
		// alpha bending combines the input image with the outline image
		Core.addWeighted(src, 1.0, faceOutline, 0.7, 0, dest, CvType.CV_8UC3);
//		Core.addWeighted(src1, alpha, src2, beta, gamma, dst);
		return dest;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyFaceFrame frame = new MyFaceFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
