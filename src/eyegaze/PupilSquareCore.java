package eyegaze;

import processing.core.PConstants;
import shootinggame.ShootingGame;
/*
  calculates biggest square that fits in the "circle".
 */
public class PupilSquareCore {
	final static float cos45=0.7071f;
	public NumPair startCor;
	public NumPair edges;//x->width, y->height
	private float halfSide;
	private ShootingGame screen;
	private int size;
	
	public PupilSquareCore(ShootingGame shootingGame){
		this.screen=shootingGame;
		startCor=new NumPair();
		edges=new NumPair();
		halfSide=0;
		size=0;
	}
	
	public void setParams(Pupil pupil){
		halfSide=pupil.rads.x*cos45;
		
		edges.x=(int)Math.floor(halfSide*2f);
		edges.y=edges.x;//Since it is square.
		startCor.x=(int)Math.floor(pupil.center.x-halfSide);
		startCor.y=(int)Math.floor(pupil.center.y-halfSide);
		size=edges.x*edges.y;
	}
	
	double avgIntensity;
	double avgCovariance;
	double absDev;
	double covariance;
    int x;
    int y;
	public void setRateOfVar(Pupil pupil){
		this.setParams(pupil);
		avgIntensity = 0;
        for(x=0;x<edges.x;x++){
            for(y=0;y<edges.y;y++){
            	//startCor.x+x , startCor.y+y;
            	avgIntensity+=Math.sqrt(EllipseDetector.colorDist( screen.getPixel(startCor.x+x, startCor.y+y),0));
            }
        }
        avgIntensity/=size;
        
        covariance=0;
        for(x=0;x<edges.x;x++){
            for(y=0;y<edges.y;y++){
            	absDev=Math.abs(Math.sqrt(EllipseDetector.colorDist( screen.getPixel(startCor.x+x, startCor.y+y),0))-avgIntensity);
            	covariance+=absDev;
            }
        }
        covariance/=size;

        pupil.avgInt=covariance;
        pupil.rateOfVar=avgIntensity / ( covariance == 0.0f ? 1.0f : covariance );
        //double rateOfVariation = sumOfMean / ( sumOfCovariance == 0.0f ? 1.0f : sumOfCovariance );
		//return avgIntensity / ( covariance == 0.0f ? 1.0f : covariance );
	}
}
