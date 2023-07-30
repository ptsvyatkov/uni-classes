package de.htw.ringert.gdm.uebung2;

public class YCbCr {
	private double y;
	private double cb;
	private double cr;

	public YCbCr() {
		// TODO Auto-generated constructor stub
	}
	
	public YCbCr(double y,double cb, double cr){
		this.setY(y);
		this.setCb(cb);
		this.setCr(cr);
	}
	
	public YCbCr changeBrightness(double value){
		YCbCr tmp=new YCbCr();
		
		tmp.setY(getY()+value);
		tmp.setCb(getCb());
		tmp.setCr(getCr());
		
		return tmp;
	}
	
	public YCbCr changeKontrast(double value){
		YCbCr tmp=new YCbCr();
		
		tmp.setY(value*(getY()-128) + 128);
		tmp.setCb(getCb());
		tmp.setCr(getCr());
		
		return tmp;
	}
	
	public YCbCr changeSaettigung(double value){
		YCbCr tmp=new YCbCr();
		
		tmp.setY(getY());
		tmp.setCb(getCb()*value);
		tmp.setCr(getCr()*value);
		
		return tmp;
	}
	
	public YCbCr changeHue(double value){
		YCbCr tmp=new YCbCr();
		
		tmp.setY(getY());
		tmp.setCb(getCb()* ( Math.cos(Math.toRadians(value)) + Math.sin(Math.toRadians(value))));
		tmp.setCr(getCr()* ( Math.sin(Math.toRadians(value)) - Math.cos(Math.toRadians(value))));
		
		return tmp;
	}

	
	public RGB transformToRGB(){
		double tmpR=getY() + 1.402 * getCr();
		double tmpG=getY() - 0.3441*getCb() - 0.7141*getCr();
		double tmpB= getY() + 1.772*getCb();
		
		return new RGB(tmpR,tmpG,tmpB);
	}
	
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getCb() {
		return cb;
	}
	public void setCb(double cb) {
		this.cb = cb;
	}
	public double getCr() {
		return cr;
	}
	public void setCr(double cr) {
		this.cr = cr;
	}
	
	public boolean equals(YCbCr tmp){
		if(getY()==tmp.getY()){
			if(getCb()==tmp.getCb()){
				if(getCr()==tmp.getCr()){
					return true;
				}
			}
		}
		return false;
	}

	
}