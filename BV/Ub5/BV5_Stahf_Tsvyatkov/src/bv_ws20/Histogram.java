// BV Ue4 SS2020 Vorgabe
//
// Copyright (C) 2019 by Klaus Jung
// All rights reserved.
// Date: 2019-05-12

package bv_ws20;

import java.util.ArrayList;
import java.util.List;

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
    private int upperBorder;
    private int lowerBorder;
    
    private int[] histogram = new int[grayLevels];
    
	
	public Histogram(GraphicsContext gc, int maxHeight) {
		this.gc = gc;
		this.maxHeight = maxHeight;
	}
	
	
	// The upper bound of the selected gray levels on the histogram is given as selectionMax
	// We need to use: for (StatsProperty property : statsData), then a switch statement for cases Level,Min,Max,Mean etc.
	// property.setValue() --> with this we can assign a value to show 
	// property.setValueInPercent() --> use this to assign a value in percent
	public void update(RasterImage image, Point2D ellipseCenter, Dimension2D ellipseSize, int selectionMax, ObservableList<StatsProperty> statsData) {
		histogram = new int[grayLevels]; // resetting the array
		
		int amountOfPixels = 0; // amount of pixels within the red border
		double amountSelected = 0; // amount of gray levels selected in the histogram
		int minimum = Integer.MAX_VALUE; // minimum value
		int maximum = 0; // maximum value
		int sumOfAllValues = 0; // all values summed up
		List<Integer> medianList = new ArrayList<>(); // all values in an ArrayList
		
		for(int x = 0; x<image.width; x++) {
			for(int y = 0; y<image.height; y++) {
				
				// scanline postion is saved
				int pos = y*image.width + x;
				
				if(1 >= ((Math.pow(x - ellipseCenter.getX(), 2)) / Math.pow(ellipseSize.getWidth()/2, 2)) +
						 (Math.pow(y - ellipseCenter.getY(), 2) / Math.pow(ellipseSize.getHeight()/2, 2))) {
					
					// pulling the argb value out of the array
					int pixel = image.argb[pos];
					
					// pulling gray out of argb value
					int gray = (pixel >> 16) & 0xff;
					
					
					histogram[gray]++;
					
					amountOfPixels++;
					
					//Calculating levels
					if(gray<=selectionMax) {
						amountSelected++;
					}
					//Getting the minimum value
					if(gray<minimum) {
						minimum = gray;
					}
					//Getting maximum value
					if(gray>maximum) {
						maximum = gray;
					}
					// Summing up all to get mean 
					sumOfAllValues += gray;
					medianList.add(gray);
				}	
			}
		}
		// Remark: Please ignore selectionMax and statsData in Exercise 4. It will be used in Exercise 5.
		
		for(StatsProperty property : statsData) {
			switch	(property) {
				case Level: 
					property.setValueInPercent(amountSelected/amountOfPixels);
					break;
				case Minimum:
					property.setValue(minimum);
					break;
				case Maximum:
					property.setValue(maximum);
					break;
				case Mean:
					try {property.setValue((double)sumOfAllValues/amountOfPixels);} 
					catch (ArithmeticException ae) {} // occasionally throws exceptions if the ellipse is too small
					break;
				case Median:
					try {
						medianList.sort(null);
						int median = medianList.get((int) (medianList.size()/2)); // takes lower-median for even numbers of medianList.size()
						property.setValue(median);
					} catch (IndexOutOfBoundsException e) {} // occasionally throws exceptions if the ellipse is too small
					break;
				
					// Alternative calculation using the probability density distribution, 05 - Slide 17
				case Variance:
					double variance = 0;
					double meanValue = (double)sumOfAllValues/amountOfPixels;
					for(int i = 0; i<histogram.length ; i++) {
						int current = histogram[i];
						variance = variance + Math.pow((i-meanValue),2)*current;
					}
					variance = (variance / amountOfPixels);
					property.setValue(variance);
					break;
					//https://www.cs.princeton.edu/courses/archive/spring17/cos126/docs/mid1-s17/ShannonEntropy.java
					// p(j) are the probabilities with which the individual color values j occur in our gray image
					// so we divide by the amount of all pixels to get the probability
				case Entropy:
					double entropy = 0;
					
					for(int i = minimum; i<=maximum; i++) {
						double p = 1.0 * histogram[i] / amountOfPixels;
						if(histogram[i] != 0) {
							entropy = entropy + (-p) * (Math.log(p))/Math.log(2);
						}
					}
					property.setValue(entropy);
					break;
				default:
					break;			
			}
		}		
		try {upperBorder = medianList.get((int) (medianList.size()*0.995));
		} catch (IndexOutOfBoundsException e) {}
		
		try {
		lowerBorder = medianList.get((int) (medianList.size()*0.005));
		} catch (IndexOutOfBoundsException e) {}
	}
	
	public int getBrightnessOffset() {
		int offSet = 0;
		int sum = 0;
		List<Integer> histogramAC = new ArrayList<>();
		for(int i = lowerBorder; i<upperBorder; i++) {
			for(int j = 0; j<histogram[i]; j++) {
				histogramAC.add(i);
				sum += i;
			}
		}
		histogramAC.sort(null);
		int acMedian = histogramAC.get((int) (histogramAC.size()/2));
		int acMean = sum/histogramAC.size();
		offSet = acMedian-128;
		return offSet;
	}
	
	public int getUpper() {
		return upperBorder;
	}
	
	public int getLower() {
		return lowerBorder;
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
		// looping over the 256 grayLevels
		for(int i = 0; i<histogram.length; i++) {
			// the x point is always moving 1 pixel at a time, only the height changes
			gc.strokeLine(i+shift, maxHeight, i+shift, maxHeight-maxHeight*histogram[i]/max);
		}
	}
    
}
