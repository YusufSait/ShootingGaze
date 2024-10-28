package shootinggame;

import processing.core.PImage;

public class Gun {
	private ShootingGame screen;
	private float effectAreaRadius;
	private PImage crossH;
	private int crossHX;
	private int crossHY;
	private float screenCenterX;
	private float screenCenterY;
	public float corX;
	public float corY;
	
	public Gun(ShootingGame screen,PImage crossHairImg, float effectAreaLength){
		this.screen=screen;
		initCrossH(crossHairImg);
		this.setEffectAreaRadius(effectAreaLength);
		screenCenterX=screen.width/2f;
		screenCenterY=screen.height/2f;
		System.out.println(screenCenterX);
		System.out.println(screenCenterY);
		System.out.println(screen.width);
		System.out.println(screen.height);
		System.out.println();
		setCenter();
	}
	
	private void initCrossH(PImage crossHairImg){
		crossH=screen.trnsWhite(crossHairImg);
		crossHY=crossH.height/2;
		crossHX=crossH.width/2;
	}
	
	public void draw(){
		//screen.putImg(crossH,screen.mouseX-crossHX,screen.mouseY-crossHY);
		screen.putImg(crossH,(int)corX-crossHX,(int)corY-crossHY);
	}
	
	public void setCenter(){
		corX=400-crossHX;
		corY=screenCenterY-crossHY;
		/*corX=screenCenterX-crossHX;
		corY=screenCenterY-crossHY;*/
	}
	
	float getEffectAreaRadius() {
		return effectAreaRadius;
	}

	void setEffectAreaRadius(float effectAreaRadius) {
		this.effectAreaRadius = effectAreaRadius;
	}

	public void incCorX(int i) {
		corX+=i;
		if(corX>screen.width)
			corX=screen.width;
	}

	public void decCorX(int i) {
		corX-=i;
		if(corX<0)
			corX=0;
		
	}

	public void decCorY(int i) {
		corY-=i;
		if(corY<0)
			corY=0;
	}
	
	public void incCorY(int i){
		corY+=i;
		if(corY>screen.height)
			corY=screen.height;
	}
	
}
