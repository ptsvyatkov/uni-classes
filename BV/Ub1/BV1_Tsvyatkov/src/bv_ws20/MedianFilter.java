package bv_ws20;

import java.util.Arrays;

public class MedianFilter implements Filter {
	
	private int kernelSize;
	private RasterImage inputImage;
	private RasterImage outputImage;
	
	@Override
	public void setSourceImage(RasterImage sourceImage) {
		// TODO Auto-generated method stub
		inputImage = sourceImage;
	}

	@Override
	public void setDestinationImage(RasterImage destinationImage) {
		// TODO Auto-generated method stub
		outputImage = destinationImage;
	}

	@Override
	public void setKernelWidth(int kernelWidth) {
		// TODO Auto-generated method stub
		this.kernelSize = kernelWidth;
	}

	@Override
	public void setKernelHeight(int kernelHeight) {
		// TODO Auto-generated method stub
		this.kernelSize = kernelHeight;
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		// I used the book for reference to solve this (Median filter 
		// I was also researching on the internet to find some information about median filter implementation and found
		// median filter pseudocode here: https://en.wikipedia.org/wiki/Median_filter
		// Found Convolution pseudocode here: https://www.cs.auckland.ac.nz/courses/compsci373s1c/PatricesLectures/Image%20Filtering.pdf
		
		// additional materials also here
		// https://web.cs.wpi.edu/~emmanuel/courses/cs545/S14/slides/lecture04.pdf
		// https://rosettacode.org/wiki/Median_filter#Kotlin ( similar to java)
		
			// given size of the kernel, calculate width and height of the kernel
	        int kernelW = kernelSize / 2;
	        int kernelH = kernelSize / 2;
	        
	        int height = inputImage.height;
	        int width = inputImage.width;
	        
	        int[] kernel = new int[kernelSize*kernelSize]; // size is same number for 1x1,3x3,5x5,7x7,9x9 kernel
	        
	        //looping over the image in scanline order
	        for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	               int pos = y*width + x; // array position
	               
	               int i = 0; // will serve as a count variable to iterate over the array
	               
	               // kernel (window) loop to apply the filter
	               // if the kernel is for example 3x3, kernelW and kernelH are 1 and it loops from -1 to 1 --> 3 times 
	        	   // (if kernel is 5x5, kernelH/W are 2, loops from -2 to 2 --> 5 times ) etc.
	               for(int j = -kernelW; j <= kernelW ; j++) {
	            	   for(int k = -kernelH; k <= kernelH; k++) {
	            		   
	            		   // checking if the mask(kernel) goes outside of the image bounds
	            		   // I talked with Paul from the group, who helped me with 
	            		   // (Randbehandlung) handling the edges 
	            		   int edgeY = j;
	            		   int edgeX = k;
	            		   if ( (y+j) < 0 || (y+j) >= height) edgeY = 0;
	            		   if ( (x+k) < 0 || (x+k) >= width ) edgeX = 0;
	            		   
	            		   // I was at this part on Monday night. I think i need to check what's the position of the kernel and
	            		   // save the pixel values of the input image on that exact position of the kernel..
	            		   
	            		   //I was able to finish the rest with the extra time given.
	            		   // On Tuesday I talked with Paul from our group about this part and he explained to me that I need to 
	            		   //  - write the value into the array in the inner for loop, the value is actually calculated exactly
	            		   // the same way i calculated "pos" earlier, but only with additionally added j and k from the
	            		   // kernel loops
	            		   //  - use the new kernel position as index of the input image pixels and store it in a new variable
	            		   //  - and also a "counter" for the array which i already assigned as "i"
	            		  
	            		   int yPos = y + edgeY;
	            		   int xPos = x + edgeX;
	            		   int kernelPos = yPos*width + xPos; // kernel array position serves as an index for the input pixels
	                        
	            		   // iterating over kernel with i++, using kernelPos as the array position in the inputImage pixels,
	            		   // extracting the color value ( since we are doing grayscale we have only one) and storing into kernel array
	                       kernel[i++] = (inputImage.argb[kernelPos]>> 16) & 0xFF;
	            	   }
	               }
	               // sort the kernel
	               Arrays.sort(kernel);
	               // the median value is the value that sits right in the middle
	               int medianValue = kernel[kernel.length/2];
	               
	               outputImage.argb[pos] = (0xFF<<24) | (medianValue<<16) | (medianValue<<8) | medianValue;
	            }
	        }
	    }
	}