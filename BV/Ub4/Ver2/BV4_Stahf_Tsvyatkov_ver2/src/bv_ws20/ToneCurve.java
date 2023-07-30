// BV Ue4 SS2020 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-16

package bv_ws20;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ToneCurve {
	
	private static final int grayLevels = 256;
	
    private GraphicsContext gc;
    
    private int brightness = 0;
    private double gamma = 1.0;
    
    private int[] grayTable = new int[grayLevels];

	public ToneCurve(GraphicsContext gc) {
		this.gc = gc;
	}
	
	public void setBrightness(int brightness) {
		this.brightness = brightness;
		updateTable();
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
		updateTable();
	}

	private void updateTable() {
		for(int i = 0; i<grayTable.length; i++) {
			
			int grayIn = i+brightness; // applying brightness
			
			int grayOut = (int) ((255* (Math.pow(grayIn, 1/gamma))) / (Math.pow(255, 1/gamma))); // applying gamma
			
			grayOut = limitRGB(grayOut); // limit between 0-255
			
			grayTable[i] = grayOut;	// save in table
			
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
	
	public int mappedGray(int inputGray) {
		return grayTable[inputGray];
	}
	
	public void draw() {
		gc.clearRect(0, 0, grayLevels, grayLevels);
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(3);

		gc.beginPath();
		
		for(int i = 0; i<255; i++) {
			gc.lineTo(i, 255-grayTable[i]);
		}
		
		gc.stroke();
	}

	
}
