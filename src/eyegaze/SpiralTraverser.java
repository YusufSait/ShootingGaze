package eyegaze;

public class SpiralTraverser {
	private int x;// starting x
	private int y;// starting y
	private int step;
	private int limitStep;
	private EllipseDetector caller;
	
	private int leftBorder;
	private int rightBorder;
	private int upBorder;
	private int downBorder;
	private int i;

	private Vertex rEdge;
	private Vertex dEdge;
	private Vertex lEdge;
	private Vertex uEdge;

	public SpiralTraverser(EllipseDetector caller) {
		this.caller = caller;
		x=0;
		y=0;
		limitStep=-1;
	}
	
	
	public void setArea(NumPair startPoint, int rectX, int rectY, int rectW, int rectH) {
		step = 1;
		
		x = startPoint.x;
		y = startPoint.y;
		
		leftBorder = (x - rectX) * 2;
		rightBorder = ((rectX + rectW) - x) * 2;
		upBorder = (y - rectY) * 2;
		downBorder = ((rectY + rectH) - y) * 2;
		limitStep=-1;
		//System.out.println(""+x+"-"+y+"-"+rectX+"-"+rectY+"-"+rectW+"-"+rectH);
		
		if (leftBorder < 1 || rightBorder < 1 || upBorder < 1 || downBorder < 1) {
			if (rightBorder < 1)
				x=rectX+rectW-1;
			else if(leftBorder <1)
				x=rectX+1;
			
			if(upBorder<1)
				y=rectY+1;
			else if(downBorder <1)
				y=rectY+rectH-1;

			leftBorder = (x - rectX) * 2;
			rightBorder = ((rectX + rectW) - x) * 2;
			upBorder = (y - rectY) * 2;
			downBorder = ((rectY + rectH) - y) * 2;
		}

		rEdge = new Vertex(true, true, rightBorder);
		dEdge = new Vertex(false, true, downBorder);
		lEdge = new Vertex(true, false, leftBorder);
		uEdge = new Vertex(false, false, upBorder);
		rEdge.setBefore(uEdge);
		dEdge.setBefore(rEdge);
		lEdge.setBefore(dEdge);
		uEdge.setBefore(lEdge);
	}
	
	public int getStep(){
		return step;
	}
	
	void traverse(int limitStep) {
		this.limitStep=limitStep;
		traverse();
	}
	
	void traverse() {
		caller.perform(x, y);
		
		while (!rEdge.ended || !dEdge.ended || !lEdge.ended || !uEdge.ended) {
			if(limitStep!=-1 && limitStep<step)
				return;
			rEdge.step(step);
			stepCor(rEdge);
			dEdge.step(step);
			stepCor(dEdge);
			++step;

			lEdge.step(step);
			stepCor(lEdge);
			uEdge.step(step);
			stepCor(uEdge);
			++step;
		}
	}

	private void stepCor(Vertex v) {
		if (v.cor) {
			if (v.dir) {
				x += v.noDrawS;
				if (v.ghost)
					return;
				else {
					for (i = 0; i < v.draw; i++) {
						++x;
						caller.perform(x,y );
					}
					x += v.noDrawE;
				}
			} else {
				x -= v.noDrawS;
				if (v.ghost)
					return;
				else {
					for (i = 0; i < v.draw; i++) {
						--x;
						caller.perform(x, y);
					}
					x -= v.noDrawE;
				}
			}
		} else {
			if (v.dir) {
				y += v.noDrawS;
				if (v.ghost)
					return;
				else {
					for (i = 0; i < v.draw; i++) {
						++y;
						caller.perform(x, y);
					}
					y += v.noDrawE;
				}
			} else {
				y -= v.noDrawS;
				if (v.ghost)
					return;
				else {
					for (i = 0; i < v.draw; i++) {
						--y;
						caller.perform(x, y);
					}
					y -= v.noDrawE;
				}
			}
		}
		
	}
	
	//TODO:sil
	public int getMaxStep(){
		return Math.max(Math.max(Math.max(rightBorder, leftBorder), upBorder),downBorder);
	}

	private class Vertex {
		private Vertex before;
		private int maxStep;
		public int noDrawS;
		public int draw;
		public int noDrawE;
		public boolean ended;
		public boolean ghost;
		public final boolean cor, dir;

		public Vertex(boolean cor, boolean dir, int maxStep) {// cor->(true:x
																// false:y)
																// dir->(true:+
																// false:-)
			this.cor = cor;
			this.dir = dir;
			this.maxStep = maxStep;
			ended = false;
			ghost = false;
		}

		public void step(int step) {
			if (!this.ended && step > maxStep)
				this.ended = true;

			if (this.ghost) {
				noDrawS = step;
			} else {
				if (before.ended) {
					this.ghost = true;
					noDrawS = step;
				} else {
					if (before.ghost) {
						++noDrawS;
						if (this.ended)
							++noDrawE;
						else
							++draw;
					} else {
						if (this.ended) {
							++noDrawE;
							++draw;
						} else {
							draw = step;// ++draw, ++draw
						}
					}
				}
			}
		}

		public void setBefore(Vertex before) {
			this.before = before;
		}
	}

	public void stop() {
		//TODO: Uncomment
		//stop=true;
	}

}
