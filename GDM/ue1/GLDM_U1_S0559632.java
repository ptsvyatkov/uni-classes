package uebung1;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GLDM_U1_S0559632 implements PlugIn {
	
	final static String[] choices = {
		"Schwarzes Bild",
		"Gelbes Bild", 
		"Belgische Fahne",
		"Schwarz/Weiss Verlauf",
		"Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf",
		"USA Fahne",
		"Japanische Fahne"
	};
	
	private String choice;
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		GLDM_U1_S0559632 imageGeneration = new GLDM_U1_S0559632();
		imageGeneration.run("");
	}
	
	public void run(String arg) {
		
		int width  = 566;  // Breite
		int height = 400;  // Hoehe
		
		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();
		
		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[])ip.getPixels();
		
		dialog();
		
		////////////////////////////////////////////////////////////////
		// Hier bitte Ihre Aenderungen / Erweiterungen
		
		// This one was already done
		if ( choice.equals("Schwarzes Bild") ) {
			generateBlackImage(width, height, pixels);
		}
		
		// Shown in the video explanation
		if ( choice.equals("Gelbes Bild") ) {
			generateYellowImage(width, height, pixels);
		}
		
		if ( choice.equals("Belgische Fahne") ) {
			makeBelgianFlag(width, height, pixels);
		}
		
		if ( choice.equals("Schwarz/Weiss Verlauf") ) {
			blackWhiteGradient(width, height, pixels);
		}
		
		if ( choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf") ) {
			mixedHorizontalVerticalGradient(width, height, pixels);
		}
		
		if ( choice.equals("USA Fahne") ) {
			makeAmericanFlag(width, height, pixels);
		}
		
		if ( choice.equals("Japanische Fahne") ) {
			makeJapaneseFlag(width, height, pixels);
		}
		
		////////////////////////////////////////////////////////////////////
		
		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}
	
	// "Schwarzes Bild" was already here as an "example"	
	private void generateBlackImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				
				int r = 0;
				int g = 0;
				int b = 0;
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	
	//This one was shown as a how-to in the explanation video
	private void generateYellowImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 255;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}
	
	/* To make this one, I need to divide the width into three equal parts
	 which means i can make use of width/3 ( into thirds ) and paint them accordingly.
	 Then i just use if statements to paint the first third of the width black (x<=width/3), 
	 the second one yellow and the last red.
	 */
	private void makeBelgianFlag(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r;
				int g;
				int b;
				// using the variable thirdOfWidth to represent a third of the width in the image
				int thirdOfWidth = width/3;
				//the first third of the width
				if (x <= thirdOfWidth) {
					// rgb(0,0,0) for black
					r = 0;
					g = 0;
					b = 0;	
				} 
				// second third of the width
				// which has to be after the first third and before the last third
				// using thirdOfWidth*2 to specify that it's the middle one
				else if(x > thirdOfWidth && x <= thirdOfWidth*2) {
					// rgb(255,255,0) for yellow
					r = 255;
					g = 255;
					b = 0;
				} 
				// finally the last third
				else {
					// rgb(255,0,0) for red
					r = 255;
					g = 0;
					b = 0;
				}
				// Werte zurueckschreiben
				// im not entirely sure what "zurueckschreiben" means, but we just
				// use the bitshifting operator for the rgb values values
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}
	
	// As seen in Professor Barthel's lectures, here we can use just one variable and assign it
	// when bitshifting instead of three for r,g,b.
	private void blackWhiteGradient(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				
				// moving horizontally step by step and slowly passing from black to white
				int gradient = x*255/(width-1);
				
				// using the variable for all three positions
				pixels[pos] = 0xFF000000 | (gradient << 16) | (gradient << 8) | gradient;
			}
		}
	}
	
	// This one looked complicated at first, but when i thought about it.. the first part is just
	// like the previous exercise! Like going from black to white, but in this case it's from
	// black to red horizontally. Just need to think about the vertical part.
	private void mixedHorizontalVerticalGradient(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int r, g, b;
				int pos = y * width + x; // Arrayposition bestimmen
				
				// similarly to previous task using (width -1) to move horizontally, but just the value for red
				r = 255 * x / (width - 1);
				
				g = 0;
				
				// Since we use (width - 1) for horizontal part, then the vertical part 
				// must be (height - 1). Tried out - worked!
				// vertically moving the value for blue
				b = 255 * y / (height - 1);

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}
	
	// The american flag has 13 stripes - 7 red and 6 white. We use a height of 400 so 400/13 = 31.
	//making a white background first
	private void makeAmericanFlag(int width, int height, int[] pixels) {
		//Schleife ueber die y-Werte (wei�)
				for (int y=0; y<height; y++) {
					//Schleife ueber die x-Werte 
					for (int x=0; x<width; x++) { //x=height/2
							int pos = y*width + x; //Arrayposition bestimmen
						int r,g,b;
						r = 255;
						g = 255;
						b = 255;
							
						//Werte zurueckschreiben
						pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
					}
				}
				// every 31 pixels ( or how should i call it ) when going vertically, we should change the stripes
				// so first 31 are red, then 31 are left white, then again 31 are red and so on..
				// there are 13 stripes - 7 red, 6 white ( 400/13 = 31 )
				boolean skip = true;
				int swap = 31; // 400/13 = 31
				//Schleife ueber die y-Werte (rote Streifen)
				for (int y=0; y<height; y++) { 
					if(y > swap) {
						skip = !skip;
						swap += 31;
					}
					//Schleife ueber die x-Werte
					// we start from 0 so first 31 "lines of pixels" will be red
					// setting the r,g,b here to paint it in red
					if(skip == true) {
						for (int x=0; x<width; x++) { 
							int pos = y*width + x; //Arrayposition bestimmen
							int r,g,b;
							r = 200;
							g = 50; 
							b = 50;
							
							//Werte zurueckschreiben
							pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
						}
					}
				}
		// Finally, we need the blue rectangle in the top left corner ( no stars ).
		// The blue part rectangle covers 4 red stripes so that's around half of the height, need to adjust.
		// Schleife ueber die y-Werte
		int topLeftBlueRect = (height / 2) + 18; // looks like with +18 the blue doesn't go over the stripes
		for (int y = 0; y < topLeftBlueRect; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < topLeftBlueRect; x++) {
				int pos = y * width + x; // Arrayposition bestimmen

				int r = 50;
				int g = 50;
				int b = 120;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}

	}
	
	/*
	 * Thanks to some students from the class for giving tips to use the Pythagorean theorem 
	 * to be able to make the circle, using deltaX, deltaY and radius. We were able to have a nice discussion
	 * on discord about how it should work exactly.
	 * Everything outside the radius should be white, the circle is red.
	 */
	private void makeJapaneseFlag(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen
				int r, g, b;
			//	Satz des Pythagoras zur Bestimung Entfernung vom Punkt (X/Y) zur Mitte
				// Pythagorean theorem to determine the distance between point X/Y and the middle
				
				//adjust the circle vertically
				int deltaY = y - (height / 2);
				
				//adjust the circle horizontally
				int deltaX = x - (width / 2);
				
				// calculate the distance using the formula
				int distance = (deltaX * deltaX) + (deltaY * deltaY);
				
				// if r^2 is bigger than the distance, paint it red
				// some classmates were nice and helpful and showed others how to deal with this one
				int radius = 105;
				if ((radius * radius) > distance) {
					r = 255;
					g = 0;
					b = 0;
				} else { // the background is just white	
					r = 255;
					g = 255;
					b = 255;
				}
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	

	
	
	
	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");
		
		gd.addChoice("Bildtyp", choices, choices[0]);
		
		
		gd.showDialog();	// generiere Eingabefenster
		
		choice = gd.getNextChoice(); // Auswahl uebernehmen
		
		if (gd.wasCanceled())
			System.exit(0);
	}
}
