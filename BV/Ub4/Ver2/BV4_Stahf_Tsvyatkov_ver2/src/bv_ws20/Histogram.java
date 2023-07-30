// BV Ue4 SS2020 Vorgabe
//
// Copyright (C) 2019 by Klaus Jung
// All rights reserved.
// Date: 2019-05-12

package bv_ws20;

import bv_ws20.ImageAnalysisAppController.StatsProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Histogram {

	private static final int grayLevels = 256;
	
    private GraphicsContext gc;
    private int maxHeight;
    
    private int[] histogram = new int[grayLevels];

	public Histogram(GraphicsContext gc, int maxHeight) {
		this.gc = gc;
		this.maxHeight = maxHeight;
	}
	
	public void update(RasterImage image, Point2D ellipseCenter, Dimension2D ellipseSize, int selectionMax, ObservableList<StatsProperty> statsData) {
		histogram = new int[grayLevels]; // resetting the array
		
		for(int x = 0; x<image.width; x++) {
			for(int y = 0; y<image.height; y++) {
				
				// scanline postion is saved
				int pos = y*image.width + x;
				
				if(1 >= ((Math.pow(x - ellipseCenter.getX(), 2)) / Math.pow(ellipseSize.getWidth()/2, 2)) + (Math.pow(y - ellipseCenter.getY(), 2) / Math.pow(ellipseSize.getHeight()/2, 2))) {
					/// pulling the argb value out of the array
					int pixel = image.argb[pos];
					
					// pulling gray out of argb value
					int gray = (pixel >> 16) & 0xff;
					
					histogram[gray]++;
				}
			}
		}
		// Remark: Please ignore selectionMax and statsData in Exercise 4. It will be used in Exercise 5.
		
	}
	    
	public void draw() {
		gc.clearRect(0, 0, grayLevels, maxHeight);
		gc.setLineWidth(1);

		double shift = 0.5;
		
		int max = 1;
		// evaluating maximum amount of any value
		for(int i = 0; i<histogram.length; i++) {
			if(histogram[i]>max) {
				max = histogram[i];
			}
		}
		
		// drawing the histogram
		gc.setStroke(Color.BLACK);
		for(int i = 0; i<histogram.length; i++) {
			gc.strokeLine(i+shift, maxHeight, i+shift, maxHeight-maxHeight*histogram[i]/max);
		}

	}
    
}
