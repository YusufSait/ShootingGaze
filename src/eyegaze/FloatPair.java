package eyegaze;

public class FloatPair {
	public float x;
	public float y;
	
	public FloatPair(){
		x=0;
		y=0;
	}
	
	public FloatPair(float x, float y){
		this.x=x;
		this.y=y;
	}
	
	public void setCor(float x, float y){
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