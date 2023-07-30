package bv_ws20;

public class MinFilter implements Filter {
	
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
		// I used the book for reference to solve this (p.116 Non-linear filters)
		// I was also researching on the internet to find some information about maximum filter implementation and found
		// https://de.wikipedia.org/wiki/Rangordnungsfilter
		// Found Convolution pseudocode here: https://www.cs.auckland.ac.nz/courses/compsci373s1c/PatricesLectures/Image%20Filtering.pdf
		
			// given size of the kernel, calculate width and height of the kernel
	        int kernelW = kernelSize / 2;
	        int kernelH = kernelSize / 2;
	        
	        int height = inputImage.height;
	        int width = inputImage.width;
	        
	        int maxValue = 255; // the variable to replace with the minimum value in the pixel neighbourhood
	        //looping over the image in scanline order
	        for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++) {
	               int pos = y*width + x; // array position
	               
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
	            		  
	            		   int yPos = y + edgeY;
	            		   int xPos = x + edgeX;
	            		   int kernelPos = yPos*width + xPos; // kernel array position serves as an index for the input pixels

	            		   // if the value of the pixel at the position is LESSER than 'maxValue'
	            		   if ( ((inputImage.argb[kernelPos] >> 16) & 0xFF) < maxValue) {
	            			 //then maxValue is updated(assigned) with this LESSER value
	            			   maxValue = ((inputImage.argb[kernelPos] >> 16) & 0xFF);
	            		   }
	            	   }
	               }
	               outputImage.argb[pos] = (0xFF<<24) | (maxValue<<16) | (maxValue<<8) | maxValue;
	               maxValue = 255; // need to update 'maxValue' to 255 again after writing back the values or the image is black
	            }
	        }
	    }
	}