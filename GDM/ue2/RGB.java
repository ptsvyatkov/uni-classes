package de.htw.ringert.gdm.uebung2;

public class RGB {
	private int r;
	private int g;
	private int b;
	
	public RGB(double r, double g, double b){
		this.setR((int)Math.round(r));
		this.setG((int)Math.round(g));
		this.setB((int)Math.round(b));
	}
	
	public RGB(int r, int g, int b){
		this.setR(r);
		this.setG(g);
		this.setB(b);
	}
	
	public YCbCr transformToYCbCr(){
		double tmpY= 0.299 * getR() + 0.587 * getG() + 0.114 * getB();
		double tmpCb = -0.168736 * getR() - 0.331264 * getG() + 0.5 * getB();
		double tmpCr = 0.5 * getR() - 0.418688 * getG() - 0.081312 * getB();
		
		return new YCbCr(tmpY,tmpCb,tmpCr);
	}
	public int checkForOverflow(int x){
		int tmp=x;
		if(x<0){
			tmp=0;
		}
		if(x>255){
			tmp=255;
		}
		
		return tmp;	
	}

	public String toString(){
		return("R:"+getR()+"\n"+"G:"+getG()+"\n"+"B:"+getB());
	}
	
	public static void testTransform(int r, int g, int b){

		RGB rgb1=new RGB(r,g,b);
//		System.out.println("rgb1:\n"+rgb1.toString()+"\n####");
		YCbCr ycbcr1=rgb1.transformToYCbCr();
		RGB rgb2=ycbcr1.transformToRGB();
//		System.out.println("rgb2:\n"+rgb2.toString());
		YCbCr ycbcr2=rgb2.transformToYCbCr();
		
		
		System.out.println("rgb test:"+rgb1.equals(rgb2));
//		System.out.println("YCbCr test:"+ycbcr1.equals(ycbcr2));
//		System.out.println("##########");
	}
	
	public boolean equals(RGB rgb){
		if(getR()==rgb.getR()){
			if(getG()==rgb.getG()){
				if(getB()==rgb.getB()){
					return true;
				}else{
					System.out.println("B "+getB()+"!="+rgb.getB());
				}
			}else{
				System.out.println("G "+getG()+"!="+rgb.getG());
			}
		}else{
			System.out.println("R "+getR()+"!="+rgb.getR());
		}
		return false;
	}
	
	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = checkForOverflow(r);
	}
	public int getG() {
		return g;
	}
	public void setG(int g) {
		this.g = checkForOverflow(g);
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = checkForOverflow(b);
	}
}
