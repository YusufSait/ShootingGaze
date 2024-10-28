package shootinggame;

import processing.core.PImage;

public class StartPage implements Page{
	private ShootingGame screen;
	private PImage backgroundImg;
	private RectButton btnStart;
	private RectButton btnOptions;
	private int btnWidth;
	private int btnHeight;
	public String message="";
	
	public StartPage(ShootingGame screen, PImage backgroundImg){
		this.screen=screen;
		btnWidth=125;
		btnHeight=50;
		this.backgroundImg = backgroundImg;
		btnStart=new RectButton("Start",(screen.width-btnWidth)/2, (screen.height-btnHeight)/2-100, btnWidth, btnHeight);
		btnOptions=new RectButton("Options",(screen.width-btnWidth)/2, (screen.height-btnHeight)/2-90+btnHeight, btnWidth, btnHeight);
	}

	public void draw() {
		screen.image(backgroundImg, 0,0);
		btnStart.draw(screen);
		btnOptions.draw(screen);
		
		screen.textSize(22);
		screen.fill(250, 250, 0,240);
		screen.text(message,(screen.width-btnWidth)/2, (screen.height-btnHeight)/2-100);
		
	}

	public void lClick(int x, int y) {
		if(btnStart.lClick(x, y))
			screen.startStage();
		else if(btnOptions.lClick(x, y))
			return;
			//System.out.println("Not implemented yet!");
		
	}

	private class RectButton{
		private int x;	//Run  calcCenter() on set functions
		private int y;
		private int width;
		private int height;
		private String text;
		
		public RectButton(String buttonText, int x, int y, int width, int height){
			this.x=x;
			this.y=y;
			this.width=width;
			this.height=height;
			text=buttonText;
		}
		
		public boolean lClick(int xPos, int yPos) {
			xPos-=x;
			yPos-=y;
			if(xPos<width && yPos<height)
				return true;
			return false;
		}
		
		public void draw(ShootingGame screen){
			screen.fill(0,0,255);
			screen.rect(x, y, width, height);
			screen.textSize(32);
			screen.fill(250, 250, 250,240);
			screen.text(text, x+4, y+height-16);
		}
	} 
}
