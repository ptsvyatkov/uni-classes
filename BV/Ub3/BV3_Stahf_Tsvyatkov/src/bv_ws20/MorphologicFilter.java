// BV Ue3 WS2019/20 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws20;

public class MorphologicFilter {
	
	public enum FilterType { 
		DILATION("Dilation"),
		EROSION("Erosion");
		
		private final String name;       
	    private FilterType(String s) { name = s; }
	    public String toString() { return this.name; }
	};
	
	// filter implementations go here:
	
	public void copy(RasterImage src, RasterImage dst) {
		dst.argb = src.argb.clone(); // simple array-cloning
	}
	
	public void dilation(RasterImage src, RasterImage dst, double radius) {
		if(radius != 0.0) {
			// looping over the image
	    	for(int x = 0; x<src.width; x++) {
	        	for(int y = 0; y<src.height; y++) {

	            	int pos = y * src.width + x;    // scan-line position is saved
	                // if pixel is black and we are inside the image bounds
	                if(src.argb[pos] == 0xFF000000 && x>radius && x<src.width-radius-1 && y>radius && y<src.height-radius-1) {
	                	// loop for pixels within the radius (square)
	                	for(int i = (int) Math.round(-radius); i < radius; i++) {
	                    	for(int j = (int) Math.round(-radius); j < radius; j++) {
	                    		// determining if pixel is inside the "real" radius (circle)
	                        	if(i * i + j*j <= radius * radius){
	                            	dst.argb[pos+j*dst.width+i] = 0xFF000000; // make it black
	                        	} 
	                    	}
	                	}
	                } else {
	                	dst.argb[pos] = 0xFFFFFFFF; // make it white
	            	}
	        	}
	    	}
	    } else {
	    	dst.argb = src.argb;
		}
	}
	
	public void erosion(RasterImage src, RasterImage dst, double radius) {
		// in order to not change the picture in the middle, we created a temporary RasterImage for inversion
		RasterImage inv = new RasterImage(src.width,src.height);
		copy(src,inv);
		
		// dualism from lecture:
		inv.invert();
	    dilation(inv,dst, radius);
	    dst.invert();
	}
	
	
	
	

}
