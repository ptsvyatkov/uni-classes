package uebung2;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.color.*;


/**
     Opens an image window and adds a panel below the image
*/
public class GLDM_U2_S0559632 implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	IJ.open("C:\\about uni\\3sem\\GDM\\u2\\orchid.jpg");
    	
    	// Aufgabe 3 
    	// Exercise 3, making sure the values are the same after going from RGB to YUV and then back to RGB
    	// 12.05.2020 THIS DIDN'T WORK AT FIRST, ALL VALUES WERE OFF BY 1 
    	// 13.05.2020 Classmates told me to use Math.round in the YUV to RGB method... WORKS!
    	// 13.05.2020 Apparently, also in the YUVtoRGB method, the 3rd array element ( [2] ) has to come
    	// on 2nd place, because we need the blue value to get the green value([1])
    	testConversionRGB_To_YUV_To_RGB();
		
		GLDM_U2_S0559632 pw = new GLDM_U2_S0559632();
		pw.imp = IJ.getImage();
		pw.run("");
	}
    
    public void run(String arg) {
    	if (imp==null) 
    		imp = WindowManager.getCurrentImage();
        if (imp==null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);
        
        storePixelValues(imp.getProcessor());
        
        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
    	width = ip.getWidth();
		height = ip.getHeight();
		
		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {
    
        CustomCanvas(ImagePlus imp) {
            super(imp);
        }
    
    } // CustomCanvas inner class
    
    
    class CustomWindow extends ImageWindow implements ChangeListener {
    	/*
		 * Need to add sliders for Saturation,Contract,Hue
		 * then need to add respective (double) values for them to be manipulated
		 * ==> (need to rename jSlider2 to whatever I do next)
		 */

    	// First slider/variable was already here
        private JSlider jSliderBrightness;
        private double brightness; 
        
        // Added 3 new sliders and variables to manipulate contrast, saturation, hue
     	private JSlider jSliderContrast; // Renamed from jSlider2 to Contrast with Refactoring( to apply everywhere )
		private double contrast;
		
		private JSlider jSliderSaturation;
		private double saturation;
		
		private JSlider jSliderHue; 
		private double hue;
		
		
		CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
            
            // Haven't tested if this makes any difference, a classmate noted this may be needed.
            brightness = 0;
            contrast = 1;
            saturation = 1;
        }
    
		// Creates the panel with the sliders, their names, adds it and packs to make it fit size of components
        void addPanel() {
        	//JPanel panel = new JPanel();
        	Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            
            jSliderBrightness = makeTitledSlider("Helligkeit", -128, 128, 0);
            jSliderContrast = makeTitledSlider("Kontrast", 0, 100, 50); 
            jSliderSaturation = makeTitledSlider("Saettigung", 0, 50, 25);	
            jSliderHue = makeTitledSlider("Hue", 0, 360, 0);
            panel.add(jSliderBrightness);
            panel.add(jSliderContrast);
            panel.add(jSliderSaturation); 
            panel.add(jSliderHue);
            add(panel);
            
            pack();
         }
        
        // Creates a new Slider, I need to use this to make the other 3 sliders
        private JSlider makeTitledSlider(String string, int minVal, int maxVal, int val) {
		
        	JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
        	Dimension preferredSize = new Dimension(width, 50);
        	slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);
			
			return slider;
		}
        
        private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
				str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}
        
        // Whenever we drag the sliders, the state is changed
		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();

			// manipulating the Helligkeit slider
			if (slider == jSliderBrightness) {
				brightness = slider.getValue(); 				
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderBrightness, str); 
			}
			
			// manipulating the Kontrast slider
			if (slider == jSliderContrast) {
				contrast = slider.getValue(); //nice way to use this afterwards
				if ( contrast > 50 && contrast <= 60) { contrast = slider.getValue() / 30.0; }
				else if ( contrast > 60 && contrast <= 70) { contrast = Math.round(slider.getValue() / 18.0); }
				else if ( contrast > 70 && contrast <= 80) { contrast = Math.round(slider.getValue() / 13.0); }
				else if ( contrast > 80 && contrast <= 90) { contrast = Math.round(slider.getValue() / 11.2); }
				else if ( contrast > 90 && contrast <= 100) { contrast = slider.getValue() / 10.0; }
				else { contrast = slider.getValue() / 50.0 ;}
				String str = "Kontrast " + contrast; 
				setSliderTitle(jSliderContrast, str); 
			}
			
			// manipulating the Farbsaetiggung / Saturation slider
			if(slider == jSliderSaturation) {
				saturation = slider.getValue();
				if ( saturation > 25 && saturation <= 30) { saturation = slider.getValue() / 15.0; }
				else if ( saturation > 30 && saturation <= 35) { saturation = Math.round(slider.getValue() / 14.0); }
				else if ( saturation > 35 && saturation <= 40) { saturation = Math.round(slider.getValue() / 10.0); }
				else if ( saturation > 40 && saturation <= 50) { saturation = Math.round(slider.getValue() / 10.0); }
				else { saturation = slider.getValue() / 25.0 ;}
				String str = "Saettigung" + saturation; 
				setSliderTitle(jSliderSaturation, str);
			}
			
			// manipulating the Hue slider
			if(slider == jSliderHue) {
				hue = slider.getValue();
				String str = "Hue" + hue;
				setSliderTitle(jSliderHue, str);
			}
			
			changePixelValues(imp.getProcessor());
			
			imp.updateAndDraw();
		}

		private void changePixelValues(ImageProcessor ip) {
			
			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			// To calculate the Hue, we use the formula radians = degrees * (pi/180)
			// https://stackoverflow.com/questions/135909/what-is-the-method-for-converting-radians-to-degrees/135930
			
			double fi = hue * Math.PI / 180;
			double sine = Math.sin(fi);	
			double cosine = Math.cos(fi);	
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					// anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
					// die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren ( should be YUV )
			//--------------------------------------------------------------------------------------------------
					/*
					 * We need to use Y(Helligkeit) U V in this exercise, formulas are given on the exercise page
					 * (if B-Y is 0 for example U is 0 , same goes for V) ==> From Podcast 7 
					 * First we need to transform the values to Y U V
					 */
					
					// After creating the 2 methods to transform rgb->yuv and back to rgb ( at the end of code )
					// now we need to call the color transformation in this method (changePixelValues)
					
					// First creating an array of doubles to hold the converted values
					double[] yuv = convertRGB_To_YUV(r,g,b);

					/*
					 * Then we need to change the brightness, contrast, saturation, hue
					 * TODO Alles wird hier verändert
					 */
					// Calculating the contrast and brightness using the new values
					yuv[0] = (int) ((contrast) * yuv[0] + brightness); // Y
					
					// Calculating the saturation using the new YUV values
					yuv[1] = (int) (yuv[1] * (saturation));
					yuv[2] = (int) (yuv[2] * (saturation));

					
					// Applying the sine/cosine formula
					double valueU = yuv[1];
					yuv[1] = (int) ((cosine * yuv[1]) - (sine * yuv[2]));
					yuv[2] = (int) ((sine * valueU) + (cosine * yuv[2]));

					/*
					 * Then back to RGB values 
					 * TODO zurück nach RGB
					 */
					
					int convertedBackToRGB[] = convertYUV_To_RGB(yuv[0], yuv[1], yuv[2]);

					int rn = convertedBackToRGB[0];
					int gn = convertedBackToRGB[1];
					int bn = convertedBackToRGB[2];

					
					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
					/*
					 * Need to add a quick if statement, if the values go beyond 255, change it to 255
					 * if the values goes beneath 0, change it to 0 ==> so there is no overflow ( uberlauf )
					 */
					
					// Red
					rn = correctOverflow(rn);
					
					// Green
					gn = correctOverflow(gn);
					
					// Blue
					bn = correctOverflow(bn);

					pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
				}
			}
			
		}
		
    } // CustomWindow inner class
    
    // Aufgabe 3 Farbtransformation von RGB nach YUV 
    // Y - Luminanz , U - U Kanal, V - V Kanal
    public static double[] convertRGB_To_YUV(int r, int g, int b) {
		double[] rgbToYUV = new double[3];
		rgbToYUV[0] = 0.299 * r + 0.587 * g + 0.114 * b; 	// (Y) Luminanz
		rgbToYUV[1] = (b - rgbToYUV[0]) * 0.493; 			//  U Kanal
		rgbToYUV[2] = (r - rgbToYUV[0]) * 0.877; 			//  V Kanal
		return rgbToYUV;
	}
    
    
    // Aufgabe 3 Farbtransformation von YUV nach RGB
    // 13.05.2020 EDIT: Classmates noted to me that i should use Math.round when converting
    // Now it works correctly!
    public static int[] convertYUV_To_RGB(double y, double u, double v) {
		int[] yuvToRGB = new int[3];
		yuvToRGB[0] = (int) Math.round(y + v/0.877); 					
		yuvToRGB[2] = (int) Math.round(y + u/0.493); 
		yuvToRGB[1] = (int) Math.round(1/0.587 * y - ((0.299/0.587)*yuvToRGB[0]) - ((0.114/0.587) * yuvToRGB[2]));
		return yuvToRGB;
	}
    
    public static int correctOverflow(int value) {
    	int temp = value;
		if (value > 255) { temp = 255; }
		else if (value < 0) { temp = 0; }
		return temp;
    }
    
    // Überprüfen Sie, ob das Ergebnis nach erfolgter Hin- und Rücktransformation wieder dem Original entspricht.
    // Need to check if the rgb values are the same if i convert them to YUV and then convert
    // them back to RGB, to make sure there are no rounding errors
    private static void testConversionRGB_To_YUV_To_RGB() {
    	
    	// I was discussing this with some other students from the other semesters and they
    	// guided me in the right direction on how to properly test this. 
		for (int i = 0; i<= 255; i++) {
		 for (int j = 0; j<= 255; j++) {
		  for (int k = 0; k<= 255; k++) {
					
			 //  Transform the RGB values to YUV and save them in a double array transformedToYUV
			double transformedToYUV[] = convertRGB_To_YUV( i, j, k);
			
			// This time use the transformed values as parameters to transform back to the original RGB
			// AND save the values in an int array transformedBackToRGB
			int transformedBackToRGB[] = convertYUV_To_RGB(transformedToYUV[0], transformedToYUV[1], transformedToYUV[2]);
			
			// NOW : Compare the values to the original ones
			 if ( i != transformedBackToRGB[0] || 
				  j != transformedBackToRGB[1] ||
				  k != transformedBackToRGB[2]	)
				  {System.out.println("Something is wrong with the conversion. Check your formulas.");}
		  }
		 }
		}
		System.out.println("Exercise 3, testing RGB to YUV and back to RGB. Test successful.");
	}
} 