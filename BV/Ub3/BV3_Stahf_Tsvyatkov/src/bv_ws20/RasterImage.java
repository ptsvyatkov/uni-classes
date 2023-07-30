// BV Ue3 WS2019/20 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws20;

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
	
	public RasterImage(int width, int height) {
		// creates an empty RasterImage of given size
		this.width = width;
		this.height = height;
		argb = new int[width * height];
		Arrays.fill(argb, gray);
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
	
	public void binarize(int threshold) {
		for(int x = 0; x<width; x++) {
			for(int y = 0; y<height; y++) {
				int pos = y*width + x;	// scan-line position is saved
				
				// extracting color values
				int r = (argb[pos] >> 16) & 0xff;
				int g = (argb[pos] >> 8)  & 0xff;
				int b =  argb[pos]        & 0xff;
				// calculating grey
				int grey = (r+g+b)/3;
				
				if(grey <= threshold) { // if smaller than threshold
					argb[pos] = 0xFF000000; // black
				} else {				// if bigger than threshold
					argb[pos] = 0xFFFFFFFF; // white
				}
			}
		}
	}
	
	public void invert() {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pos = y*width + x;
                    int argb = this.argb[pos];  // Reading original color value

                    int r = (argb >> 16) & 0xff;
                    int g = (argb >>  8) & 0xff;
                    int b =  argb        & 0xff;

                    // reversing the colors
                    int rn = 255 - r;
                    int gn = 255 - g;
                    int bn = 255 - b;

                    this.argb[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                }
            }
	}
	

	
}
