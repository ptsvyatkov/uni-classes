package uebung4;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;


public class GLDM_U4_S0559632 implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = {"Wischen", "Wisch Blende", "Weiche Blende", "Ineinanderkopieren", "Schieb Blende", "Chroma Key", "Eigene"};

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB+STACK_REQUIRED;
	}
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		IJ.open("C:\\about uni\\3sem\\GDM\\ue4\\StackB.zip");
		
		GLDM_U4_S0559632 sd = new GLDM_U4_S0559632();
		sd.imp = IJ.getImage();
		ImageProcessor B_ip = sd.imp.getProcessor();
		sd.run(B_ip);
	}

	public void run(ImageProcessor B_ip) {
		// Film B wird uebergeben
		ImageStack stack_B = imp.getStack();
		
		int length = stack_B.getSize();
		int width  = B_ip.getWidth();
		int height = B_ip.getHeight();
		
		// ermoeglicht das Laden eines Bildes / Films
		Opener o = new Opener();
		//OpenDialog od_A = new OpenDialog("Auswählen des 2. Filmes ...",  "");
				
		// Film A wird dazugeladen
		//String dateiA = od_A.getFileName();
		//if (dateiA == null) return; // Abbruch
		//String pfadA = od_A.getDirectory();
		ImagePlus A = o.openImage("C:\\about uni\\3sem\\GDM\\ue4\\StackA.zip");
		if (A == null) return; // Abbruch

		ImageProcessor A_ip = A.getProcessor();
		ImageStack stack_A  = A.getStack();

		if (A_ip.getWidth() != width || A_ip.getHeight() != height)
		{
			IJ.showMessage("Fehler", "Bildgrößen passen nicht zusammen");
			return;
		}
		
		// Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
		length = Math.min(length,stack_A.getSize());

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
		ImageStack stack_Erg  = Erg.getStack();

		// Dialog fuer Auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("Überlagerung");
		gd.addChoice("Methode",choices,"");
		gd.showDialog();

		int methode = 0;		
		String s = gd.getNextChoice();
		if (s.equals("Wischen")) methode = 1;
		if (s.equals("Wisch Blende")) methode = 2;
		if (s.equals("Weiche Blende")) methode = 3;
		if (s.equals("Ineinanderkopieren")) methode = 4;
		if (s.equals("Schieb Blende")) methode = 5;
		if (s.equals("Chroma Key")) methode = 6;
		if (s.equals("Eigene")) methode = 7;
		
		

		// Arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_A;
		int[] pixels_Erg;

		// Schleife ueber alle Bilder
		for (int z=1; z<=length; z++)  // z is pixels (95)
		{
			pixels_B   = (int[]) stack_B.getPixels(z);
			pixels_A   = (int[]) stack_A.getPixels(z);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			int pos = 0;
			for (int y=0; y<height; y++)
				for (int x=0; x<width; x++, pos++)
				{
					// Stack A values - Hintergrundbild
					int cA = pixels_A[pos];
					int rA = (cA & 0xff0000) >> 16;
					int gA = (cA & 0x00ff00) >> 8;
					int bA = (cA & 0x0000ff);
					
					// Stack B values - Vordergrundbild
					int cB = pixels_B[pos];
					int rB = (cB & 0xff0000) >> 16;
					int gB = (cB & 0x00ff00) >> 8;
					int bB = (cB & 0x0000ff);

					// Wischen - already done
					if (methode == 1)
					{
					if (x+1 > (z-1)*(double)width/(length-1))
						pixels_Erg[pos] = pixels_B[pos];
					else
						pixels_Erg[pos] = pixels_A[pos];
					}
					
					/* 
					 * Wisch-Blende
					 * Change the direction from horizontal to vertical
					 * instead of "if (x " --> "if (y"
					 */
					if (methode == 2)
					{
						if (y+1 > (z-1)*(double)width/(length-1))
							pixels_Erg[pos] = pixels_B[pos];
						else
							pixels_Erg[pos] = pixels_A[pos];
					}
					
					/*
					 * Weiche-Blende 
					 * Change the transparency of the overlayed film (Cross Dissolve)
					 * Explained in Video 11 (Prof. Barthel) 
					 * around 35:30 (Normal Bildüberlagerung)
					 *  and 1:18:50 (Überblendung: Weiche Blende)
					 * pixels_Erg[pos] -- bitshifting of pixels' position already given
					 */
					if (methode == 3) {			
						
						int a = 255*(z-1)/(length-1);														
						int r = (a* rA + (255 - a)* rB )/255;
						int g = (a* gA + (255 - a)* gB )/255;
						int b = (a* bA + (255 - a)* bB )/255;
							
						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}

					/*
					 * Ineinanderkopieren (Overlay)
					 * Information about it in Video 11 (Prof.Barthel)
					 * around 1:08:40 ( Ineinanderkopieren, Overlay)
					 * rErgebnis = rV*rH/128 if rH<=128			(rV= rVordergrundsbild, rH=rHintergrundsbild)
					 * rErgebnis = 255 - ((255 - rV) * (255 - rH) / 128)
					 * (whitespace between * and / for clarity)
					 */
					if (methode == 4) {
						
					int r,g,b;
					
					// if red value of the background image is below or equal to 128
					if (rA <= 128) {
						r = (rB * rA) / 128; 
					} 
					// the else block should be about "Vordergrundsbild", there was a discussion about it in the Discord group
						else {	
						r = 255 - ((255 - rB) * (255 - rA) / 128);
					}
					// In slide 04 (Bildmanipulation) and in video it says for g(green) and b(blue) it's analogous
					// if green value of the background image is below or equal to 128
					if(gA <= 128) {
						g = (gB * gA) / 128; 
					} 
						else {
						g = 255 - ((255 - gB) * (255 - gA) / 128);
					}
					
					// if blue value of the background image is below or equal to 128
					if(bA <= 128) {
						b = (bB * bA) / 128; 
					} 
						else {
						b = 255 - ((255 - bB) * (255 - bA) / 128);
					}	
					
					pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
				   }
					
					// Schieb-Blende
					/* I saw that many people had problems solving this one, so there was a long discussion on it
					 * between the students. I also had trouble with it, so I was listening to other students' explanations.
					 * 
					 * Someone pointed that we should see how the current pixel lies relative to its own position in the picture 
					 * and this then depends on the "cut" between the videos.
					 */
					if (methode == 5) {		
						
						int schieb = (int) ((z-1) * (double)width/(length-1));
						// Just like Wischen/Wisch-Blende "(x+1 > (z-1) * (double)width / (length-1))"
							if (x+1 > schieb) {	
							// Some students who were able to get the Schieb-Blende correctly, were kind enough to
							// help others. This one seemed to be a difficult task.
							int a = pos - schieb; 
							if(a > pixels_A.length-1) {
								a = pixels_A.length-1;
							} 
							else if(a < 0) { 	// not allowing negative values
								a = 0;
							} 
							pixels_Erg[pos] = pixels_A[a];
							}							
							else {
							int a = y * width - schieb + x;
							if( a > pixels_B.length-1) {
								a = pixels_B.length-1;
							} else if(a < 0) { 		// not allowing negative values
								a = 0;
							}
							pixels_Erg[pos] = pixels_B[a];
							}					
					}
					
				    //Chroma Keying
				    if(methode == 6) {
				    	/*
				    	 * Take a color range (last slide in image manipulation 01) and just say, 
				    	 * if this pixel is in this area you use movie B, 
				    	 * if it is not in this area (i.e. not orange) you use movie A.
				    	 */
				    	//Values for the orange color in the background video which should be removed
						int backR = 215; 
						int backG = 155;
						int backB = 55;
						
						// Some explanations about Keying in Video 11 (Barthel) around 1:30:00
						// Similar formula in Folie 04 (Bildmanipulation1)
						double radius = Math.sqrt((backR - rA) * (backR - rA) + (backG - gA) * (backG - gA) + (backB - bA) * (backB - bA));
						
						if(radius < 150) {
							pixels_Erg[pos] = pixels_B[pos];
						}else {
							pixels_Erg[pos] = pixels_A[pos];
						}					
					}
				    
				    /*
				     * Eigene Überblendung
				     */
				    if (methode == 7) {
				    	int a = 255*(z-1)/(length-1);														
						int r = (a* rA + (255 - a)* rB )/255;
						int g = (a* gA + (255 - a)* gB )/255;
						int b = (a* bA + (255 - a)* bB )/255;
				    	if (rA <= 128) {
							r = (rB * rA) / 64; 
						}
				    	if(gA <= 128) {
							g = (gB * gA) / 64; 
						} 
				    	if(bA <= 128) {
							b = (gB * gA) / 64; 
						} 
				    	
				    	pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
				    }
				}
		}

		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();

	}

}

