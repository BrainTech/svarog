/* PaletteTest.java created 2008-03-06
 * 
 */

package org.signalml.test;

import java.awt.Color;

/** PaletteTest
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PaletteTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int[] arr = new int[256];
		
		MakePalette(arr, false);
		Color color;
		
		for( int i=0; i<arr.length; i++ ) {
			color = new Color( arr[i], false );
			System.out.println( "Palette at [" + i + "] color [" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "]" );
			
		}
		
	}

	
	 public final static int RGBToInteger(int red,int green,int blue) {
		  return 0xff000000|(red<<16)|(green<<8)|blue;
	  }

	  private static double SQR(double x) {
		return x*x;
	  }

	  public final static void MakePalette(int paleta[], boolean GrayScale) {
		double hh=255.0,ww=1.0F/(74.0*75.0);
		int i;

		if(!GrayScale)
		  for(i=1 ; i<=255 ; i++) {
		int r=(int)(hh*Math.exp(-ww*SQR(i-64.0))),
			g=(int)(hh*Math.exp(-ww*SQR(i-128.0))),
			b=(int)(hh*Math.exp(-ww*SQR(i-192.0)));
		
		paleta[256-i]=RGBToInteger(r,g,b);
		System.out.println( "Cr Palette at [" + (256-i) + "] color [" + r + "," + g + "," + b + "]" );
		  } else for(i=1 ; i<255 ; i++) {
		int itmp=255-i;
		paleta[i]=RGBToInteger(itmp,itmp,itmp);
		  }

		paleta[255]=RGBToInteger(0,0,0);
		paleta[0]=RGBToInteger(255,255,255);
	  } 	
}
