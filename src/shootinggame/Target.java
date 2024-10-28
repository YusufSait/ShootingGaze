package shootinggame;

import java.util.Random;

import processing.core.PImage;

public class Target{
	private ShootingGame screen;
	private int borderWidth;
	private int borderHeight;
	private boolean shot;
	
	private Stage stage;
	private PImage imgTarget;
	
	private int width;
	private int height;
	private float radius;
	//Coordinates of imgTarget
	private int corX;
	private int corY;
	//Coordinates of imgTarget's center
	private int centerX;
	private int centerY;
	Random randomGenerator;
	
	public Target(ShootingGame screen,Stage stage, PImage imgTarget, int posX, int posY){
		width=80;
		height=80;
		this.screen=screen;
		borderWidth=stage.getBackgroundWidth();
		borderHeight=stage.getBackgroundHeight();
		this.stage=stage;
		randomGenerator = new Random();

		init(imgTarget);
		corX=posX;
		corY=posY;
		centerX=width/2+corX;
		centerY=height/2+corY;
	}
	
	public Target(ShootingGame screen,Stage stage, PImage imgTarget){
		width=80;
		height=80;
		this.screen=screen;
		borderWidth=stage.getBackgroundWidth();
		borderHeight=stage.getBackgroundHeight();
		this.stage=stage;
		randomGenerator = new Random();

		init(imgTarget);
		corX=randomX();
		corY=randomY();
		centerX=width/2+corX;
		centerY=height/2+corY;
	}
	
	public void init(PImage imgTarget){
		shot=false;
		radius=width/2;
		this.imgTarget= imgTarget;
		imgTarget.resize(width, height);
	}
	
	//TODO: no use. Delete
	private void reInit(int posX, int posY){
		corX=posX;
		corY=posY;
		centerX=width/2+corX;
		centerY=height/2+corY;
		shot=false;
	}
	
	
	public void draw(){
		screen.image(imgTarget,corX,corY);
	}
	
	public void drawRandom(){
		corX=randomX();
		corY=randomY();
		reInit(corX,corY);
		screen.image(imgTarget,corX,corY);
	}
	
	private int randomX(){
		return randomGenerator.nextInt(borderWidth-width);	//subtract width from upper bound to prevent border exceed
	}
	private int randomY(){
		return randomGenerator.nextInt(borderHeight-height); //subtract height from upper bound to prevent border exceed
	}
	
	
	public float checkHit(int x, int y, Gun gun) {
		float d=distance(x,y)-gun.getEffectAreaRadius();//TODO: globalize
		if(d<radius){
			setShot(true);
			return radius-d;
		}
		return 0;
	}
	
	private void setShot(boolean shot){
		this.shot=shot;
	}
	
	public boolean getShot(){
		return shot;
	}
	
	public float distance(int x, int y){
		return (float)Math.sqrt(Math.pow(centerX-x,2)+Math.pow(centerY-y,2));
	}
}
