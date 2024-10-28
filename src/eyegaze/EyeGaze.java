package eyegaze;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.*;
import gab.opencv.*;
import java.awt.Rectangle;

public class EyeGaze extends PApplet {
	private Capture cam;
	private int fps = 30;
	private PImage inputFrame;
	
	private EllipseDetector pupilDetector1;
	private EllipseDetector pupilDetector2;
	private OpenCV cvEye;
	private Rectangle[] eyes;
	
	//Function variables
	int halfWidth;
	
	public void setup() {
		//size(640, 480);
		//pupilDetector1= new EllipseDetector(this,7,7);//TODO: calculate first radius from rectangle
		//pupilDetector2= new EllipseDetector(this,7,7);
		
		
		String[] cams = Capture.list();
		println(cams);
		cam = new Capture(this, 640, 480, fps);
		cam.start();
		cvEye = new OpenCV(this,cam.get());
		
		cvEye.loadCascade("haarcascade_mcs_eyepair_big.xml");
		//cvEye.calculateOpticalFlow();
	}

	public void draw1() {
		srcCam();
		//srcImage();
	}
	
	private void srcImage(){
		inputFrame = loadImage("dir2.png");
		
		cvEye = new OpenCV(this,inputFrame);
		cvEye.loadCascade("haarcascade_mcs_eyepair_big.xml");
		
		image(inputFrame, 0, 0);
		searchEyePair(getEyeRects(true));
		noLoop();
	}
	
	private void srcCam(){
		if (cam.available() == true) {
			cam.read();
			inputFrame = cam.get();
			image(inputFrame, 0, 0);
			searchEyePair(getEyeRects(true));
		}
		else{
			System.out.println("Camera error!");
			//noLoop();
		}
	}
	
	/*private Rectangle[] getEyeRects(boolean draw){
		cvEye.loadImage(inputFrame);
		eyes = cvEye.detect();
		
		if(draw){
			noFill();
			stroke(0, 255, 0);
			strokeWeight(1);
			for (int i = 0; i < eyes.length; i++) {
				rect(eyes[i].x, eyes[i].y, eyes[i].width/2, eyes[i].height);
				rect(eyes[i].x+(eyes[i].width/2), eyes[i].y, eyes[i].width/2, eyes[i].height);
			}
		}
		return eyes;
	}*/
	
	int maxArea;
	int maxInd;
	int currArea;
	private Rectangle getEyeRects(boolean draw){
		currArea=0;
		maxArea=0;
		maxInd=-1;
		cvEye.loadImage(inputFrame);
		eyes = cvEye.detect();
		
		noFill();
		stroke(0, 255, 0);
		strokeWeight(1);
		for (int i = 0; i < eyes.length; i++) {
			currArea= eyes[i].width* eyes[i].height;
			if(currArea>maxArea){
				maxArea=currArea;
				maxInd=i;
			}
		}
		if(maxInd==-1)
			return null;
		if(draw){
			rect(eyes[maxInd].x, eyes[maxInd].y, eyes[maxInd].width/2, eyes[maxInd].height);
			rect(eyes[maxInd].x+(eyes[maxInd].width/2), eyes[maxInd].y, eyes[maxInd].width/2, eyes[maxInd].height);
		}
		return eyes[maxInd];
	}
	
	public int getPixel(NumPair cor){
		return inputFrame.pixels[cor.y*inputFrame.width+cor.x];
	}
	
	public int getPixel(int x, int y){
		return inputFrame.pixels[y*inputFrame.width+x];
	}
	
	private void searchEyePair(Rectangle eye){
		if(eye!=null){
			halfWidth=(int)eye.width/2;
			pupilDetector1.rangeDetect(eye.x,eye.y,halfWidth,eye.height);
			//pupilDetector2.rangeDetect(eye.x+halfWidth,eye.y,halfWidth,eye.height);
		}
	}
	
	public void drawPupil(Pupil pupil){
		strokeWeight( 1f );
        stroke( 0, 255, 255 );
        point(pupil.center.x, pupil.center.y);
        
        strokeWeight( 1.5f );
        stroke( 255, 0, 0 );
        ellipse(pupil.center.x, pupil.center.y, pupil.rads.x*2, pupil.rads.y*2);
	}
	
	public static void main(String _args[]) {
		
		PApplet.main(new String[] { eyegaze.EyeGaze.class.getName() });
		
		
	}

}