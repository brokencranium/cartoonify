package com.buildstuff.vv.ip;

import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MyFrame extends JFrame{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	 private JButton captureBut;
	 private VideoCap videoCap = new VideoCap();
	 
	public MyFrame(){
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100,1280, 1024);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    
//    Add button 
    captureBut = new JButton("Capture Me");
    captureBut.setBounds(50, 5, 80, 30);
    contentPane.add(captureBut,2,0);
    contentPane.setLayout(null);
    
    setContentPane(contentPane);
    
    new MyThread().start();
}


public void paint(Graphics g){
    g = contentPane.getGraphics();
    g.drawImage(videoCap.getOneFrame(), 0, 40, this);
    
   
}

class MyThread extends Thread{
    @Override
    public void run() {
        for (;;){
            repaint();
            try { Thread.sleep(30);
            } catch (InterruptedException e) {    }
        }  
    } 
}

/**
 * Launch the application.
 */
   public static void main(String[] args) {
       EventQueue.invokeLater(new Runnable() {
           public void run() {
               try {
                   MyFrame frame = new MyFrame();
                   frame.setVisible(true);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       });
   }
   
}
