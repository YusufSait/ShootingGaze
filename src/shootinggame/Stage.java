package shootinggame;

import java.util.ArrayList;

import processing.core.PImage;

public class Stage implements Page{
	private ShootingGame screen;	//TODO: Change to PApplet
	private PImage backgroundImg;
	private PImage targetImg;
	private ArrayList <Target> targets;
	
	private long currentTimeMs;
	private int stagePoints;
	private int tourNumber;
	private long endTime;
	private int numOfTargets;
	private int tourDuration;
	
	public Stage(ShootingGame screen, PImage targetImg, PImage backgroundImg,int tourNumber, int tourDuration, int numOfTargets) {
		stagePoints=0;
		this.screen=screen;
		this.backgroundImg=backgroundImg;
		this.targetImg=targetImg;
		this.tourNumber=tourNumber;
		this.numOfTargets=numOfTargets;
		this.tourDuration=tourDuration;
		currentTimeMs=-1;
	}
	
	private void init(){
		currentTimeMs=System.currentTimeMillis();
		endTime=tourDuration+currentTimeMs;
		targets=new ArrayList<Target>();	//All remaining targets are destroyed
		if(numOfTargets<1)
			numOfTargets=1;
		for(int i=0;i<numOfTargets;i++)
			targets.add(new Target(screen,this,targetImg));
	}
	
	public void draw(){
		if(currentTimeMs==-1)
			init();
		screen.putImg(backgroundImg,0,0);
		checkTime();
		drawTargets();
	}
	
	public void drawTargets(){
		for(Target t:targets)
			t.draw();
	}
	
	//TODO: Garbage Collector
	private void checkTime(){
		if (endTime < System.currentTimeMillis())
			decTourNumber();
	}
	
	public void lClick(int x, int y) {
			for (int i = 0; i < targets.size(); i++) {
				stagePoints += targets.get(i).checkHit(x, y, screen.cursor);
				if (targets.get(i).getShot()){ // if target gets shot, remove that target
					removeTarget(i);
				}
			}
		screen.addGamePoints(stagePoints);
		//System.out.println("Stage Points: "+stagePoints);
	}
	
	private void removeTarget(int index){
		targets.remove(index);
		if(targets.size()==0)
			decTourNumber();
	}
	
	private void decTourNumber(){
		if(--tourNumber!=0)	//Reinitialize targets
			init();
		else
			screen.updateStage();
	}
	
	public int getBackgroundWidth() {
		return backgroundImg.width;
	}
	
	public int getBackgroundHeight() {
		return backgroundImg.height;
	}

	public int getStagePoints(){
		return stagePoints;
	}
}
