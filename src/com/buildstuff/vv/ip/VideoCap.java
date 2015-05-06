package com.buildstuff.vv.ip;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

public class VideoCap {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    VideoCapture cap;
    Mat2Image mat2Img = new Mat2Image();

   public VideoCap(){
        cap = new VideoCapture();
        cap.open(0);
    } 
 
    public BufferedImage getOneFrame() {
        cap.read(mat2Img.mat);
        return mat2Img.getImage(mat2Img.mat);
    }

}
