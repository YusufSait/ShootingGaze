package eyegaze;

public class NumPair {
	public int x;
	public int y;
	
	public NumPair(){
		x=0;
		y=0;
	}
	
	public NumPair(int x, int y){
		this.x=x;
		this.y=y;
	}
	
	public void setCor(int x, int y){
		this.x=x;
		this.y=y;
	}
	
	public void copyPair(NumPair equ){
		x=equ.x;
		y=equ.y;
	}
	
	public void inc(){
		++x;
		++y;
	}
}
