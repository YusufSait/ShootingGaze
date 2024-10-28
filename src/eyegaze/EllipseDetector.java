package eyegaze;
import processing.core.PConstants;
import shootinggame.ShootingGame;

public class EllipseDetector {
	private ShootingGame screen;
	private SpiralTraverser traverser;
	///////////TODO:Dir
	private FloatPair relCenter;
	private float prevFrameSimRate;
	private NumPair cursor;
	///////////////////////////
	int traverseRange=40;//TODO: set 20
	int radiusRange=5;//in px 
	int numOfWindows = 8;//16
	int edgeThreshold = 400;
	
	// Variables used inside of methods
	Pupil lastPupil;
	Pupil bestPupil;
	PupilSquareCore pupilCore;
	boolean isFirstPupil;
	
	NumPair startRadius;
	NumPair currentRad;
	int centralPixel = 0;
	int kernelPixel_center, kernelPixel_prev, kernelPixel_next;
	int maxBright =80 ;
	int absDev;
	NumPair circEdgeCenter, circEdgePrev, circEdgeNext;
	static int red,green,blue;//Variables for color distance;
	
	public EllipseDetector(ShootingGame shootingGame, int minXRadius, int minYRadius) {
		prevFrameSimRate=0;
		radiusRange+=(radiusRange+1)%2;
		startRadius=new NumPair(minXRadius, minYRadius);
		screen = shootingGame;
		traverser = new SpiralTraverser(this);
		
		pupilCore=new PupilSquareCore(shootingGame);
		lastPupil=new Pupil(numOfWindows);
		isFirstPupil=true;
		
		cursor=new NumPair(screen.width/2,screen.height/2);
		currentRad=new NumPair();
		circEdgeCenter=new NumPair();
		circEdgePrev=new NumPair();
		circEdgeNext=new NumPair();
		currentRel=new FloatPair();
		relCenter=new FloatPair();
	}
	
	public void perform(int x, int y){
		//currentRad.setCor(bestPupil.rads.x-(int)((radiusRange-1)/2), bestPupil.rads.y-(int)((radiusRange-1)/2));
		currentRad.copyPair(startRadius);
		for(int i=0;i<radiusRange;i++){
			updateLastPupil(currentRad, x, y);
			
			if(lastPupil.getSimRate()>bestPupil.getSimRate()){
			//if(lastPupil.getSimRate()+((i+1)/radiusRange)>=bestPupil.getSimRate()+((bestPupil.rads.x-startRadius.x+1)/radiusRange)){
				if(lastPupil.rateOfVar<6)
					if(lastPupil.avgInt<15)
				//if((colorDist(screen.getPixel(lastPupil.center),0) < 2000))
				bestPupil.copy(lastPupil);
			}
			/*
			if(lastPupil.getSimRate()>0.3){
				if(eyeDarkPoint(lastPupil)>1.6){
					screen.drawPupil(lastPupil);
					++silC;
					System.out.println(silC+" Simrate: "+lastPupil.getSimRate());
					System.out.println(silC+" darkEye(): "+eyeDarkPoint(lastPupil));
				}
			}
			*/
			currentRad.inc();
		}
	}
	
	private void setRelativeCenter(NumPair point){
		relCenter.x=(float)(point.x-srcX)/srcWidth;
		relCenter.y=(float)(point.y-srcY)/srcHeight;
	}
	
	FloatPair currentRel;
	private void calcDir(NumPair point){
		currentRel.x=(float)(point.x-srcX)/srcWidth;
		currentRel.y=(float)(point.y-srcY)/srcHeight;

		//screen.cursor.corX+=(relCenter.x-currentRel.x)*10*24;
		//screen.cursor.corY-=(relCenter.y-currentRel.y)*10*1;
		if(relCenter.x-currentRel.x>0.05)
			screen.cursor.incCorX(10);
		else if(relCenter.x-currentRel.x<-0.05)
			screen.cursor.decCorX(10);
			
		
		if((relCenter.y-currentRel.y)>0.06)
			screen.cursor.decCorY(10);
		else if((relCenter.y-currentRel.y)<-0.06)
			screen.cursor.incCorY(10);
		
	}
	
