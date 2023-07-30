// BV Ue2 WS20/21 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws20;


public class GeometricTransform {

	public enum InterpolationType { 
		NEAREST("Nearest Neighbour"), 
		BILINEAR("Bilinear");
		
		private final String name;       
	    private InterpolationType(String s) { name = s; }
	    public String toString() { return this.name; }
	};
	
	public void perspective(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion, InterpolationType interpolation) {
		switch(interpolation) {
		case NEAREST:
			perspectiveNearestNeighbour(src, dst, angle, perspectiveDistortion);
			break;
		case BILINEAR:
			perspectiveBilinear(src, dst, angle, perspectiveDistortion);
			break;
		default:
			break;	
		}
		
	}

	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param s amount of the perspective distortion 
	 */
	public void perspectiveNearestNeighbour(RasterImage src, RasterImage dst, double angle, double s) {
		for(int x = 0; x<dst.width; x++) {
			for(int y = 0; y<dst.height; y++) {
				int pos = y*dst.width + x;	// scan-line position is saved
				
				// translation
				x = x-dst.width/2;
				y = y-dst.height/2;
			
				// rotation
				int xSrc =  (int) (x / (Math.cos(Math.toRadians(angle)) - x * s * Math.sin(Math.toRadians(angle))));
				int ySrc =  (int) (y * (s * Math.sin(Math.toRadians(angle)) * xSrc + 1));
				
				// re-translation
				x = x+dst.width/2;
				y = y+dst.height/2;
				xSrc = xSrc+src.width/2;
				ySrc = ySrc+src.height/2;
				
				// source scan-line position
				int srcPos = ySrc*src.width + xSrc;
				
				// if pixel is within source image
				if (ySrc<src.height && xSrc < src.width && ySrc >= 0 && xSrc >=0) {
                    dst.argb[pos] = src.argb[srcPos];
                } else {
                    dst.argb[pos] = 0xFFFFFF; // white
                }
			}
		}
	}
		
	


	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param s amount of the perspective distortion 
	 */
	public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double s) {
		for(int x = 0; x<dst.width; x++) {
			for(int y = 0; y<dst.height; y++) {
				int pos = y*dst.width + x;	// scan-line position is saved
				
				// translation
				x = x-dst.width/2;
				y = y-dst.height/2;
			
				// rotation
				double xSrc = x / (Math.cos(Math.toRadians(angle)) - x * s * Math.sin(Math.toRadians(angle)));
				double ySrc = y * (s * Math.sin(Math.toRadians(angle)) * xSrc + 1);
				
				// re-translation
				x = x+dst.width/2;
				y = y+dst.height/2;
				xSrc = xSrc+src.width/2;
				ySrc = ySrc+src.height/2;
				
				// if no border pixel and within the source image
				if(xSrc>0 && xSrc<src.width-1 && ySrc>0 && ySrc<src.height-1) {
					// find nearest pixels
					int a = (int) (Math.round(ySrc-0.5) * src.width + Math.round(xSrc-0.5));
					int b = (int) (Math.round(ySrc-0.5) * src.width + Math.round(xSrc+0.5));
					int c = (int) (Math.round(ySrc+0.5) * src.width + Math.round(xSrc-0.5));
					int d = (int) (Math.round(ySrc+0.5) * src.width + Math.round(xSrc+0.5));
					
					// extracting each color values
					int rA = (src.argb[a] >> 16) & 0xff;
					int gA = (src.argb[a] >> 8)  & 0xff;
					int bA =  src.argb[a]        & 0xff;
					int rB = (src.argb[b] >> 16) & 0xff;
					int gB = (src.argb[b] >> 8)  & 0xff;
					int bB =  src.argb[b]        & 0xff;
					int rC = (src.argb[c] >> 16) & 0xff;
					int gC = (src.argb[c] >> 8)  & 0xff;
					int bC =  src.argb[c]        & 0xff;
					int rD = (src.argb[d] >> 16) & 0xff;
					int gD = (src.argb[d] >> 8)  & 0xff;
					int bD =  src.argb[d]        & 0xff;
					
					// measure distance to nearest pixels (a in this case)
					double h = xSrc-(int) xSrc;
					double v = ySrc-(int) ySrc;
					
					// calculing bilinear interpolation
					int rn = (int) (rA*(1-h)*(1-v) + rB*h*(1-v) + rC*(1-h)*v + rD*h*v);
					int gn = (int) (gA*(1-h)*(1-v) + gB*h*(1-v) + gC*(1-h)*v + gD*h*v);
					int bn = (int) (bA*(1-h)*(1-v) + bB*h*(1-v) + bC*(1-h)*v + bD*h*v);
					
					// limiting color values to 0-255
					rn = limitRGB(rn);
					gn = limitRGB(gn);
					bn = limitRGB(bn);
					
					// passing over new pixel value
	                dst.argb[pos] = (0xff<<24) | (rn<<16) | (gn<<8) | (bn);
                } else {
                    dst.argb[pos] = 0xFFFFFF; // white
                }
			}
		}
 	}
	
	private int limitRGB(int colorValue){
		// Method to limit the RGB value of a pixel between 0 and 255.
		if (colorValue>255){
			return 255;
		}
		if (colorValue<0){
			return 0;
		}
		return colorValue;
	}


}
