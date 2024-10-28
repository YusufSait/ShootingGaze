package eyegaze;

import java.util.ArrayList;

public class Pupil{
	public NumPair center;
	public NumPair rads; //x radius and y radius
	public double rateOfVar, avgInt;
	private int numOfWindows;
	public int edgeNum;
	private int simRate, maxCluster, secMaxCluster, i;
	private int tolerance;
	private int cIndex;//cluster index pointer
	private int[] edgeClusters;
	private int sum;
	private int prevCenterC, prevNextC;
	
	private int lastCenterColor, lastNextColor, lastIndex;
	//find max 2 cluster
	public Pupil(int numOfWindows){
		center=new NumPair();
		rads=new NumPair();
		this.numOfWindows=numOfWindows;
		reset();
	}
	
	public void reset(){
		tolerance=1;//1 is most strict(next edge), numOfWindows is most tolerated
		edgeClusters=new int[numOfWindows];
		edgeNum=0;
		lastIndex=0;
		cIndex=0;
		maxCluster=0;
		secMaxCluster=0;
		sum=-1;
		rateOfVar=255;
		avgInt=255;
	}
	
	public Pupil(NumPair center, NumPair radiuses, int numOfWindows){
		this.center=new NumPair(center.x,center.y);
		rads=new NumPair(radiuses.x, radiuses.y);
		this.numOfWindows=numOfWindows;
		reset();
	}
	
	boolean notIncreased=true;
	public void addEdge(int centerColor, int nextColor, int edgeIndex){
		if(lastIndex==0||lastIndex+tolerance>=edgeIndex){
			//if(EllipseDetector.colorDist(last,centerColor)>){//TODO: implement
				++edgeClusters[cIndex];
				lastIndex=edgeIndex;
				notIncreased=true;
			//}
		}
		else if(notIncreased){
			lastIndex=edgeIndex;
			++cIndex;
			++edgeClusters[cIndex];
			notIncreased=false;
		}
		//TODO: color differance...
	}
	

	public void setParams(NumPair radius, NumPair position){
		setParams(radius,position.x,position.y);
	}
	
	public void setParams(NumPair radius, int x, int y){
		center.setCor(x, y);
		rads.copyPair(radius);
		notIncreased=true;
		edgeNum=0;
		edgeClusters=new int[numOfWindows];
		
		lastIndex=0;
		cIndex=0;
		maxCluster=0;
		sum=-1;
	}
	
	public void copy(Pupil n){
		this.center.copyPair(n.center);
		this.rads.copyPair(n.rads);
		this.edgeNum=n.edgeNum;
		this.edgeClusters=(int[]) n.edgeClusters.clone();	//private?
		this.cIndex=n.cIndex;	//private?
		this.sum=n.sum;
		
		this.rateOfVar=n.rateOfVar;
		this.avgInt=n.avgInt;
	}
	
	public void incEdge(){
		++edgeNum;
	}
	
	public float getSimRate(){
		//return edgeNum/numOfWindows;
		if(sum==-1)
			sum = getSumOfMaxTwoClusters(); // getSumOfEdges(); 
		return (float)sum/numOfWindows;
	}
	
	private int getSumOfMaxTwoClusters(){
		for(i=0;i<=cIndex;i++){
			if(edgeClusters[i]>maxCluster)
				maxCluster=edgeClusters[i];
			else if(edgeClusters[i]>secMaxCluster)
				secMaxCluster=edgeClusters[i];
		}
		return maxCluster+secMaxCluster;
	}
}