	int srcX, srcY;
	int srcWidth, srcHeight;
	int silC;//TODO:sil
	public void rangeDetect(int srcX, int srcY, int srcWidth, int srcHeight){
		silC=0;
		this.srcX=srcX;
		this.srcY=srcY;
		this.srcWidth=srcWidth;
		this.srcHeight=srcHeight;
		if(bestPupil==null){
			NumPair tempCor=new NumPair(srcX+(srcWidth/2),srcY+(srcHeight/2));
			bestPupil=new Pupil(tempCor,startRadius, numOfWindows);
		}else if(bestPupil.getSimRate()==0){
			NumPair tempCor=new NumPair(srcX+(srcWidth/2),srcY+(srcHeight/2));
			bestPupil=new Pupil(tempCor,startRadius, numOfWindows);
		}
		bestPupil.reset();
		
		traverser.setArea(bestPupil.center, srcX, srcY, srcWidth, srcHeight);
		traverser.traverse(traverseRange);//Calls perform() - updates bestPupil
		
        //drawPupilEdges(bestPupil.rads, bestPupil.center.x ,  bestPupil.center.y);
        //System.out.println("sim:"+bestPupil.getSimRate());
        
		//////////////////Direction
		if(isFirstPupil){
			setRelativeCenter(bestPupil.center);
			isFirstPupil=false;
		}
		calcDir(bestPupil.center);
		///////////////////////////
		
		if(prevFrameSimRate-bestPupil.getSimRate()>0.2f){
			screen.takeShot();
			//screen.cursor.setCenter();
		}
			//System.out.println("Blink!");
		prevFrameSimRate=bestPupil.getSimRate();
        /*
        screen.strokeWeight( 1.5f );
        screen.stroke( 0, 255, 0 );
        bestPupil.rads.setCor(bestPupil.rads.x+1, bestPupil.rads.y+1);
        drawPupilEdges(bestPupil.rads, bestPupil.center.x ,  bestPupil.center.y);
        
        System.out.println("Num of steps"+traverser.getStep());
        */
	}
	int edgeI;
	
	public void updateLastPupil(NumPair radius, int x, int y) {
		cPrev=0;
		nPrev=0;
		edgeI=0;
		lastPupil.setParams(radius, x, y);
		centralPixel = screen.getPixel(x,y);
		if (screen.brightness(centralPixel) < maxBright) {
			for (float theta = 0.0f; theta < PConstants.TWO_PI; theta += PConstants.TWO_PI/numOfWindows) {
				
				circEdgeCenter.x = (int) (x + Math.round(Math.cos(theta) * radius.x));
				circEdgeCenter.y = (int) (y + Math.round(Math.sin(theta) * radius.y));
				circEdgeNext.x = (int) (x + Math.round(Math.cos(theta)* (radius.x + 1)));
				circEdgeNext.y = (int) (y + Math.round(Math.sin(theta)* (radius.y + 1)));
				
				kernelPixel_center = screen.getPixel(circEdgeCenter.x,circEdgeCenter.y);
				kernelPixel_next = screen.getPixel(circEdgeNext.x,circEdgeNext.y);

				int outDev = colorDist(kernelPixel_center,kernelPixel_next);
				if (outDev > edgeThreshold) {
					//if((cPrev==0)||(colorDist(kernelPixel_center,cPrev)< edgeThreshold && colorDist(kernelPixel_next, nPrev)<edgeThreshold)){
					if((cPrev==0)||(colorDist(kernelPixel_center,cPrev)< colorDist(kernelPixel_center,kernelPixel_next) && colorDist(kernelPixel_next, nPrev)<colorDist(kernelPixel_center,kernelPixel_next))){
						//if(screen.hue(kernelPixel_center)<screen.hue(kernelPixel_next)){		
							lastPupil.addEdge(kernelPixel_center, kernelPixel_next, edgeI);
							//screen.point(circEdgeCenter.x,circEdgeCenter.y);
						//}
					}
				}
				cPrev=kernelPixel_center;
				nPrev=kernelPixel_next;
				
				/*
				if (outDev > edgeThreshold) {
					//lastPupil.incEdge();
					lastPupil.addEdge(kernelPixel_center, kernelPixel_next, edgeI);
				} */
				++edgeI;
			}
		}
		
		pupilCore.setRateOfVar(lastPupil);
	}
	
