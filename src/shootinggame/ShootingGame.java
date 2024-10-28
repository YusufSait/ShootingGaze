package shootinggame;

import java.awt.Rectangle;

import eyegaze.*;
import gab.opencv.OpenCV;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

public class ShootingGame extends PApplet {
	private Page currentPage;
	private Stage[] stages;
	private StartPage startPage;
	private int numberOfStages = 5;// TODO: Make it final
	private int currentStage;
	public Gun cursor;
	private int gamePoints;

	// ////////////////////////EyeGaze
	private Capture cam;
	private int fps = 30;
	private PImage inputFrame;

	private EllipseDetector pupilDetector1;
	private EllipseDetector pupilDetector2;
	private OpenCV cvEye;
	private Rectangle[] eyes;

	// Function variables
	int halfWidth;

	// ///////////////////////////////
	public void setup() {
		stages = new Stage[numberOfStages];
		currentStage = 0;
		gamePoints = 0;
		PImage backGround = new PImage();
		backGround = loadImage("back1.jpg");

		PImage crossHair = new PImage();
		crossHair = loadImage("crosshair1.jpg");
		PImage targetImg = new PImage();
		targetImg = loadImage("target.png");

		// TODO: Change duration according to stage's hardness
		for (int i = 0; i < stages.length; i++)
			stages[i] = new Stage(this, targetImg, backGround, 3, 40000, i + 1);// 7000
		frameRate(30);//60

		// TODO: Set size from ShootingGame. Set that size to stages!
		size(stages[0].getBackgroundWidth(), stages[0].getBackgroundHeight());
		cursor = new Gun(this, crossHair, 5);

		// currentPage=stages[0];
		startPage = new StartPage(this, backGround);
		currentPage = startPage;
		noCursor();
		// ////////////////////EyeGaze
		pupilDetector1 = new EllipseDetector(this, 7, 7);// TODO: calculate
															// first radius from
															// rectangle
		pupilDetector2 = new EllipseDetector(this, 7, 7);

		String[] cams = Capture.list();
		println(cams);
		cam = new Capture(this, 640, 480, fps);
		cam.start();
		cvEye = new OpenCV(this, cam.get());

		cvEye.loadCascade("haarcascade_mcs_eyepair_big.xml");
		// /////////////////////////////
	}

	public void draw() {
		currentPage.draw();
		/*
		 * if(stages!=null) stages[currentStage].draw();
		 */
		cursor.draw();
		takeShot();//Take shot constantly without an action.
		// ////////////////////////EyeGaze
		srcCam();
		// srcImage();
		// ////////////////////////////////
	}

	// ////////////////////////////////EyeGaze
	private void srcImage() {
		inputFrame = loadImage("dir2.png");

		cvEye = new OpenCV(this, inputFrame);
		cvEye.loadCascade("haarcascade_mcs_eyepair_big.xml");

		searchEyePair(getEyeRects(true));
		noLoop();
	}

	private void srcCam() {
		if (cam.available() == true) {
			cam.read();
			inputFrame = cam.get();
			// image(inputFrame, 0, 0);
			searchEyePair(getEyeRects(false));
		} else {
			System.out.println("Camera error!");
			// noLoop();
		}
	}

	int maxArea;
	int maxInd;
	int currArea;

	private Rectangle getEyeRects(boolean draw) {
		currArea = 0;
		maxArea = 0;
		maxInd = -1;
		cvEye.loadImage(inputFrame);
		eyes = cvEye.detect();

		noFill();
		stroke(0, 255, 0);
		strokeWeight(1);
		for (int i = 0; i < eyes.length; i++) {
			currArea = eyes[i].width * eyes[i].height;
			if (currArea > maxArea) {
				maxArea = currArea;
				maxInd = i;
			}
		}
		if (maxInd == -1)
			return null;
		if (draw) {
			rect(eyes[maxInd].x, eyes[maxInd].y, eyes[maxInd].width / 2,
					eyes[maxInd].height);
			rect(eyes[maxInd].x + (eyes[maxInd].width / 2), eyes[maxInd].y,
					eyes[maxInd].width / 2, eyes[maxInd].height);
		}
		return eyes[maxInd];
	}

	public int getPixel(NumPair cor) {
		return inputFrame.pixels[cor.y * inputFrame.width + cor.x];
	}

	private void searchEyePair(Rectangle eye) {
		if (eye != null) {
			halfWidth = (int) eye.width / 2;
			pupilDetector1.rangeDetect(eye.x, eye.y, halfWidth, eye.height);
			// pupilDetector2.rangeDetect(eye.x+halfWidth,eye.y,halfWidth,eye.height);
		}
	}

	// ///////////////////////////////////////
	public int getPixel(int x, int y) {
		return inputFrame.pixels[y * inputFrame.width + x];
	}

	public void startStage() {
		currentPage = stages[0];
	}

	// TODO: this method should be only visible to Stage
	public void updateStage() {
		if (currentStage < stages.length - 1) {
			++currentStage;
			currentPage = stages[currentStage];
		} else {
			System.out.println("End of the game!\n"
					+ (gamePoints + stages[currentStage].getStagePoints()));// TODO:
																			// handle
																			// end
																			// of
																			// the
																			// game
			/*
			 * stages=new Stage[currentStage=0]; stages[0]=new Stage(this,
			 * targetImg, backgroundImg, tourNumber, tourDuration, numOfTargets)
			 */
			currentPage = startPage;
			startPage.message = "Last Score: "
					+ (gamePoints + stages[currentStage].getStagePoints() / 1000);
		}
	}

	/*
	 * public void keyPressed() { if (key == 'r') {
	 * 
	 * } }
	 */

	public void mouseClicked() {
		/*if (mouseButton == LEFT)
			currentPage.lClick(mouseX, mouseY);*/
	}

	public void takeShot() {
		currentPage.lClick((int) cursor.corX, (int) cursor.corY);
	}

	public PImage trnsWhite(PImage img) {
		PImage trnsImg = createImage(img.width, img.height, ARGB);
		int thr = 200;

		if (img == null)
			return null;

		for (int i = 0; i < img.pixels.length; i++) {
			if (brightness((img.pixels[i])) > thr)
				trnsImg.pixels[i] = color(0, 255, 255, 0);
			else
				trnsImg.pixels[i] = color(0, 0, 0, 255);
		}
		trnsImg.updatePixels();
		return trnsImg;
	}

	public void putImg(PImage img, int i, int j) {
		image(img, i, j);
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { shootinggame.ShootingGame.class.getName() });
	}

	public void addGamePoints(int gamePoints) {
		this.gamePoints += gamePoints;
	}
}
