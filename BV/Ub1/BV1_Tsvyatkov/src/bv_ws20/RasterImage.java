// BV Ue1 WS2020/21 Vorgabe
//
// Copyright (C) 2019-2020 by Klaus Jung
// All rights reserved.
// Date: 2020-10-08

package bv_ws20;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

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
	
	public RasterImage(int width, int height) {
		// creates an empty RasterImage of given size
		this.width = width;
		this.height = height;
		argb = new int[width * height];
		Arrays.fill(argb, gray);
	}
	
	public RasterImage(RasterImage src) {
		// copy constructor
		this.width = src.width;
		this.height = src.height;
		argb = src.argb.clone();
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
			this.width = 256;
			this.height = 256;
			argb = new int[width * height];
			Arrays.fill(argb, gray);
		}
	}
	
	public RasterImage(ImageView imageView) {
		// creates a RasterImage from that what is shown in the given ImageView
		Image image = imageView.getImage();
		width = (int)image.getWidth();
		height = (int)image.getHeight();
		argb = new int[width * height];
		image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
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
	
	
	// image point operations to be added here
	
	public void convertToGray() {
		// TODO: convert the image to grayscale
		// Just like we did in the GDM exercise to make a grayscale
		int rn = 0;
		int gn = 0;
		int bn = 0;
		// looping over all the pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				//using scanline order for the position of the pixels
				int pos = y*width + x;
				
				//storing each position from the array argb in argb2
				int argb2 = argb[pos];  // Storing the original values in argb2

				int r = (argb2 >> 16) & 0xff;
				int g = (argb2 >>  8) & 0xff;
				int b =  argb2        & 0xff;
				
				//taking the average of the three colors
				int grayscale = (r+g+b)/3;
				
				// in a grayscale image the values for each r,g,b are all equal
				 rn = grayscale;
				 gn = grayscale;
				 bn = grayscale;

				argb[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
			}
		}
		
	}
	
	/**
	 * @param quantity The fraction of pixels that need to be modified
	 * @param strength The brightness to be added or subtracted from a pixel's gray level
	 */
	public void addNoise(double quantity, int strength) {
		// TODO: add noise with the given quantity and strength
		int rn = 0;
		int gn = 0;
		int bn = 0;
		Random rand = new Random();
		
		// all the pixels in the image
		int pixels = width * height;
		
		//percentage proportion of the changed pixels when dragging Noise Quantity slider
		double changedPixels = pixels*quantity;
		
		for (int i = 0; i < changedPixels; i++) {
		 //determines random pixel position
		 int pos = rand.nextInt(pixels);
		
		 int argb2 = argb[pos];
		 
		 int r = (argb2 >> 16) & 0xff;
		 int g = (argb2 >>  8) & 0xff;
		 int b =  argb2        & 0xff;
		 
		 // chose randomly if pixel gets darker or lighter
		 // rand.nextInt(255) --> numbers 0 inclusive to 255 exclusive
		 // if between 128 and 255 add strength
		 if(rand.nextInt(255) >= 128) {
		 rn = r + strength;
		 gn = g + strength;
		 bn = b + strength;
		 }
		 // else (under 128) subtract strength
		 else {
			 rn = r - strength;
			 gn = g - strength;
			 bn = b - strength;
		 }
		 //not allowing values above 255 and below 0
		 rn = correctOverflow(rn);
		 gn = correctOverflow(gn);
		 bn = correctOverflow(bn);

		 argb[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
		 }
		}
	
	// Sets an upper and lower bound for the pixel values
	public static int correctOverflow(int value) {
    	int temp = value;
		if (value > 255) { temp = 255; }
		else if (value < 0) { temp = 0; }
		return temp;
    }

}
