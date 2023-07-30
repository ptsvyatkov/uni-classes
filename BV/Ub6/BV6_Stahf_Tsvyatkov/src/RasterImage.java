import java.io.File;
import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RasterImage {
	
	private static final int gray  = 0xffa0a0a0;
	
	public int[] argb;	// pixels represented as ARGB values in scanline order
	public int width;	// image width in pixels
	public int height;	// image height in pixels
	public double entropy;
	
	public RasterImage(int width, int height) {
		// creates an empty RasterImage of given size
		this.width = width;
		this.height = height;
		argb = new int[width * height];
		calculateEntropy();
		Arrays.fill(argb, gray);
	}
	
	public RasterImage(RasterImage src) {
		// copy constructor
		this.width = src.width;
		this.height = src.height;
		argb = src.argb.clone();
		calculateEntropy();
	}
	
	public RasterImage(File file) {
		// creates an RasterImage by reading the given file
		Image image = null;
		if(file != null && file.exists()) {
			image = new Image(file.toURI().toString());
		}
		if(image != null && image.getPixelReader() != null) {
			width = (int)image.getWidth();
			height = (int)image.getHeight();
			argb = new int[width * height];
			image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
		} else {
			// file reading failed: create an empty RasterImage
			this.width = 375;
			this.height = 225;
			argb = new int[width * height];
			Arrays.fill(argb, gray);
		}
		calculateEntropy();
	}
	
	public RasterImage(ImageView imageView) {
		// creates a RasterImage from that what is shown in the given ImageView
		Image image = imageView.getImage();
		width = (int)image.getWidth();
		height = (int)image.getHeight();
		argb = new int[width * height];
		image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
		calculateEntropy();
	}
	
	public void setToView(ImageView imageView) {
		// sets the current argb pixels to be shown in the given ImageView
		if(argb != null) {
			WritableImage wr = new WritableImage(width, height);
			PixelWriter pw = wr.getPixelWriter();
			pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
			imageView.setImage(wr);
		}
	}
	
	public void convertToGray() {
		// Just like we did in the GDM exercise to make a grayscale
		// looping over all the pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				//using scanline order for the position of the pixels
				int pos = y*width + x;
				
				//storing each position from the array argb in argb2
				int argb2 = argb[pos];  // Storing the original values in argb2
				
				// Extracting the color values
				int r = (argb2 >> 16) & 0xff;
				int g = (argb2 >>  8) & 0xff;
				int b =  argb2        & 0xff;
				
				//taking the average of the three colors
				int grayscale = (r+g+b)/3;

				argb[pos] = (0xFF<<24) | (grayscale<<16) | (grayscale<<8) | grayscale;
			}
		}
		
	}
	
	// p(j) are the probabilities with which the individual color values j occur in our gray image
	// so we divide by the amount of all pixels to get the probability
	public void calculateEntropy() {
		int[] histogram = new int[256];
		Arrays.fill(histogram, 0);

		for (int i = 0; i < argb.length; i++) {
			int val = argb[i] & 0xFF;
			histogram[val]++;
		}

		int amountOfPixels = width * height;
		double entropy = 0;
		for (int i = 0;i<histogram.length;i++) {
			double prob = 1.0* histogram[i] / amountOfPixels;
			if (histogram[i] != 0) {
				entropy -= prob * (Math.log(prob) / Math.log(2));
		}
		this.entropy = entropy;
	}
}
}