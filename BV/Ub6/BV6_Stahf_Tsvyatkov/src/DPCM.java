import java.util.Arrays;

public class DPCM {
	private RasterImage orig;
	private RasterImage error; // the error values of out image in scanline order
	private RasterImage reconstructed; // the reconstructed image in scanline order
	private String type;
	private static final int gray  = 0xffa0a0a0;
	private double MSE;
	
	public DPCM (RasterImage original, String type) {
		this.orig = original;
		this.error = new RasterImage(orig.width, orig.height);
		this.reconstructed = new RasterImage(orig.width, orig.height);
		this.type = type;
		Arrays.fill(reconstructed.argb, 0xFF000000);
		apply();
	}
	
	private void apply() {
		switch (type) {
		case "A":
			calcError(-1, 0);
			calcRec(-1, 0);
			calcMSE(orig.argb, reconstructed.argb);
			break;
		case "B":
			calcError(0 , -1);
			calcRec(0, -1);
			calcMSE(orig.argb, reconstructed.argb);
			break;
		case "C":	
			calcError(-1 , -1);
			calcRec(-1, -1);
			calcMSE(orig.argb, reconstructed.argb);
			break;
		case "ABC":
			DPCM a = new DPCM(orig, "A");
			DPCM b = new DPCM(orig, "B");
			DPCM c = new DPCM(orig, "C");
			calcError(a,b,c);
			calcMSE(orig.argb, reconstructed.argb);
		}
		
	}

	private void calcError(DPCM a, DPCM b, DPCM c) {
		for(int x = 0; x<error.width; x++) {
			for(int y = 0; y<error.height; y++) {
				
				int pos = y*orig.width + x;
				
				int va = (a.error.argb[pos] >> 16) & 0xff;
				int vb = (b.error.argb[pos] >> 16) & 0xff;
				int vc = (c.error.argb[pos] >> 16) & 0xff;
				
				int v = va + vb - vc;
				
				v = limitRGB(v);
				
				error.argb[pos] = (0xFF<<24) | (v<<16) | (v<<8) | v;
			}
		}
	}

	private void calcError(int xp, int yp) {
		for(int x = 0; x<error.width; x++) {
			for(int y = 0; y<error.height; y++) {
				
				int pos = y*orig.width + x;
				int posPrev = (y+yp)*orig.width + x + xp;
				
				if(x==0 && y==0) {
					
					int v = (orig.argb[pos] >> 16) & 0xff;
					error.argb[pos] = (0xFF<<24) | (v<<16) | (v<<8) | v;
					
				} else if (y==0 || x == 0) {
					
					int v = (orig.argb[pos] >> 16) & 0xff;
					int vPrev = (orig.argb[pos-1] >> 16) & 0xff;
					
					int errorValue = v-vPrev;
					
					v = 128+errorValue;
					
					v = limitRGB(v);
					
					// the final argb value is written back
					error.argb[pos] = (0xFF<<24) | (v<<16) | (v<<8) | v;
				
				} else {
					
					// pulling colors out of argb value
					int v = (orig.argb[pos] >> 16) & 0xff;
					int vPrev = (orig.argb[posPrev] >> 16) & 0xff;
					
					int errorValue = v-vPrev;
					
					v = 128+errorValue;
					
					v = limitRGB(v);
					
					// the final argb value is written back
					error.argb[pos] = (0xFF<<24) | (v<<16) | (v<<8) | v;
				}
			}
		}
	}
	
	private void calcRec(int xp, int yp) {
		for(int x = 0; x<reconstructed.width+xp; x++) {
			for(int y = 0; y<reconstructed.height+yp; y++) {
				
				int pos = y*error.width + x;
				int posPrev = (y+yp)*error.width + x + xp;
				
				if(x==0 && y==0) {
					
					int v = (error.argb[pos] >> 16) & 0xff;
					reconstructed.argb[pos] = (0xFF<<24) | (v<<16) | (v<<8) | v;
					
					
				} else if (y==0 || x == 0) {
					
					// pulling colors out of argb value
					int v = (error.argb[pos] >> 16) & 0xff;
					int vPrev = (reconstructed.argb[pos-1] >> 16) & 0xff;
					
					int errorValue = v-128;
					
					v = vPrev+errorValue;
					
					v = limitRGB(v);
					
					// the final argb value is written back
					reconstructed.argb[pos] = (0xFF<<24) | (v<<16) | (v<<8) | v;
					
					
				} else {
					
					// pulling colors out of argb value
					int v = (error.argb[pos] >> 16) & 0xff;
					int vPrev = (reconstructed.argb[posPrev] >> 16) & 0xff;
					
					int errorValue = v-128;
					
					v = vPrev+errorValue;
					
					v = limitRGB(v);
					
					// the final argb value is written back
					reconstructed.argb[pos] = (0xFF<<24) | (v<<16) | (v<<8) | v;
				}
			}
		}
	}
	
	private int limitRGB(int colorValue){
		// Method to limit the RGB value of a pixel between 0 and 255.
		if (colorValue>255){
			colorValue = 255;
		}
		if (colorValue<0){
			colorValue = 0;
		}
		return colorValue;
	}
	
	// https://www.oreilly.com/library/view/mastering-java-for/9781782174271/f2aad1aa-9f16-4fd1-9bbc-295cf52da842.xhtml
	private void calcMSE(int[] orig, int[] reconstructed) {
		int n = orig.length; 
		double sum = 0.0;

		for (int i = 0; i < n; i++) {
			int diff = reconstructed[i] - reconstructed[i];
			sum = sum + diff * diff;
		}

		this.MSE = sum / n;
	}
	
	public double getMSE() {
		return this.MSE;
	}
	
	public RasterImage getErrorImg() {
		return error;
	}
	
	public RasterImage getRecImg() {
		return reconstructed;
	}
}
