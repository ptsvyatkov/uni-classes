package uebung3;

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
public class GLDM_U3_S0559632 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Rot-Kanal", "Negativ", "Graustufen", "Binaerbild-Schwarz-Weiss", 
			"Binaerbild-HorizFehlerdiff", "Sepia", "sechsFarbenBild"};


	public static void main(String args[]) {
		
		IJ.open("C:\\about uni\\3sem\\GDM\\ue3\\bear.jpg");
		//IJ.open("/users/barthel/applications/ImageJ/_images/orchid.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GLDM_U3_S0559632 pw = new GLDM_U3_S0559632();
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
			
			if (method.equals("Rot-Kanal")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						//int g = (argb >>  8) & 0xff;
						//int b =  argb        & 0xff;

						int rn = r;
						int gn = 0;
						int bn = 0;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			/*
			 * This one was also explained in Prof. Barthel's podcast ( number 9 - Digitale Bilder II ) (after grayscale)
			 * I also found this video to be veeery very helpful: https://www.youtube.com/watch?v=rLLIJN57iHI
			 */
			if(method.equals("Negativ")) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 
						
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						// reversing the colors
						int rn = 255 - r;
						int gn = 255 - g;
						int bn = 255 - b;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
				
			}
			
			/* Explained in Prof. Barthel's video podcast 9
			 * also
			 * There was a discussion going with other classmates about this exercise on discord.
			 * https://stackoverflow.com/questions/21899824/java-convert-a-greyscale-and-sepia-version-of-an-image-with-bufferedimage
			 *
			 * This one has a lot of useful code, but it solves it using alpha channel for opacity (still interesting)
			 * https://rosettacode.org/wiki/Grayscale_image#Java
			 */
			if (method.equals("Graustufen")) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						int grayscale = (r+g+b)/3;
						
						int rn = grayscale;
						int gn = grayscale;
						int bn = grayscale;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			
			/* I was skeptical about this, but it indeed seems to work and transform it to a binary image
			 * http://www.informatics4kids.de/index.php/java/java-bildverarbeitung/86-farbbild-graubild-binaerbild
			 */
			if (method.equals("Binaerbild-Schwarz-Weiss")) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						int binaryValue;
						// < 150 didn't work well, so I tried different values, between 105-125 is pretty good
						if (g < 125) {
							binaryValue = 0;
						}
						else {
							binaryValue = 255;
						}

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (binaryValue<<16) | (binaryValue<<8) | binaryValue;
					}
				}
			}
			
			/*
			 * https://www.youtube.com/watch?v=ico4fJfohMQ
			 * https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering  - this appears to be too complicated...
			 * We discussed this exercise with other students, it seemed to be difficult to solve
			 * In the end someone hinted at a solution
			 */
			if (method.equals("Binaerbild-HorizFehlerdiff")) {
				int cap = 255*3;
  			  int errorDiffusion = 0;
	    	  for (int y = 0; y < height; y++) {
	    		  for (int x = 0; x < width; x++) {
	    			  
	    			  
	    			  int pos = y*width + x;
	    			  int argb = origPixels[pos];
	    			  
	    			  int r = (argb >> 16) & 0xff;
	    	          int g = (argb >>  8) & 0xff;
	    	          int b =  argb        & 0xff;
	    	          
	    	          // setting everything to white
	    	          int rn = 255;
	    	          int gn = 255;
	    	          int bn = 255;
	    	          
	    	          //wenn werte über 255 pixel auf weiß, sonst normal grau
	    	          // if the values are going above 255, pixel gets set to white
	    	          // otherwise gray
	    	          
	    	          if ((r + g + b + errorDiffusion) < cap) { 
	    	        	  rn = 0;
	    	        	  gn = 0;
	    	        	  bn = 0;
	    	          } 
	    	          
	    	          // I tried to ask the students that helped with this exercise, what exactly the 
	    	          // lines of code do, but didn't get much information onto this
	    	          // I read the discussion on moodle and the wikipedia page on error diffusion to understand it
	    	          errorDiffusion = ((r + g + b + errorDiffusion) - (rn+gn+bn));
	    	          pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
	    	          }
	    		  errorDiffusion = 0;
	    		  }
				
			}
			
			
			/* https://stackoverflow.com/questions/21899824/java-convert-a-greyscale-and-sepia-version-of-an-image-with-bufferedimage
			* https://www.youtube.com/watch?v=Q5Xbj3jlMsc really helpful again
			*
			* I found this discussion in google to be the best example and followed it - worked great first try!
			*https://groups.google.com/forum/#!topic/comp.lang.java.programmer/nSCnLECxGdA
			*/
			if (method.equals("Sepia")) {
				// tried this out with different values, between 20-30 it looks good
				int sepiaDepth = 20;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						int greyScale = (r+g+b)/3;
						r = greyScale;
						g = greyScale;
						b = greyScale;
						
						int red = r + (sepiaDepth * 2);
					    int green = g + sepiaDepth;	 
						int blue = b; 
						
						red = correctOverflow(red);
						green = correctOverflow(green);
						blue = correctOverflow(b);
						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (red<<16) | (green<<8) | blue;
					}
				}
			}
			
			/*
			 * I had some ideas about this one, someone suggested to use the gray scale formula (Gr = (r+g+b)/3 )
			 * and when Gr is less than a certain value ( e.g Gr < 50 ) set to color 1, when Gr < 100 then set to 
			 * color 2 and so on.. Someone else hinted this could be done just by using the r,g,b values too
			 */
			if (method.equals("sechsFarbenBild")) {
				int rn, gn, bn;
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 
						
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						rn = 0;
						gn = 0;
						bn = 0;
						
						// Color 1: very dark gray.. almost black ( #323232 )
			            if (r <= 50 || r > 50 && r <= 60 && g < 70) {
			            	rn = 50;
							gn = 50;
						    bn = 50;
			              }
			            // Color 2: blue  ( #3C7DC3 )  ((tried playing around with this one, but was getting many
			            // different results, it doesn't look too bad like that
						else if (((r > 50 && r <= 60) && g > 90) || 
								((r > 60 && r <= 90) && b > 90)) {
								rn = 0;
								gn = 125;
								bn = 195;
						}
			            // Color 3: some shade of brown ( #735A22 )
						else if (r > 67 && r <= 100) {
								rn = 115;
								gn = 90;
								bn = 34;
						} 
			            // It was interesting to play around with the brown and sienna colors
			            // Color 4: sienna (#A0522D)
						else if (r > 100 && r <= 133) {
								rn = 160;
								gn = 82;
								bn = 45;
						}
			            // Color 5: inbetween darkgray(169,169,169 #A9A9A9) and gray(128,128,128 #808080)
						else if (r > 133 && r <= 176) {
								rn = 148;
								gn = 148;
								bn = 148;
						}
			            // Color 6: gainsboro (#DCDCDC)
						else if (r > 176 && r <= 255) {
								rn = 180;
								gn = 180;
								bn = 180;
						}
						
						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
		}


	} // CustomWindow inner class
	
	public static int correctOverflow(int value) {
    	int temp = value;
		if (value > 255) { temp = 255; }
		else if (value < 0) { temp = 0; }
		return temp;
    }
} 