	int c=0;
	private int cPrev=0;
	private int nPrev=0;
	public void drawPupilEdges(NumPair radius, int x, int y) {
		int cPrev=0;
		int nPrev=0;
		centralPixel = screen.getPixel(x,y);
		//if (screen.brightness(centralPixel) < darkThreshold) {
			for (float theta = 0.0f; theta < PConstants.TWO_PI; theta += PConstants.TWO_PI/numOfWindows) {
				
				circEdgeCenter.x = (int) (x + Math.round(Math.cos(theta) * radius.x));
				circEdgeCenter.y = (int) (y + Math.round(Math.sin(theta) * radius.y));
				circEdgeNext.x = (int) (x + Math.round(Math.cos(theta)* (radius.x + 1)));
				circEdgeNext.y = (int) (y + Math.round(Math.sin(theta)* (radius.y + 1)));
				
				kernelPixel_center = screen.getPixel(circEdgeCenter.x,circEdgeCenter.y);
				kernelPixel_next = screen.getPixel(circEdgeNext.x,circEdgeNext.y);
				
				int outDev = colorDist(kernelPixel_center,kernelPixel_next);
				
				if (outDev > edgeThreshold) {
					if((cPrev==0)||(colorDist(kernelPixel_center,cPrev)< colorDist(kernelPixel_center,kernelPixel_next) && colorDist(kernelPixel_next, nPrev)<colorDist(kernelPixel_center,kernelPixel_next))){
						//if(screen.saturation(kernelPixel_center)<screen.saturation(kernelPixel_next)){
							screen.point(circEdgeCenter.x,circEdgeCenter.y);
							lastPupil.addEdge(kernelPixel_center, kernelPixel_next, edgeI);
						//}
					}
				} 
				cPrev=kernelPixel_center;
				nPrev=kernelPixel_next;
				
				/*
				if (outDev > edgeThreshold) {
					++c;
						screen.point(circEdgeCenter.x,circEdgeCenter.y);
						System.out.println("Center: "+screen.red(kernelPixel_center)+"-"+screen.green(kernelPixel_center)+"-"+screen.blue(kernelPixel_center));
						System.out.println("Next: "+screen.red(kernelPixel_next)+"-"+screen.green(kernelPixel_next)+"-"+screen.blue(kernelPixel_next));
						System.out.println("Distance: "+colorDist(kernelPixel_center, kernelPixel_next));
						if(cPrev!=0){
							System.out.println("Prev Center Dist: "+colorBDist(kernelPixel_center,cPrev));
							System.out.println("Prev Next Dist: "+colorBDist(kernelPixel_next, nPrev));
						}
						else{
							screen.rect(circEdgeCenter.x,circEdgeCenter.y,3,3);
						}
						System.out.println();
						cPrev=kernelPixel_center;
						nPrev=kernelPixel_next;
				} */
			//}
		}
	}
	
	//Bound with (y = srcY+yRadius; y < srcY+srcHeight-yRadius) - (x = srcX+xRadius; x < srcX+srcWidth-xRadius)
	
	public double eyeDarkPoint(Pupil ellipse) {
		double sumOfMean = 0.0f;
        int temp_XRadius = ellipse.rads.x;
        int temp_YRadius = ellipse.rads.y;
        int counter = 0;
        while( temp_XRadius > 0 && temp_YRadius > 0 )
        {
          counter++;
          
          double mean = 0.0f;
        
          int windowID = 0;
          
          for( double theta = 0.0f; theta < PConstants.TWO_PI; theta += PConstants.TWO_PI / numOfWindows )
          {
            int xPrime   =   (int) ( ellipse.center.x + Math.round( Math.cos( theta ) * temp_XRadius ) );
            int yPrime   =   (int) ( ellipse.center.y + Math.round( Math.sin( theta ) * temp_YRadius ) );
            windowID++;
            
            double window_darknessIntensity = Math.sqrt(colorDist( screen.getPixel(xPrime, yPrime),0));
            mean += window_darknessIntensity;
          }
          mean = mean / windowID;
          sumOfMean += mean;
          temp_XRadius--;
          temp_YRadius--;
        }

        sumOfMean = sumOfMean / counter;
        
        double sumOfCovariance = 0.0f;
        
        temp_XRadius = ellipse.rads.x;
        temp_YRadius = ellipse.rads.y;
        
        while( temp_XRadius > 0 && temp_YRadius > 0 )
        {
          double cov  = 0.0f;
          int windowID = 0;
          for( double theta = 0.0f; theta < PConstants.TWO_PI; theta += PConstants.TWO_PI / numOfWindows )
          {
	    	  int xPrime   =   (int) ( ellipse.center.x + Math.round( Math.cos( theta ) * temp_XRadius ) );
	          int yPrime   =   (int) ( ellipse.center.y + Math.round( Math.sin( theta ) * temp_YRadius ) );
            
              windowID++;
              double window_darknessIntensity = Math.sqrt(colorDist( screen.getPixel(xPrime, yPrime),0));
              double absDev = Math.abs( window_darknessIntensity - sumOfMean );
              cov += absDev;
          }
          
          cov = cov / windowID;
          sumOfCovariance += cov;
          
          temp_XRadius--;
          temp_YRadius--;
        }
        sumOfCovariance = sumOfCovariance / counter;
        
        //double rateOfVariation = sumOfMean / ( sumOfCovariance == 0.0f ? 1.0f : sumOfCovariance );
        double rateOfVariation = sumOfMean * ( sumOfCovariance == 0.0f ? 1.0f : sumOfCovariance );
        
        /*	TODO:
        System.out.println("rateOfVariation:"+rateOfVariation);
        System.out.println("sumOfMean: "+sumOfMean);
        */
        
		return rateOfVariation;
	}
	
