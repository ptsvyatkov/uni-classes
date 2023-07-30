package uebung5;

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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
 */
public class GLDM_U5_S0559632 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Weichzeichnen", "Hochpassgefiltert", "Verstaerkte Kanten"};


	public static void main(String args[]) {

		IJ.open("C:\\about uni\\3sem\\GDM\\ue5\\sail.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GLDM_U5_S0559632 pw = new GLDM_U5_S0559632();
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


	class CustomWindow extends ImageWindow implements ItemListener {

		private String method;
		
		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			} 

		}


		private void changePixelValues(ImageProcessor ip) {

			// Array zum Zurückschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						
						pixels[pos] = origPixels[pos];
					}
				}
			}
			
			/*
			 *  Weichzeichnen = Blur/Smoothing ( Gaussian Blur )
			 *  Video 12 (Nachbarschaftsoperatoren) from Prof. Barthel. On minute 50:00
			 *  he shows how it works when pictures have different resolution
			 */
			
			if (method.equals("Weichzeichnen")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 
						
						int rn = 0;
						int gn = 0;
						int bn = 0;
						
						// We discussed this on Discord and I liked the following idea that someone shared:
						// we could start off with a simple 3x3 matrix and set each pixel's value to 0
						// There was a similar exercise in Info 2, where we were programming the Game of Life and
						// had to think about the pixel's position, so that helped a bit as well
						
						// first the middle pixel which represents x,y
						int middle = 0;
						
						// first row pixels
						int upperLeft = 0; // y-1, x-1
						int upperMiddle = 0; // y-1, x
						int upperRight = 0; // y-1 , x+1
						
						//second row pixels, we already have mid
						int left = 0; // left from middle
						int right = 0; // right from middle
						
						// third row pixels
						int lowerLeft = 0; //y+1, x-1
						int lowerMiddle = 0; // y+1, x
						int lowerRight = 0; // y+1, x+1
						
						// Need to stay in the allowed values(inside the borders) or we get IndexOutOfBounds exception
						if(x > 0 && y > 0) {
							if(x < width - 1 && y < height -1) {	
								middle = pos; // y*width + x
								left   = y*width + (x-1);
								right  = y*width + (x+1);
								
								upperLeft   =  (y-1)*width + (x-1);
								upperMiddle =  (y-1)*width +x;
								upperRight  =  (y-1)*width + (x+1);
								
								lowerLeft   =  (y+1)*width + (x-1);
								lowerMiddle =  (y+1)*width + x;
								lowerRight  =  (y+1)*width + (x+1);	
							}
						}
						// I'm glad other students pointed me in the right direction to just put them all in an array here..
						// it made everything so much easier
						int newPixels[] = { upperLeft, upperMiddle, upperRight, left, middle,
								right, lowerLeft, lowerMiddle, lowerRight };
						
						
						for (int j = 0; j < 9; j++) {
							int values = origPixels[newPixels[j]];
							
							int r = (values >> 16) & 0xff;
							int g = (values >>  8) & 0xff;
							int b =  values        & 0xff;
							
							// As shown on the exercise page and in Prof. Barthel's video we divide the values by 9
							// to get the desired end result of all pixels being 1/9 ( sum of all is 1 ) as in slides
							// Tiefpassfilter (Mittelwertbildung) Summe der Filterkoeffizienten = 1
							
							// Also found an interesting link while searching a bit more about this: 
							// https://diffractionlimited.com/help/maximdl/Low-Pass_Filtering.htm
							rn = rn + r/9;
							gn = gn + g/9;
							bn = bn + b/9;		
							
						}

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
					}
				}
			}
			
			/*
			 * High-pass filter
			 * Video 12 (Nachbarschaftsoperatoren) from Prof. Barthel. 56:20 "Hochpassfilter basteln"
			 * In the end we need to add +128
			 */
			if (method.equals("Hochpassgefiltert")){
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int values = origPixels[pos];  // Lesen der Originalwerte
						int rn, gn, bn = 0;
						
						// A lot of copy pasted code from Weichzeichner sadly, but I didn't manage to refactor
						// the code in a better way and pull the duplicated code outside..
						
						// I talked to my classmates about this exercise and we saw that there are a lot of
						// common points in the 3 filters we have to implement
						int middle = 0;
						int upperLeft = 0; 
						int upperMiddle = 0;
						int upperRight = 0; 
						int left = 0; 
						int right = 0; 
						int lowerLeft = 0; 
						int lowerMiddle = 0; 
						int lowerRight = 0; 
						
						int r = (values >> 16) & 0xff;
						int g = (values >>  8) & 0xff;
						int b =  values        & 0xff;
						
						// Later we are going to loop on everything except the middle pixel,
						// so that the other pixels have their correct -1/9 value
						rn = 8 * r / 9;						
						gn = 8 * g / 9;
						bn = 8 * b / 9;
						
						// Not going outside of the borders and not including the border(same as Weichzeichner)
						if(x > 0 && y > 0) {
							if(x < width - 1 && y < height -1) {	
								middle = pos; // y*width + x
								left   = y*width + (x-1);
								right  = y*width + (x+1);
								
								upperLeft   =  (y-1)*width + (x-1);
								upperMiddle =  (y-1)*width +x;
								upperRight  =  (y-1)*width + (x+1);
								
								lowerLeft   =  (y+1)*width + (x-1);
								lowerMiddle =  (y+1)*width + x;
								lowerRight  =  (y+1)*width + (x+1);	
							}
						}
						// not including the middle pixel from the 3x3 matrix this time,
						// because the sum of all pixel has to become 0 ( 8/9 + 8*(-1/9) )
						int newPixels[] = { upperLeft, upperMiddle, upperRight, left,
								right, lowerLeft, lowerMiddle, lowerRight };
						
						//Looping over all the pixels *except* the middle
						for (int j = 0; j < 8; j++) {
							int valuesHochpass = origPixels[newPixels[j]];
							
							int newRedVal   = (valuesHochpass >> 16) & 0xff;
							int newGreenVal = (valuesHochpass >>  8) & 0xff;
							int newBlueVal  =  valuesHochpass        & 0xff;							
						
							// By looking at the exercise sheet and the video, the end pixel
							// values should be -1/9 except the middle which is 8/9 and their sum is 0
							// Hochpassfilter (Differenzbildung) Summe der Filterkoeffizienten = 0
							rn = rn - newRedVal/9;
							gn = gn - newGreenVal/9;
							bn = bn - newBlueVal/9;							
						}						
						
						// Whoops, forgot to put the offset the first time and it looked black like
						// Prof. Barthel showed on the video before seeting Offset to 128
						rn = rn + 128;
						gn = gn + 128;
						bn = bn + 128;						
					
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;	
					}
			    }
			}
			
		   /*
			* I was not sure how to research in english about this, but i found this
			* Wikipedia link: https://de.wikipedia.org/wiki/Kantendetektion
			* While listening to Prof. Barthel's lecture i heard about the "Sobel-Operator"
			*/	 
			if (method.equals("Verstaerkte Kanten")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int values = origPixels[pos];  // Lesen der Originalwerte
						int rn, gn, bn = 0;
						
						// Same variables like in last filter
						int middle = 0;
						int upperLeft = 0; 
						int upperMiddle = 0;
						int upperRight = 0; 
						int left = 0; 
						int right = 0; 
						int lowerLeft = 0; 
						int lowerMiddle = 0; 
						int lowerRight = 0; 
						
						int r = (values >> 16) & 0xff;
						int g = (values >>  8) & 0xff;
						int b =  values        & 0xff;
						
						// Analogous to the previous filter, however this time we want the
						// pixel in the middle to have the value of 1
						rn = 17*r/9;
						gn = 17*g/9;
						bn = 17*b/9;
						
						// borders
						if(x > 0 && y > 0) {
							if(x < width - 1 && y < height -1) {	
								middle = pos; // y*width + x
								left   = y*width + (x-1);
								right  = y*width + (x+1);
								
								upperLeft   =  (y-1)*width + (x-1);
								upperMiddle =  (y-1)*width +x;
								upperRight  =  (y-1)*width + (x+1);
								
								lowerLeft   =  (y+1)*width + (x-1);
								lowerMiddle =  (y+1)*width + x;
								lowerRight  =  (y+1)*width + (x+1);	
							}
						}
						// Almost the same process like in the previous filters
						int newPixels[] = { upperLeft, upperMiddle, upperRight, left,
								right, lowerLeft, lowerMiddle, lowerRight };
						//Looping over all the pixels *except* the middle
						for (int j = 0; j < 8; j++) {
							int valuesHochpass = origPixels[newPixels[j]];
							
							int newRedVal   = (valuesHochpass >> 16) & 0xff;
							int newGreenVal = (valuesHochpass >>  8) & 0xff;
							int newBlueVal  =  valuesHochpass        & 0xff;	
							
							rn = rn - newRedVal/9;
							gn = gn - newGreenVal/9;
							bn = bn - newBlueVal/9;
						}
						
						// Bearbeiten Sie zunächst alle Bildpunkte ohne die Randpixel. Wenn dies funktioniert,
						// fügen Sie eine Randbehandlung hinzu, so dass alle Pixel gefiltert werden können. 
						rn = correctOverflow(rn);
						gn = correctOverflow(gn);
						bn = correctOverflow(bn);
						
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
					}
				}
			}
			
			

	} // CustomWindow inner class
		// Reusing the overflow method from previous exercises
		public int correctOverflow(int value) {
	    	int temp = value;
			if (value > 255) { temp = 255; }
			else if (value < 0) { temp = 0; }
			return temp;
	    }
	
	}	
}