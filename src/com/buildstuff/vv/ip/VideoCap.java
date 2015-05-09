package com.buildstuff.vv.ip;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class VideoCap {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    VideoCapture cap;
    Mat2Image mat2Img = new Mat2Image();
    private Mat mat; 
    
    
   public VideoCap(){
       mat = new Mat();
	   cap = new VideoCapture();
        cap.open(0);
    } 
   
   public Mat getOneMatFrame(){
	     cap.read(mat2Img.mat);
       return mat2Img.mat;
   }
   
    public BufferedImage getOneFrame() {
        return mat2Img.getImage(this.getOneMatFrame());
    }
    
    public BufferedImage getOneBuffFrame(Mat mat){
    	return mat2Img.getImage(mat);
    }

}