	/*
	int numOfIterations=10;
	public void eyeDark(int xRadius, int yRadius){
		int sX = 0,sY=0;
		double minMean=Double.MAX_VALUE;//Todo
		double minCov=Double.MAX_VALUE;
		for (int y = srcY+yRadius; y < srcY+srcHeight-yRadius; y++) {	//Central pixel loop
			for (int x = srcX+xRadius; x < srcX+srcWidth-xRadius; x++) {
	        for( int i = 0; i < numOfIterations; i++ )
	        {
	          double darkness_Intensity = colorDist(screen.getPixel(x,y), 0 );
	          if( darkness_Intensity < minMean )//TODO:<-?
	          {
	            
	            double sumOfMean = 0.0f;
	            
	            int temp_XRadius = xRadius;
	            int temp_YRadius = yRadius;
	            
	            int counter = 0;
	            
	            while( temp_XRadius > 0 && temp_YRadius > 0 )
	            {
	              counter++;
	              
	              double mean = 0.0f;
	            
	              int windowID = 0;
	              
	              for( double theta = 0.0f; theta < PConstants.TWO_PI; theta += PConstants.TWO_PI / numOfWindows )
	              {
	                int xPrime   =   (int) ( x + Math.round( Math.cos( theta ) * xRadius ) );
	                int yPrime   =   (int) ( y + Math.round( Math.sin( theta ) * yRadius ) );
                
                  windowID++;
                  
                  double window_darknessIntensity = colorDist( screen.getPixel(xPrime, yPrime),0);
                  mean += window_darknessIntensity;
	              }
	              
	              mean = mean / windowID;
	              
	              sumOfMean += mean;
	              
	              temp_XRadius--;
	              temp_YRadius--;
	            
	            }
	            
	            sumOfMean = sumOfMean / counter;
	            
	            double sumOfCovariance = 0.0f;
	            
	            temp_XRadius = xRadius;
	            temp_YRadius = yRadius;
	            
	            while( temp_XRadius > 0 && temp_YRadius > 0 )
	            {
	              double cov  = 0.0f;
	              
	              int windowID = 0;
	              
	              for( double theta = 0.0f; theta < PConstants.TWO_PI; theta += PConstants.TWO_PI / numOfWindows )
	              {
		              int xPrime   =   (int) ( x + Math.round( Math.cos( theta ) * xRadius ) );
		              int yPrime   =   (int) ( y + Math.round( Math.sin( theta ) * yRadius ) );
		            
		              windowID++;
		
		              double window_darknessIntensity = colorDist( screen.getPixel(xPrime, yPrime),0);
		              
		              double absDev = Math.abs( window_darknessIntensity - sumOfMean );
		              
		              cov += absDev;
	              }
	              
	              cov = cov / windowID;
	              
	              sumOfCovariance += cov;
	              
	              temp_XRadius--;
	              temp_YRadius--;
	            
	            }
	            
	            sumOfCovariance = sumOfCovariance / counter;
	            
	            double rateOfVariation = sumOfMean / ( sumOfCovariance == 0.0f ? 1.0f : sumOfCovariance );
	            
	            if( rateOfVariation < minCov )
	            {
	              minCov = rateOfVariation;
	              
	              sX=x;
	              sY=y;
	            }
	            
	            minMean = sumOfMean;
	            
	          }
	        }
	      }
	    }

        screen.strokeWeight( 2f );
        screen.stroke( 255, 0, 0 );
        screen.noFill();
        screen.ellipse( sX, sY, xRadius * 2, yRadius * 2 );
	}
	*/
	
	public static int colorDist(int c1, int c2){
		red =  (c1 >> 16 & 0xFF)-(c2 >> 16 & 0xFF);
		//red =  (int)screen.red(c1)-(int)screen.red(c2);
		red*=red;
		green = (c1 >> 8 & 0xFF)-(c2 >> 8 & 0xFF);
		green*=green;
		blue = (c1 & 0xFF)-(c2 & 0xFF);
		blue*=blue;
		return red+green+blue;
	}
	
	public int colorBDist(int c1, int c2){
		return (int) (screen.brightness(c1)- screen.brightness(c2));
	}

	public void spiralTry(int i, int j, int k, int l, int m, int n) {
		traverser.setArea(new NumPair(i,j), k, l, m, n);
		traverser.traverse();
	}
}
