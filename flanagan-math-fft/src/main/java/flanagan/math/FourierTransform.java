/*
*   Fourier Transform
*
*   This class contains the method for performing a
*   Fast Fourier Transform (FFT) and associated methods
*   e.g. for estimation of a power spectrum, for windowing data,
*   obtaining a time-frequency representation.
*   Basic FFT method is adapted from the Numerical Recipes
*   methods written in the C language:
*   Numerical Recipes in C, The Art of Scientific Computing,
*   W.H. Press, S.A. Teukolsky, W.T. Vetterling & B.P. Flannery,
*   Cambridge University Press, 2nd Edition (1992) pp 496 - 558.
*   (http://www.nr.com/).
*
*   AUTHOR: Dr Michael Thomas Flanagan
*   DATE:   20 December 2003
*   UPDATES: 26 July 2004, 31 August 2004, 15 June 2005, 27 January 2006
*   UPDATES: 18 February 2006  method correlation correction (thanks to Daniel Mader, Universt�t Freiburg -- IMTEK)
*   UPDATES: 11 February 2008  number of power points corrected
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web page:
*   http://www.ee.ucl.ac.uk/~mflanaga/java/FourierTranasform.html
*   http://www.ee.ucl.ac.uk/~mflanaga/java/
*
*
*   Copyright (c) July 2004, January 2006  Michael Thomas Flanagan
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/

package flanagan.math;

import java.awt.Canvas;

import flanagan.complex.Complex;

public class FourierTransform extends Canvas{

	private static final long serialVersionUID = 1L;


	private Complex[] complexData = null;   // array to hold the input data as a set of Complex numbers
	private Complex[] complexCorr = null;   // corresponding array to hold the data to be correlated with first data set
	private boolean complexDataSet = false; // if true - the complex data input array has been filled, if false - it has not.
    private int originalDataLength = 0;     // original data length value; the working data length may be altered by deletion or padding
    private int fftDataLength = 0;          // working data length - usually the smallest power of two that is either equal to originalDataLength or larger than originalDataLength
	private boolean dataAltered = false;    // set to true if originalDataLength altered, e.g. by point deletion or padding.

	private double[] fftData = null;        // array to hold a data set of complex numbers arranged as alternating
	                                        // real and imaginary parts, e.g. real_0 imag_0, real_1 imag_1, for the fast Fourier Transform method
	private double[] fftCorr = null;        // corresponding array to hold the  data to be correlated with first data set
	private boolean fftDataSet = false;     // if true - the fftData array has been filled, if false - it has not.

    private double[] fftDataWindow = null;  // array holding fftData array elements multiplied by the windowing weights
	private double[] fftCorrWindow = null;  // corresponding array to hold the data to be correlated with first data set

	private int windowOption = 0;	        //	Window Option
	                                        //      = 0; no windowing applied (default) - equivalent to option = 1
									        //		= 1; Rectangular (square, box-car)
									        //		= 2; Bartlett (triangular)
									        //		= 3; Welch
									        //      = 4; Hann (Hanning)
									        //      = 5; Hamming
									        //      = 6; Kaiser
									        //      = 7; Gaussian
	// all window names
	private double kaiserAlpha = 2.0D;      //  Kaiser window constant, alpha
	private double gaussianAlpha = 2.5D;    //  Gaussian window constant, alpha
	private double[] weights = null;        //  windowing weights
    @SuppressWarnings("unused")
	private boolean windowSet = false;      //  = true when a windowing option has been chosen, otherwise = false
    private boolean windowApplied = false;  //  = true when data has been multiplied by windowing weights, otherwise = false
	private double sumOfSquaredWeights = 0.0D;     //  Sum of the windowing weights

	private Complex[] transformedDataComplex = null;  // transformed data set of Complex numbers
	private double[] transformedDataFft = null; // transformed data set of double adjacent real and imaginary parts

    private double[][] powerSpectrumEstimate = null;  // first row - array to hold frequencies
                                                      // second row - array to hold estimated power density (psd) spectrum
    private boolean powSpecDone = false;    // = false - PowerSpectrum has not been called
                                            // = true  - PowerSpectrum has been called
    private int psdNumberOfPoints = 0;      // Number of points in the estimated power spectrum

	private int segmentNumber = 1;	        //	Number of segments into which the data has been split
	private int segmentLength = 0;	        //	Number of of data points in a segment
	private boolean overlap = false;	    //	Data segment overlap option
									        //	= true; overlap by half segment length - smallest spectral variance per data point
									        //		good where data already recorded and data reduction is after the process
									        //	= false;  no overlap - smallest spectral variance per conputer operation
	    							        //		good for real time data collection where data reduction is computer limited
	private boolean segNumSet = false;      //  true if segment number has been set
    private boolean segLenSet = false;      //  true of segment length has been set

    private double deltaT = 1.0D;           // Sampling period (needed only for true graphical output)
    private boolean deltaTset = false;      // true if sampling period has been set

    private double[][] correlationArray = null; // first row - array to hold time lags
                                                // second row - correlation between fftDataWindow and fftCorrWindow
    private boolean correlateDone = false;      // = false - correlation has not been called
                                                // = true  - correlation has been called

    private int numberOfWarnings = 9;           // Number of warnings
    private boolean[] warning = new boolean[numberOfWarnings];  // warnings - if warning[x] = true warningText[x] is printed

    private int plotLineOption = 0;             // PlotPowerSpectrum line option
                                                // = 0 points linked by straight line [default option]
                                                // = 1 cubic spline interpolation
                                                // = 2 no line - only points

    private int plotPointOption = 0;            // PlotPowerSpectrum point option
                                                // = 0 no point symbols [default option]
                                                // = 1 filled circles

    private double[][] timeFrequency = null;    // matrix of time against frequency mean square powers from shoert time FT
                                                //  first row = blank cell followed by time vector
                                                //  first column = blank cell followed by frequency vector
                                                //  each cell is then the mean square amplitude at that frequency and time
    private boolean shortTimeDone = false;      // = true when short time Fourier Transform has been performed
    private int numShortFreq = 0;               // number of frequency points in short time Fourier transform
    private int numShortTimes = 0;              // number of time points in short time Fourier transform

	// constructors
	// No initialisation of the data variables
	public FourierTransform(){
        for(int i=0; i<numberOfWarnings; i++) warning[i] = false;

	}

    // constuctor entering a data array of real numbers
	public FourierTransform(double[] realData){
	    this.originalDataLength = realData.length;
	    this.fftDataLength = FourierTransform.nextPowerOfTwo(this.originalDataLength);
	    this.complexData = Complex.oneDarray(this.fftDataLength);
	    for(int i=0; i<this.originalDataLength; i++){
	        this.complexData[i].setReal(realData[i]);
		    this.complexData[i].setImag(0.0D);
	    }
	    for(int i=this.originalDataLength; i<this.fftDataLength; i++)this.complexData[i].reset(0.0D, 0.0D);
	    this.complexDataSet = true;

	    this.fftData = new double[2*this.fftDataLength];
	    int j = 0;
	    for(int i=0; i<this.fftDataLength; i++){
	        this.fftData[j] = complexData[i].getReal();
	        j++;
	        this.fftData[j] = 0.0D;
	        j++;
	    }
	    this.fftDataSet = true;

	    this.fftDataWindow = new double[2*this.fftDataLength];
	    this.weights = new double[this.fftDataLength];
	    this.sumOfSquaredWeights = windowData(this.fftData, this.fftDataWindow, this.weights);

	    this.transformedDataFft = new double[2*this.fftDataLength];
	    this.transformedDataComplex = Complex.oneDarray(this.fftDataLength);
	    this.segmentLength = this.fftDataLength;

        for(int i=0; i<numberOfWarnings; i++) warning[i] = false;
	}

    // constuctor entering a data array of complex numbers
	public FourierTransform(Complex[] data){
	    this.originalDataLength = data.length;
	    this.fftDataLength = FourierTransform.nextPowerOfTwo(this.originalDataLength);
	    this.complexData = Complex.oneDarray(this.fftDataLength);
	    for(int i=0; i<this.originalDataLength; i++){
	        this.complexData[i] = data[i].copy();
	    }
	    for(int i=this.originalDataLength; i<this.fftDataLength; i++)this.complexData[i].reset(0.0D, 0.0D);
	    this.complexDataSet = true;

	    this.fftData = new double[2*this.fftDataLength];
	    int j = 0;
	    for(int i=0; i<this.fftDataLength; i++){
	        this.fftData[j] = complexData[i].getReal();
	        j++;
	        this.fftData[j] = complexData[i].getImag();
	        j++;
	    }
	    this.fftDataSet = true;

	    this.fftDataWindow = new double[2*this.fftDataLength];
	    this.weights = new double[this.fftDataLength];
	    this.sumOfSquaredWeights = windowData(this.fftData, this.fftDataWindow, this.weights);

	    this.transformedDataFft = new double[2*this.fftDataLength];
	    this.transformedDataComplex = Complex.oneDarray(this.fftDataLength);
	    this.segmentLength = this.fftDataLength;

        for(int i=0; i<numberOfWarnings; i++) warning[i] = false;
	}

    // Enter a data array of real numbers
    public void setData(double[] realData){
        this.originalDataLength = realData.length;
	    this.fftDataLength = FourierTransform.nextPowerOfTwo(this.originalDataLength);
	    this.complexData = Complex.oneDarray(this.fftDataLength);
	    for(int i=0; i<this.originalDataLength; i++){
	        this.complexData[i].setReal(realData[i]);
		    this.complexData[i].setImag(0.0D);
	    }
	    for(int i=this.originalDataLength; i<this.fftDataLength; i++)this.complexData[i].reset(0.0D, 0.0D);
	    this.complexDataSet = true;

	    this.fftData = new double[2*this.fftDataLength];
	    int j = 0;
	    for(int i=0; i<this.fftDataLength; i++){
	        this.fftData[j] = complexData[i].getReal();
	        j++;
	        this.fftData[j] = 0.0D;
	        j++;
	    }
	    this.fftDataSet = true;

	    this.fftDataWindow = new double[2*this.fftDataLength];
	    this.weights = new double[this.fftDataLength];
	    this.sumOfSquaredWeights = windowData(this.fftData, this.fftDataWindow, this.weights);

	    this.transformedDataFft = new double[2*this.fftDataLength];
	    this.transformedDataComplex = Complex.oneDarray(this.fftDataLength);

	    if(this.segNumSet){
	        this.setSegmentNumber(this.segmentNumber);
	    }
	    else{
	        if(this.segLenSet){
	            this.setSegmentLength(this.segmentLength);
	        }
	        else{
	            this.segmentLength = this.fftDataLength;
	        }
	    }
	}

    // Enter a data array of complex numbers
    public void setData(Complex[] data){
        this.originalDataLength = data.length;
	    this.fftDataLength = FourierTransform.nextPowerOfTwo(this.originalDataLength);
	    this.complexData = Complex.oneDarray(this.fftDataLength);
	    for(int i=0; i<this.originalDataLength; i++){
	        this.complexData[i] = data[i].copy();
	    }
	    for(int i=this.originalDataLength; i<this.fftDataLength; i++)this.complexData[i].reset(0.0D, 0.0D);
	    this.complexDataSet = true;

	    this.fftData = new double[2*this.fftDataLength];
	    int j = 0;
	    for(int i=0; i<this.fftDataLength; i++){
	        this.fftData[j] = complexData[i].getReal();
	        j++;
	        this.fftData[j] = complexData[i].getImag();
	        j++;
	    }
	    this.fftDataSet = true;

	    this.fftDataWindow = new double[2*this.fftDataLength];
	    this.weights = new double[this.fftDataLength];
	    this.sumOfSquaredWeights = windowData(this.fftData, this.fftDataWindow, this.weights);

	    this.transformedDataFft = new double[2*this.fftDataLength];
	    this.transformedDataComplex = Complex.oneDarray(this.fftDataLength);

	    if(this.segNumSet){
	        this.setSegmentNumber(this.segmentNumber);
	    }
	    else{
	        if(this.segLenSet){
	            this.setSegmentLength(this.segmentLength);
	        }
	        else{
	            this.segmentLength = this.fftDataLength;
	        }
	    }
	}

	// Enter a data array of adjacent alternating real and imaginary parts for fft method, fastFourierTransform
    public void setFftData(double[] fftdata){
        if(fftdata.length % 2 != 0)throw new IllegalArgumentException("data length must be an even number");

        this.originalDataLength = fftdata.length/2;
	    this.fftDataLength = FourierTransform.nextPowerOfTwo(this.originalDataLength);
	    this.fftData = new double[2*this.fftDataLength];
	    for(int i=0; i<2*this.originalDataLength; i++)this.fftData[i] = fftdata[i];
	    for(int i=2*this.originalDataLength; i<2*this.fftDataLength; i++)this.fftData[i] = 0.0D;
	    this.fftDataSet = true;

	    this.complexData = Complex.oneDarray(this.fftDataLength);
	    int j = -1;
	    for(int i=0; i<this.fftDataLength; i++){
	        this.complexData[i].setReal(this.fftData[++j]);
            this.complexData[i].setImag(this.fftData[++j]);
	    }
	    this.complexDataSet = true;

	    this.fftDataWindow = new double[2*this.fftDataLength];
	    this.weights = new double[this.fftDataLength];
	    this.sumOfSquaredWeights = windowData(this.fftData, this.fftDataWindow, this.weights);

	    this.transformedDataFft = new double[2*this.fftDataLength];
	    this.transformedDataComplex = Complex.oneDarray(this.fftDataLength);

	    if(this.segNumSet){
	        this.setSegmentNumber(this.segmentNumber);
	    }
	    else{
	        if(this.segLenSet){
	            this.setSegmentLength(this.segmentLength);
	        }
	        else{
	            this.segmentLength = this.fftDataLength;
	        }
	    }
	}

	// Get the input data array as Complex
    public Complex[] getComplexInputData(){
        if(!this.complexDataSet){
		    System.out.println("complex data set not entered or calculated - null returned");
		}
		return this.complexData;
	}

	// Get the input data array as adjacent real and imaginary pairs
    public double[] getAlternateInputData(){
        if(!this.fftDataSet){
		    System.out.println("fft data set not entered or calculted - null returned");
		}
	    return this.fftData;
	}

    // Get the windowed input data array as windowed adjacent real and imaginary pairs
    public double[] getAlternateWindowedInputData(){
        if(!this.fftDataSet){
		    System.out.println("fft data set not entered or calculted - null returned");
		}
		if(!this.fftDataSet){
		    System.out.println("fft data set not entered or calculted - null returned");
		}
		if(!this.windowApplied){
		    System.out.println("fft data set has not been multiplied by windowing weights");
		}
	    return this.fftDataWindow;
	}

	// get the original number of data points
	public int getOriginalDataLength(){
		return this.originalDataLength;
	}

	// get the actual number of data points
	public int getUsedDataLength(){
		return this.fftDataLength;
	}

    // Set a samplimg period
    public void setDeltaT(double deltaT){
        this.deltaT = deltaT;
        this.deltaTset = true;
    }

    // Get the samplimg period
    public double getDeltaT(){
        double ret = 0.0D;
        if(this.deltaTset){
            ret = this.deltaT;
        }
        else{
            System.out.println("detaT has not been set - zero returned");
        }
        return ret;
    }

    // Set a Rectangular window option
    public void setRectangular(){
        this.windowOption = 1;
	    this.windowSet = true;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = true;
	    }
	}

	// Set a Bartlett window option
    public void setBartlett(){
        this.windowOption = 2;
	    this.windowSet = true;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = true;
	    }
	}

	// Set a Welch window option
    public void setWelch(){
        this.windowOption = 3;
	    this.windowSet = true;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = true;
	    }
	}

	// Set a Hann window option
    public void setHann(){
        this.windowOption = 4;
	    this.windowSet = true;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = true;
	    }
	}

	// Set a Hamming window option
    public void setHamming(){
        this.windowOption = 5;
	    this.windowSet = true;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = true;
	    }
	}

	// Set a Kaiser window option
    public void setKaiser(double alpha){
        this.kaiserAlpha  = alpha;
        this.windowOption = 6;
	    this.windowSet = true;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = true;
	    }
	}

	// Set a Kaiser window option
	// default option for alpha
    public void setKaiser(){
        this.windowOption = 6;
	    this.windowSet = true;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = true;
	    }
	}

	// Set a Gaussian window option
    public void setGaussian(double alpha){
        if(alpha<2.0D){
            alpha=2.0D;
            System.out.println("setGaussian; alpha must be greater than or equal to 2 - alpha has been reset to 2");
        }
        this.gaussianAlpha  = alpha;
        this.windowOption = 7;
	    this.windowSet = true;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = true;
	    }
	}

    // Set a Gaussian window option
    // default option for alpha
    public void setGaussian(){
        this.windowOption = 7;
	    this.windowSet = true;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = true;
	    }
	}

    // Remove windowing
    public void removeWindow(){
        this.windowOption = 0;
	    this.windowSet = false;
        if(fftDataSet){
	        this.sumOfSquaredWeights = this.windowData(this.fftData, this.fftDataWindow, this.weights);
	        this.windowApplied = false;
	    }
	}

    // Applies a window to the data
	private double windowData(double[] data, double[] window, double[] weight){
	    int m = data.length;
	    int n = m/2-1;
	    int j = 0;
	    double sum = 0.0D;
	    switch(this.windowOption){
	        // 0.  No windowing applied or remove windowing
			case 0:
			// 1.  Rectangular
			case 1:	for(int i=0; i<=n; i++){
			            weight[i] = 1.0D;
			            window[j] = data[j++];
			            window[j] = data[j++];
			        }
			        sum = n+1;
					break;
			// 2.  Bartlett
			case 2:	for(int i=0; i<=n; i++){
			            weight[i] = 1.0D - Math.abs((i-n/2)/n/2);
			            sum += weight[i]*weight[i];
			            window[j] = data[j++]*weight[i];
			            window[j] = data[j++]*weight[i];
			        }
					break;
			// 3.  Welch
			case 3:	for(int i=0; i<=n; i++){
			            weight[i] = 1.0D - Fmath.square((i-n/2)/n/2);
		                sum += weight[i]*weight[i];
			            window[j] = data[j++]*weight[i];
			            window[j] = data[j++]*weight[i];
			        }
					break;
			// 4.  Hann
			case 4:	for(int i=0; i<=n; i++){
				        weight[i] = (1.0D - Math.cos(2.0D*i*Math.PI/n))/2.0D;
	                    sum += weight[i]*weight[i];
			            window[j] = data[j++]*weight[i];
			            window[j] = data[j++]*weight[i];
			        }
					break;
			// 5.  Hamming
			case 5:	for(int i=0; i<=n; i++){
			            weight[i] = 0.54D + 0.46D*Math.cos(2.0D*i*Math.PI/n);
                        sum += weight[i]*weight[i];
			            window[j] = data[j++]*weight[i];
			            window[j] = data[j++]*weight[i];
			        }
					break;
			// 6.  Kaiser
			case 6:	double denom = FourierTransform.modBesselIo(Math.PI*this.kaiserAlpha);
			        double numer = 0.0D;
			        for(int i=0; i<=n; i++){
			            numer = FourierTransform.modBesselIo(Math.PI*this.kaiserAlpha*Math.sqrt(1.0D-Fmath.square(2.0D*i/n-1.0D)));
			            weight[i] = numer/denom;
                        sum += weight[i]*weight[i];
			            window[j] = data[j++]*weight[i];
			            window[j] = data[j++]*weight[i];
			        }
					break;
	        // 6.  Kaiser
			case 7:	for(int i=0; i<=n; i++){
			            weight[i] = Math.exp(-0.5D*Fmath.square(this.gaussianAlpha*(2*i-n)/n));
                        sum += weight[i]*weight[i];
			            window[j] = data[j++]*weight[i];
			            window[j] = data[j++]*weight[i];
			        }
					break;
			}
		return sum;
	}

	// return modified Bessel Function of the zeroth order (for Kaiser window)
	//   after numerical Recipe's bessi0
	//   - Abramowitz and Stegun coeeficients
	public static double modBesselIo(double arg){
        double absArg = 0.0D;
        double poly   = 0.0D;
        double bessel = 0.0D;

        if((absArg = Math.abs(arg)) < 3.75){
            poly = arg/3.75;
            poly *= poly;
            bessel = 1.0D + poly*(3.5156229D + poly*(3.08989424D + poly*(1.2067492D + poly*(0.2659732 + poly*(0.360768e-1 + poly*0.45813e-2)))));
        }
        else{
            bessel = (Math.exp(absArg)/Math.sqrt(absArg))*(0.39894228D + poly*(0.1328592e-1D + poly*(0.225319e-2 + poly*(-0.157565e-2 + poly*(0.916281e-2 + poly*(-0.2057706e-1 + poly*(0.2635537e-1 + poly*(-0.1647633e-1 + poly*0.392377e-2))))))));
        }
        return bessel;
    }

	// get window option - see above for options
	public String getWindowOption(){
	    String option = " ";
	    switch(this.windowOption){
			case 0: option = "No windowing applied";
			        break;
			case 1:	option = "Rectangular";
					break;
			case 2:	option = "Bartlett";
					break;
			case 3:	option = "Welch";
					break;
			case 4:	option = "Hann";
			        break;
			case 5:	option = "Hamming";
			        break;
			case 6:	option = "Kaiser";
			        break;
            case 7:	option = "Gaussian";
			        break;
		}
		return option;
	}

    // Get the windowing weights
    public double[] getWeights(){
		return this.weights;
	}

    // set the number of segments
	public void setSegmentNumber(int sNum){
		this.segmentNumber = sNum;
	    this.segNumSet = true;
	    if(this.segLenSet)this.segLenSet=false;
	}

    // set the segment length
	public void setSegmentLength(int sLen){
		this.segmentLength = sLen;
	    this.segLenSet = true;
	    if(this.segNumSet)this.segNumSet=false;
	}

    // check and set up the segments
	private void checkSegmentDetails(){
		if(!this.fftDataSet)throw new IllegalArgumentException("No fft data has been entered or calculated");
	    if(this.fftDataLength<2)throw new IllegalArgumentException("More than one point, MANY MORE, are needed");

        // check if data number is even
        if(this.fftDataLength % 2 != 0){
	        System.out.println("Number of data points must be an even number");
	        System.out.println("last point deleted");
	        this.fftDataLength -= 1;
	        this.dataAltered = true;
	        this.warning[0] = true;
	    }

        // check segmentation with no overlap
        if(this.segNumSet && !this.overlap){
            if(this.fftDataLength % this.segmentNumber == 0){
                int segL = this.fftDataLength/this.segmentNumber;
                if(FourierTransform.checkPowerOfTwo(segL)){
                    this.segmentLength = segL;
                    this.segLenSet = true;
                }
                else{
                    System.out.println("segment length is not an integer power of two");
                    System.out.println("segment length reset to total data length, i.e. no segmentation");
                    warning[1] = true;
                    this.segmentNumber = 1;
                    this.segmentLength = this.fftDataLength;
                    this.segLenSet = true;
                }
            }
            else{
                System.out.println("total data length divided by the number of segments is not an integer");
                System.out.println("segment length reset to total data length, i.e. no segmentation");
                warning[2] = true;
                this.segmentNumber = 1;
                this.segmentLength = this.fftDataLength;
                this.segLenSet = true;
            }
        }

        if(this.segLenSet && !this.overlap){
            if(this.fftDataLength % this.segmentLength == 0){
                 if(FourierTransform.checkPowerOfTwo(this.segmentLength)){
                    this.segmentNumber = this.fftDataLength/this.segmentLength;
                    this.segNumSet = true;
                }
                else{
                    System.out.println("segment length is not an integer power of two");
                    System.out.println("segment length reset to total data length, i.e. no segmentation");
                    warning[1] = true;
                    this.segmentNumber = 1;
                    this.segmentLength = this.fftDataLength;
                    this.segNumSet = true;
                }
            }
            else{
                System.out.println("total data length divided by the segment length is not an integer");
                System.out.println("segment length reset to total data length, i.e. no segmentation");
                warning[3] = true;
                this.segmentNumber = 1;
                this.segmentLength = this.fftDataLength;
                this.segNumSet = true;
            }
        }

        // check segmentation with overlap
        if(this.segNumSet && this.overlap){
            if(this.fftDataLength % (this.segmentNumber+1) == 0){
                int segL = 2*this.fftDataLength/(this.segmentNumber+1);
                if(FourierTransform.checkPowerOfTwo(segL)){
                    this.segmentLength = segL;
                    this.segLenSet = true;
                }
                else{
                    System.out.println("segment length is not an integer power of two");
                    System.out.println("segment length reset to total data length, i.e. no segmentation");
                    warning[1] = true;
                    this.segmentNumber = 1;
                    this.segmentLength = this.fftDataLength;
                    this.segLenSet = true;
                    this.overlap = false;
                }
            }
            else{
                System.out.println("total data length divided by the number of segments plus one is not an integer");
                System.out.println("segment length reset to total data length, i.e. no segmentation");
                warning[4] = true;
                this.segmentNumber = 1;
                this.segmentLength = this.fftDataLength;
                this.segLenSet = true;
                this.overlap = false;
            }
        }

        if(this.segLenSet && this.overlap){
            if((2*this.fftDataLength) % this.segmentLength == 0){
                 if(FourierTransform.checkPowerOfTwo(this.segmentLength)){
                    this.segmentNumber = (2*this.fftDataLength)/this.segmentLength - 1;
                    this.segNumSet = true;
                }
                else{
                    System.out.println("segment length is not an integer power of two");
                    System.out.println("segment length reset to total data length, i.e. no segmentation");
                    warning[1] = true;
                    this.segmentNumber = 1;
                    this.segmentLength = this.fftDataLength;
                    this.segNumSet = true;
                    this.overlap = false;
                }
            }
            else{
                System.out.println("twice the total data length divided by the segment length is not an integer");
                System.out.println("segment length reset to total data length, i.e. no segmentation");
                warning[5] = true;
                this.segmentNumber = 1;
                this.segmentLength = this.fftDataLength;
                this.segNumSet = true;
                this.overlap = false;
            }
        }

	    if(!this.segNumSet && !this.segLenSet){
	        this.segmentNumber = 1;
	        this.segNumSet = true;
	        this.overlap = false;
	    }

        if(this.overlap && this.segmentNumber<2){
		    System.out.println("Overlap is not possible with less than two segments.");
			System.out.println("Overlap option has been reset to 'no overlap' i.e. to false.");
            this.overlap = false;
            this.segmentNumber = 1;
            this.segNumSet = true;
            warning[6] = true;
        }

        // check no segmentation option
	    int segLno = 0;
	    int segNno = 0;
	    int segLov = 0;
	    int segNov = 0;

	    if(this.segmentNumber==1){
	        // check if data number is a power of two
	        if(!FourierTransform.checkPowerOfTwo(this.fftDataLength)){
	            boolean test0 = true;
	            boolean test1 = true;
	            boolean test2 = true;
	            int newL = 0;
	            int ii=2;
	            // not a power of two - check segmentation options
	            // no overlap option
	            while(test0){
	                newL = this.fftDataLength/ii;
	                if(FourierTransform.checkPowerOfTwo(newL) && (this.fftDataLength % ii)==0){
	                    test0 = false;
	                    segLno = newL;
	                    segNno = ii;
	                }
	                else{
	                    if(newL<2){
	                        test1 = false;
	                        test0 = false;
	                    }
	                    else{
	                        ii++;
	                    }
	                }
	            }
	            test0 = true;
	            ii = 2;
	            // overlap option
	            while(test0){
	                newL = 2*(this.fftDataLength/(ii+1));
	                if(FourierTransform.checkPowerOfTwo(newL) && (this.fftDataLength % (ii+1))==0){
	                    test0 = false;
	                    segLov = newL;
	                    segNov = ii;
	                }
	                else{
	                    if(newL<2){
	                        test2 = false;
	                        test0 = false;
	                    }
	                    else{
	                        ii++;
	                    }
	                }
	            }
	            // compare overlap and no overlap options
	            boolean setSegment = true;
	            int segL = 0;
	            int segN = 0;
	            boolean ovrlp = false;
	            if(test1){
	                if(test2){
	                    if(segLov>segLno){
	                        segL = segLov;
	                        segN = segNov;
	                        ovrlp = true;
	                    }
	                    else{
	                        segL = segLno;
	                        segN = segNno;
	                        ovrlp = false;
	                    }
	                }
	                else{
	                    segL = segLno;
	                    segN = segNno;
	                    ovrlp = false;
	                }
	            }
	            else{
	                if(test2){
	                    segL = segLov;
	                    segN = segNov;
	                    ovrlp = true;
	                }
	                else{
	                    setSegment = false;
	                }
	            }

                // compare segmentation and zero padding
                if(setSegment && (this.originalDataLength-segL <= this.fftDataLength - this.originalDataLength)){
	                System.out.println("Data length is not an integer power of two");
	                System.out.println("Data cannot be transformed as a single segment");
	                System.out.print("The data has been split into " + segN+ " segments of length " + segL);
	                if(ovrlp){
	                    System.out.println(" with 50% overlap");
	                }
	                else{
	                    System.out.println(" with no overlap");
	                }
	                this.segmentLength = segL;
	                this.segmentNumber = segN;
	                this.overlap = ovrlp;
	                this.warning[7] = true;
	            }
	            else{
	                System.out.println("Data length is not an integer power of two");
	                if(this.dataAltered){
	                    System.out.println("Deleted point has been restored and the data has been padded with zeros to give a power of two length");
	                    this.warning[0] = false;
	                }
	                else{
    	                System.out.println("Data has been padded with zeros to give a power of two length");
    	            }
	                this.warning[8] = true;
	            }
	        }
	    }
	}

	// get the number of segments
	public int getSegmentNumber(){
		return this.segmentNumber;
	}

    // get the segment length
	public int getSegmentLength(){
		return this.segmentLength;
	}

	// set overlap option - see above (head of program comment lines) for option description
	public void setOverlapOption(boolean overlapOpt){
		boolean old = this.overlap;
		this.overlap = overlapOpt;
		if(old != this.overlap){
		    if(this.fftDataSet){
		        this.setSegmentNumber(this.segmentNumber);
		    }
		}
	}

	// get overlap option - see above for options
	public boolean getOverlapOption(){
		return this.overlap;
	}

	// calculate the number of data points given the:
    // segment length (segLen), number of segments (segNum)
    // and the overlap option (overlap: true - overlap, false - no overlap)
    public static int calcDataLength(boolean overlap, int segLen, int segNum){
        if(overlap){
            return (segNum+1)*segLen/2;
        }
        else{
            return segNum*segLen;
        }
    }

    // Method for performing a Fast Fourier Transform
    public void transform(){

        // set up data array
        int isign = 1;
        if(!this.fftDataSet)throw new IllegalArgumentException("No data has been entered for the Fast Fourier Transform");
	    if(this.originalDataLength!=this.fftDataLength){
	        System.out.println("Fast Fourier Transform data length ," + this.originalDataLength + ", is not an integer power of two");
	        System.out.println("WARNING!!! Data has been padded with zeros to fill to nearest integer power of two length " + this.fftDataLength);
	    }

        // Perform fft
        double[] hold = new double[this.fftDataLength*2];
        for(int i=0; i<this.fftDataLength*2; i++)hold[i] = this.fftDataWindow[i];
        basicFft(hold, this.fftDataLength, isign);
        for(int i=0; i<this.fftDataLength*2; i++)this.transformedDataFft[i] = hold[i];

        // fill transformed data arrays
        for(int i=0; i<this.fftDataLength; i++){
            this.transformedDataComplex[i].reset(this.transformedDataFft[2*i], this.transformedDataFft[2*i+1]);
        }
    }

    // Method for performing an inverse Fast Fourier Transform
    public void inverse(){

        // set up data array
        int isign = -1;
        if(!this.fftDataSet)throw new IllegalArgumentException("No data has been entered for the inverse Fast Fourier Transform");
        if(this.originalDataLength!=this.fftDataLength){
	        System.out.println("Fast Fourier Transform data length ," + this.originalDataLength + ", is not an integer power of two");
	        System.out.println("WARNING!!! Data has been padded with zeros to fill to nearest integer power of two length " + this.fftDataLength);
	    }

        // Perform inverse fft
        double[] hold = new double[this.fftDataLength*2];
        for(int i=0; i<this.fftDataLength*2; i++)hold[i] = this.fftDataWindow[i];
        basicFft(hold, this.fftDataLength, isign);

        for(int i=0; i<this.fftDataLength*2; i++)this.transformedDataFft[i] = hold[i]/this.fftDataLength;

        // fill transformed data arrays
        for(int i=0; i<this.fftDataLength; i++){
            this.transformedDataComplex[i].reset(this.transformedDataFft[2*i], this.transformedDataFft[2*i+1]);
        }
    }

    // Base method for performing a Fast Fourier Transform
    // Based on the Numerical Recipes procedure four1
    // If isign is set to +1 this method replaces fftData[0 to 2*nn-1] by its discrete Fourier Transform
    // If isign is set to -1 this method replaces fftData[0 to 2*nn-1] by nn times its inverse discrete Fourier Transform
    // nn MUST be an integer power of 2.  This is not checked for in this method, fastFourierTransform(...), for speed.
    // If not checked for by the calling method, e.g. powerSpectrum(...) does, the method checkPowerOfTwo() may be used to check this.
    // The real and imaginary parts of the data are stored adjacently
    // i.e. fftData[0] holds the real part, fftData[1] holds the corresponding imaginary part of a data point
    // data array and data array length over 2 (nn) transferred as arguments
    // result NOT returned to this.transformedDataFft
    // Based on the Numerical Recipes procedure four1
    public void basicFft(double[] data, int nn, int isign)
    {
        double dtemp = 0.0D, wtemp = 0.0D, tempr = 0.0D, tempi = 0.0D;
        double theta = 0.0D, wr = 0.0D, wpr = 0.0D, wpi = 0.0D, wi = 0.0D;
	    int istep = 0, m = 0, mmax = 0;
	    int n = nn << 1;
	    int j = 1;
	    int jj = 0;
	    for (int i=1;i<n;i+=2) {
	        jj = j-1;
		    if (j > i) {
		        int ii = i-1;
		        dtemp = data[jj];
		        data[jj] = data[ii];
		        data[ii] = dtemp;
		        dtemp = data[jj+1];
		        data[jj+1] = data[ii+1];
		        data[ii+1] = dtemp;
		    }
		    m = n >> 1;
		    while (m >= 2 && j > m) {
			    j -= m;
			    m >>= 1;
		    }
		    j += m;
	    }
	    mmax=2;
	    while (n > mmax) {
		    istep=mmax << 1;
		    theta=isign*(6.28318530717959D/mmax);
		    wtemp=Math.sin(0.5D*theta);
		    wpr = -2.0D*wtemp*wtemp;
		    wpi=Math.sin(theta);
		    wr=1.0D;
		    wi=0.0D;
		    for (m=1;m<mmax;m+=2L) {
			    for (int i=m;i<=n;i+=istep) {
			        int ii =  i - 1;
					jj=ii+mmax;
				    tempr=wr*data[jj]-wi*data[jj+1];
				    tempi=wr*data[jj+1]+wi*data[jj];
				    data[jj]=data[ii]-tempr;
				    data[jj+1]=data[ii+1]-tempi;
				    data[ii] += tempr;
				    data[ii+1] += tempi;
			    }
			    wr=(wtemp=wr)*wpr-wi*wpi+wr;
			    wi=wi*wpr+wtemp*wpi+wi;
		    }
		    mmax=istep;
	    }
    }

    // Get the transformed data as Complex
    public Complex[] getTransformedDataAsComplex(){
		return this.transformedDataComplex;
	}

	// Get the transformed data array as adjacent real and imaginary pairs
    public double[] getTransformedDataAsAlternate(){
		return this.transformedDataFft;
	}

	// Performs and returns results a fft power spectrum density (psd) estimation
	// of unsegmented, segmented or segemented and overlapped data
	// data in array fftDataWindow
	public double[][] powerSpectrum(){

	    this.checkSegmentDetails();

        this.psdNumberOfPoints = this.segmentLength/2+1;
        this.powerSpectrumEstimate = new double[2][this.psdNumberOfPoints];

		if(!overlap && this.segmentNumber<2){
		    // Unsegmented and non-overlapped data

            // set up data array
            int isign = 1;
            if(!this.fftDataSet)throw new IllegalArgumentException("No data has been entered for the Fast Fourier Transform");
	        if(!FourierTransform.checkPowerOfTwo(this.fftDataLength))throw new IllegalArgumentException("Fast Fourier Transform data length ," + this.fftDataLength + ", is not an integer power of two");

            // perform fft
            double[] hold = new double[this.fftDataLength*2];
            for(int i=0; i<this.fftDataLength*2; i++)hold[i] = this.fftDataWindow[i];
            basicFft(hold, this.fftDataLength, isign);
            for(int i=0; i<this.fftDataLength*2; i++)this.transformedDataFft[i] = hold[i];

            // fill transformed data arrays
            for(int i=0; i<this.fftDataLength; i++){
                this.transformedDataComplex[i].reset(this.transformedDataFft[2*i], this.transformedDataFft[2*i+1]);
            }

		    // obtain weighted mean square amplitudes
		    this.powerSpectrumEstimate[1][0] = Fmath.square(hold[0]) + Fmath.square(hold[1]);
		    for(int i=1; i<this.psdNumberOfPoints-1; i++){
                this.powerSpectrumEstimate[1][i] = Fmath.square(hold[2*i]) + Fmath.square(hold[2*i+1]) + Fmath.square(hold[2*this.segmentLength-2*i]) + Fmath.square(hold[2*this.segmentLength-2*i+1]);
		    this.powerSpectrumEstimate[1][this.psdNumberOfPoints-1] = Fmath.square(hold[2*(this.psdNumberOfPoints-1)]) + Fmath.square(hold[2*this.psdNumberOfPoints-1]);
            }

		    // Normalise
	        for(int i=0; i<this.psdNumberOfPoints; i++){
                this.powerSpectrumEstimate[1][i] = 2.0D*this.powerSpectrumEstimate[1][i]/(this.fftDataLength*this.sumOfSquaredWeights);
            }

            // Calculate frequencies
		    for(int i=0; i<this.psdNumberOfPoints; i++){
		         this.powerSpectrumEstimate[0][i] = (double)i/((double)this.segmentLength*this.deltaT);
		    }
        }
		else{
		    // Segmented or segmented and overlapped data
		    this.powerSpectrumEstimate = powerSpectrumSeg();
		}

	    this.powSpecDone = true;

	    return this.powerSpectrumEstimate;
	}



	// Performs and returns results a fft power spectrum density (psd) estimation of segmented or segemented and overlaped data
	// Data in fftDataWindow array
	// Private method for PowerSpectrum (see above)
	private double[][] powerSpectrumSeg(){

        // set up segment details
        int segmentStartIndex = 0;
        int segmentStartIncrement = this.segmentLength;
        if(this.overlap)segmentStartIncrement /= 2;
        double[] data = new double[2*this.segmentLength];       // holds data and transformed data for working segment
        this.psdNumberOfPoints = this.segmentLength/2+1;          // number of PSD points
        double[] segPSD = new double[this.psdNumberOfPoints];   // holds psd for working segment
        double[][] avePSD = new double[2][this.psdNumberOfPoints];   // first row - frequencies
                                                                     // second row - accumaltes psd for averaging and then the averaged psd

        // initialis psd array and transform option
        for(int j=0; j<this.psdNumberOfPoints; j++)avePSD[1][j] = 0.0D;
        int isign = 1;

        // loop through segments
        for(int i=1; i<=this.segmentNumber; i++){

            // collect segment data
            for(int j=0; j<2*this.segmentLength; j++)data[j] = this.fftData[segmentStartIndex+j];

            // window data
            if(i==1){
                this.sumOfSquaredWeights = this.windowData(data, data, this.weights);
            }
            else{
                int k=0;
                for(int j=0; j<this.segmentLength; j++){
                    data[k] = data[k]*this.weights[j];
                    data[++k] = data[k]*this.weights[j];
                    ++k;
                }
            }

            // perform fft on windowed segment
            basicFft(data, this.segmentLength, isign);

		    // obtain weighted mean square amplitudes
		    segPSD[0] = Fmath.square(data[0]) + Fmath.square(data[1]);
		    for(int j=1; j<this.psdNumberOfPoints-1; j++){
                segPSD[j] = Fmath.square(data[2*j]) + Fmath.square(data[2*j+1]) + Fmath.square(data[2*this.segmentLength-2*j]) + Fmath.square(data[2*this.segmentLength-2*j+1]);
		    segPSD[this.psdNumberOfPoints-1] = Fmath.square(data[2*(this.psdNumberOfPoints-1)]) + Fmath.square(data[2*this.psdNumberOfPoints-1]);
            }

		    // Normalise
	        for(int j=0; j<this.psdNumberOfPoints; j++){
                segPSD[j] = 2.0D*segPSD[j]/(this.segmentLength*this.sumOfSquaredWeights);
            }

            // accumalate for averaging
            for(int j=0; j<this.psdNumberOfPoints; j++)avePSD[1][j] += segPSD[j];

            // increment segment start index
            segmentStartIndex += segmentStartIncrement;
        }

        // average all segments
        for(int j=0; j<this.psdNumberOfPoints; j++)avePSD[1][j] /= this.segmentNumber;

        // Calculate frequencies
		for(int i=0; i<this.psdNumberOfPoints; i++){
		    avePSD[0][i] = (double)i/((double)this.segmentLength*this.deltaT);
	    }

        return avePSD;
	}


	// Get the power spectrum
    public double[][] getpowerSpectrumEstimate(){
        if(!this.powSpecDone)System.out.println("getpowerSpectrumEstimate - powerSpectrum has not been called - null returned");
		return this.powerSpectrumEstimate;
	}


	// get the number of power spectrum frequency points
	public int getNumberOfPsdPoints(){
		return this.psdNumberOfPoints;
	}


	// Set the line option in plotting the power spectrum or correlation
	// = 0 join points with straight lines
	// = 1 cubic spline interpolation
	// = 3 no line - only points
	public void setPlotLineOption(int lineOpt){
	    this.plotLineOption = lineOpt;
	}

	// Get the line option in ploting the power spectrum or correlation
	// = 0 join points with straight lines
	// = 1 cubic spline interpolation
	// = 3 no line - only points
	public int getPlotLineOption(){
	    return this.plotLineOption;
	}

    // Set the point option in plotting the power spectrum or correlation
	// = 0 no point symbol
	// = 1 filled circles
	public void setPlotPointOption(int pointOpt){
	    this.plotPointOption = pointOpt;
	}

	// Get the point option in plotting the power spectrum or correlation
	// = 0 no point symbol
	// = 1 filled circles
	public int getPlotPointOption(){
	    return this.plotPointOption;
	}


    // Return correlation of data already entered with data passed as this method's argument
    // data must be real
    public double[][] correlate(double[] data){
        int nLen = data.length;
        if(!this.fftDataSet)throw new IllegalArgumentException("No data has been previously entered");
        if(nLen!=this.originalDataLength)throw new IllegalArgumentException("The two data sets to be correlated are of different length");
        if(!FourierTransform.checkPowerOfTwo(nLen))throw new IllegalArgumentException("The length of the correlation data sets is not equal to an integer power of two");

        this.complexCorr = Complex.oneDarray(nLen);
        for(int i=0; i<nLen; i++){
            this.complexCorr[i].setReal(data[i]);
            this.complexCorr[i].setImag(0.0D);
        }

        this.fftCorr = new double[2*nLen];
        int j=-1;
        for(int i=0; i<nLen; i++){
            this.fftCorr[++j] = data[i];
            this.fftCorr[++j] = 0.0D;
        }

        return correlation(nLen);
    }

    // Return correlation of data1 and data2 passed as this method's arguments
    // data must be real
    public double[][] correlate(double[] data1, double[] data2){
        int nLen = data1.length;
        int nLen2 = data2.length;
        if(nLen!=nLen2)throw new IllegalArgumentException("The two data sets to be correlated are of different length");
        if(!FourierTransform.checkPowerOfTwo(nLen))throw new IllegalArgumentException("The length of the correlation data sets is not equal to an integer power of two");

        this.fftDataLength = nLen;
	    this.complexData = Complex.oneDarray(this.fftDataLength);
	    for(int i=0; i<this.fftDataLength; i++){
	        this.complexData[i].setReal(data1[i]);
		    this.complexData[i].setImag(0.0D);
	    }

	    this.fftData = new double[2*this.fftDataLength];
	    int j = 0;
	    for(int i=0; i<this.fftDataLength; i++){
	        this.fftData[j] = data1[i];
	        j++;
	        this.fftData[j] = 0.0D;
	        j++;
	    }
	    this.fftDataSet = true;

	    this.fftDataWindow = new double[2*this.fftDataLength];
	    this.weights = new double[this.fftDataLength];
	    this.sumOfSquaredWeights = windowData(this.fftData, this.fftDataWindow, this.weights);

	    this.transformedDataFft = new double[2*this.fftDataLength];
	    this.transformedDataComplex = Complex.oneDarray(this.fftDataLength);

        this.complexCorr = Complex.oneDarray(nLen);
        for(int i=0; i<nLen; i++){
            this.complexCorr[i].setReal(data2[i]);
            this.complexCorr[i].setImag(0.0D);
        }

        this.fftCorr = new double[2*nLen];
        j=-1;
        for(int i=0; i<nLen; i++){
            this.fftCorr[++j] = data2[i];
            this.fftCorr[++j] = 0.0D;
        }

        return correlation(nLen);
    }

    // Returns the correlation of the data in fftData and fftCorr
    private double[][] correlation(int nLen){

        this.fftDataWindow = new double[2*nLen];
        this.fftCorrWindow = new double[2*nLen];
        this.weights = new double[nLen];

        this.sumOfSquaredWeights = windowData(this.fftData, this.fftDataWindow, this.weights);
        windowData(this.fftCorr, this.fftCorrWindow, this.weights);

        // Perform fft on first set of stored data
        int isign = 1;
        double[] hold1 = new double[2*nLen];
        for(int i=0; i<nLen*2; i++)hold1[i] = this.fftDataWindow[i];
        basicFft(hold1, nLen, isign);

        // Perform fft on second set of stored data
        isign = 1;
        double[] hold2 = new double[2*nLen];
        for(int i=0; i<nLen*2; i++)hold2[i] = this.fftCorrWindow[i];
        basicFft(hold2, nLen, isign);

        // multiply hold1 by complex congugate of hold2
        double[] hold3 = new double[2*nLen];
        int j=0;
        for(int i=0; i<nLen; i++){
          hold3[j] = (hold1[j]*hold2[j] + hold1[j+1]*hold2[j+1])/nLen;
          hold3[j+1] = (-hold1[j]*hold2[j+1] + hold1[j+1]*hold2[j])/nLen;
          j += 2;
        }

        // Inverse transform -> correlation
        isign = -1;
        basicFft(hold3, nLen, isign);

        // fill correlation array
        for(int i=0; i<2*nLen; i++)this.transformedDataFft[i]=hold3[i];
        this.correlationArray = new double[2][nLen];
        j=0;
        int k=nLen;
		for(int i=nLen/2+1; i<nLen; i++){
		    this.correlationArray[1][j] = hold3[k]/nLen;
		    j++;
		    k+=2;
        }
        k=0;
		for(int i=0; i<nLen/2; i++){
		    this.correlationArray[1][j] = hold3[k]/nLen;
		    j++;
		    k+=2;
	    }

	    // calculate time lags
		this.correlationArray[0][0]= -(double)(nLen/2)*this.deltaT;
	    for(int i=1; i<nLen; i++){
	        this.correlationArray[0][i] = this.correlationArray[0][i-1]+ this.deltaT;
	    }

        this.correlateDone = true;
        return this.correlationArray;
    }

    // Get the correlation
    public double[][]getCorrelation(){
        if(!this.correlateDone){
            System.out.println("getCorrelation - correlation has not been called - no correlation returned");
		}
        return this.correlationArray;
	}

	// Performs  a fft power spectrum density (psd) estimation
	// on a moving window throughout the original data set
	// returning the results as a frequency time matrix
	// windowLength is the length of the window in time units
	public double[][] shortTime(double windowTime){
	    int windowLength = (int)Math.round(windowTime/this.deltaT);
	    if(!checkPowerOfTwo(windowLength)){
            int low = lastPowerOfTwo(windowLength);
            int high = nextPowerOfTwo(windowLength);

            if((windowLength - low)<=(high-windowLength)){
                windowLength = low;
                if(low==0)windowLength=high;
            }
            else{
                windowLength = high;
            }
            System.out.println("Method - shortTime");
            System.out.println("Window length, provided as time, "+windowTime+", did not convert to an integer power of two data points");
            System.out.println("A value of "+((windowLength-1)*this.deltaT)+" was substituted");
        }

	    return shortTime(windowLength);
	}

	// Performs  a fft power spectrum density (psd) estimation
	// on a moving window throughout the original data set
	// returning the results as a frequency time matrix
	// windowLength is the number of points in the window
	public double[][] shortTime(int windowLength){

        if(!FourierTransform.checkPowerOfTwo(windowLength))throw new IllegalArgumentException("Moving window data length ," + windowLength + ", is not an integer power of two");
        if(!this.fftDataSet)throw new IllegalArgumentException("No data has been entered for the Fast Fourier Transform");
	    if(windowLength>this.originalDataLength)throw new IllegalArgumentException("The window length, " + windowLength + ", is greater than the data length, " + this.originalDataLength + ".");

        // if no window option has been set - default = Gaussian with alpha = 2.5
        if(this.windowOption==0)this.setGaussian();
        // set up time-frequency matrix
        //  first row = blank cell followed by time vector
        //  first column = blank cell followed by frequency vector
        //  each cell is then the mean square amplitude at that frequency and time
        this.numShortTimes = this.originalDataLength - windowLength + 1;
        this.numShortFreq = windowLength/2;
        this.timeFrequency = new double[this.numShortFreq+1][this.numShortTimes+1];
        this.timeFrequency[0][0]=0.0D;
        this.timeFrequency[0][1]=(double)(windowLength-1)*this.deltaT/2.0D;
        for(int i=2;i<=this.numShortTimes;i++){
            this.timeFrequency[0][i] = this.timeFrequency[0][i-1] + this.deltaT;
        }
        for(int i=0;i<this.numShortFreq;i++){
            this.timeFrequency[i+1][0] = (double)i/((double)windowLength*this.deltaT);
        }

        // set up window details
        this.segmentLength = windowLength;
        int windowStartIndex = 0;
        double[] data = new double[2*windowLength];             // holds data and transformed data for working window
        double[] winPSD = new double[this.numShortFreq];        // holds psd for working window
        int isign = 1;

        // loop through time shifts
        for(int i=1; i<=this.numShortTimes; i++){

            // collect window data
            for(int j=0; j<2*windowLength; j++)data[j] = this.fftData[windowStartIndex+j];

            // window data
            if(i==1){
                this.sumOfSquaredWeights = this.windowData(data, data, this.weights);
            }
            else{
                int k=0;
                for(int j=0; j<this.segmentLength; j++){
                    data[k] = data[k]*this.weights[j];
                    data[++k] = data[k]*this.weights[j];
                    ++k;
                }
            }

            // perform fft on windowed segment
            basicFft(data, windowLength, isign);

		    // obtain weighted mean square amplitudes
		    winPSD[0] = Fmath.square(data[0]) + Fmath.square(data[1]);
		    for(int j=1; j<this.numShortFreq; j++){
                winPSD[j] = Fmath.square(data[2*j]) + Fmath.square(data[2*j+1]) + Fmath.square(data[2*windowLength-2*j]) + Fmath.square(data[2*windowLength-2*j+1]);
            }

		    // Normalise and place in time-frequency matrix
	        for(int j=0; j<this.numShortFreq; j++){
                timeFrequency[j+1][i] = 2.0D*winPSD[j]/(windowLength*this.sumOfSquaredWeights);
            }

            // increment segment start index
            windowStartIndex += 2;
        }

        this.shortTimeDone = true;
	    return this.timeFrequency;
	}

	// Return time frequency matrix
	public double[][] getTimeFrequencyMatrix(){
	    if(!this.shortTimeDone)throw new IllegalArgumentException("No short time Fourier transform has been performed");
	    return this.timeFrequency;
	}

	// Return number of times in short time Fourier transform
	public int getShortTimeNumberOfTimes(){
	    if(!this.shortTimeDone)throw new IllegalArgumentException("No short time Fourier transform has been performed");
	    return this.numShortTimes;
	}

    // Return number of frequencies in short time Fourier transform
	public int getShortTimeNumberOfFrequencies(){
	    if(!this.shortTimeDone)throw new IllegalArgumentException("No short time Fourier transform has been performed");
	    return this.numShortFreq;
	}

	// Return number of points in short time Fourier transform window
	public int getShortTimeWindowLength(){
	    if(!this.shortTimeDone)throw new IllegalArgumentException("No short time Fourier transform has been performed");
	    return this.segmentLength;
	}

    // returns nearest power of two that is equal to or lower than argument length
    public static int lastPowerOfTwo(int len){

	    boolean test0 = true;
	    while(test0){
	        if(FourierTransform.checkPowerOfTwo(len)){
	            test0 = false;
	        }
	        else{
	            len--;
	        }
	    }
	    return len;
    }

    // returns nearest power of two that is equal to or higher than argument length
	public static int nextPowerOfTwo(int len){

	    boolean test0 = true;
	    while(test0){
	        if(FourierTransform.checkPowerOfTwo(len)){
	            test0 = false;
	        }
	        else{
	            len++;
	        }
	    }
	    return len;
	}

	// Checks whether the argument n is a power of 2
	public static boolean checkPowerOfTwo(int n){
		boolean test = true;
		int m = n;
		while(test && m>1){
			if((m % 2)!=0){
				test = false;
			}
			else{
				m /= 2;
			}
		}
		return test;
	}

	// Checks whether the argument n is an integer times a integer power of 2
	// returns integer multiplier if true
	// returns zero if false
	public static int checkIntegerTimesPowerOfTwo(int n){
		boolean testOuter1 = true;
		boolean testInner1 = true;
		boolean testInner2 = true;
		boolean testReturn = true;

		int m = n;
		int j = 1;
		int mult = 0;

		while(testOuter1){
		    testInner1 = FourierTransform.checkPowerOfTwo(m);
			if(testInner1){
				testReturn = true;
				testOuter1 = false;
			}
			else{
			    testInner2 = true;
			    while(testInner2){
				    m /= ++j;
				    if(m < 1){
				        testInner2 = false;
				        testInner1 = false;
				        testOuter1 = false;
				        testReturn = false;
				    }
				    else{
			            if((m % 2)==0)testInner2 = false;
			        }
			    }
			}
		}
		if(testReturn)mult = j;
		return mult;
	}

}




