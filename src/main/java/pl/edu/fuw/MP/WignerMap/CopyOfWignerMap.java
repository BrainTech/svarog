package pl.edu.fuw.MP.WignerMap;

import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;

public class CopyOfWignerMap {
	public double Map[][]=null;
	public double NormMap[][]=null;
	private double TimeAxis[]=null;
	private double FreqAxis[]=null;
	public int SizeX,SizeY;
	private int Start,Stop,DimBase;
	private static final double EPS=1.0e-13;
	private static final double PI2M=2.0*Math.PI;
	private double minT,maxT,minF,maxF,ATime,BTime,AFreq,BFreq;
	private double minVal=0.0,maxVal=0.0,SamplingFreq=1.0;
	private double  reconst[][]=null;
	private double ReconstSignal[]=null;
	private double signal[]=null;
	private double FreqMin=0.0,FreqMax=1.0;
	private boolean mask[]=null;
	private int Count=0;

	public double [][]getWignerMap() {
		return Map;
	}

	public double getSignalValue(int k) {
		if (signal==null || k>=DimBase) {
			return 0.0;
		}
		return signal[k];
	}

	public double getReconstValue(int k) {
		if (ReconstSignal==null || k>=DimBase) {
			return 0.0;
		}
		return ReconstSignal[k];
	}

	public double getValue(int x,int y) {
		if (Map==null) {
			return 0.0F;
		}
		if (x>=0 && x<SizeX && y>=0 && y<SizeY) {
			if (Math.abs(minVal-maxVal)<=EPS) {
				return 0.0F;
			}
			return (double)(100.0*(Map[x][y]-minVal)/(maxVal-minVal));
		}
		return 0.0F;
	}

	private void makeCosTable(double Cos[],int start,int stop,
	                          double freq,double phase) {
		double sinFreq,cosFreq,sinPhase,cosPhase,newCos,oldSin,oldCos;

		sinFreq=Math.sin(freq);
		cosFreq=Math.cos(freq);

		phase-=freq*start;
		sinPhase=Math.sin(phase);
		cosPhase=Math.cos(phase);

		oldSin=0.0;
		oldCos=1.0;

		Cos[start]=cosPhase;
		for (int i=start+1 ; i<=stop ; i++) {
			newCos=oldCos*cosFreq-oldSin*sinFreq;
			oldSin=oldSin*cosFreq+oldCos*sinFreq;
			oldCos=newCos;
			Cos[i]=oldCos*cosPhase-oldSin*sinPhase;
		}
	}

	private void RSignal(StandardBookSegment book,double signal[]) {
		double tmpsig[]=new double[DimBase+1],sum,maxSig,minSig;
		int i,k,itmp;
		double Exp[]=new double[DimBase],Cos[]=new double[DimBase];
		double dtmp;
		double  ptr[];

		Count=0;

		for (i=0 ; i<DimBase ; i++) {
			signal[i]=0.0;
		}

		int BookLen=book.getAtomCount();
		mask=new boolean[ BookLen ];
		reconst=null;
		System.gc();
		reconst=new double[BookLen][DimBase];

		for (k=0 ; k<BookLen ; k++) {
			mask[k]=false;
			ptr=reconst[k];
			for (i=0 ; i<DimBase ; i++)
				ptr[i]=0.0F;
		}

		for (int kk=0 ; kk<BookLen ; kk++) {
			StandardBookAtom atom=book.getAtomAt(kk);
			ptr=reconst[kk];
			if (atom.getType()==StandardBookAtom.DIRACDELTA_IDENTITY) {
				signal[itmp=(int)atom.getPosition()]+=/*(dtmp=((atom.getPhase())>=0.5F) ?
						  -1.0 : 1.0)*/ (dtmp=atom.getModulus());
				ptr[itmp]=(double)dtmp;
			} else if (atom.getType()==StandardBookAtom.SINCOSWAVE_IDENTITY) {
				double freq=atom.getFrequency(),
				       phase=atom.getPhase()-freq*atom.getPosition();

				for (i=0,sum=0.0 ; i<DimBase ; i++) {
					sum+=SQR(tmpsig[i]=Math.cos(freq*i+phase));
				}

				sum=atom.getModulus()/Math.sqrt(sum);
				for (i=0 ; i<DimBase ; i++) {
					signal[i]+=(dtmp=tmpsig[i]*sum);
					ptr[i]=(double)dtmp;
				}
			} else {
				double freq=atom.getFrequency(),
				       phase=atom.getPhase()-freq*atom.getPosition();
				int start=0,stop=DimBase-1;

				MakeExpTable(Exp,Math.PI/SQR(atom.getScale()),
				             atom.getPosition(),
				             start,stop);

				makeCosTable(Cos,start,stop,freq,phase);

				for (i=start,sum=0.0 ; i<=stop ; i++)
					sum+=SQR(tmpsig[i]=Exp[i]*Cos[i]);
				/*Math.cos(freq*i+phase)*/
				sum=atom.getModulus()/Math.sqrt(sum);
				for (i=start ; i<=stop ; i++) {
					signal[i]+=(dtmp=tmpsig[i]*sum);
					ptr[i]=(double)dtmp;
				}
			}
		}

		minSig=maxSig=signal[0];
		for (i=1 ; i<DimBase ; i++) {
			if (signal[i]<minSig) {
				minSig=signal[i];
			}
			if (signal[i]>maxSig) {
				maxSig=signal[i];
			}
		}

//   double Yscale=((minSig==maxSig) ? 1.0 : 1.0/(maxSig-minSig));
//   RBot=-Yscale*minSig;

		ReconstSignal=new double[DimBase];
		for (i=0 ; i<DimBase ; i++) {
			//  signal[i]=Yscale*(signal[i]-minSig);
			ReconstSignal[i]=0.0; //RBot;
		}

		/*
		for(k=0 ; k<BookLen ; k++) {
			ptr=reconst[k];
		    for(i=0 ; i<DimBase ; i++) {
		       ptr[i]=(double)(Yscale*ptr[i]);
		    }
		}
		*/
	}

	public void atomToReconst(int k) {
		if (mask==null) {
			return;
		}

		if (k>=0 && k<mask.length) {
			double ptr[]=reconst[k];
			int i;

			if (mask[k]) {
				Count--;
				for (i=0 ; i<DimBase ; i++) {
					ReconstSignal[i]-=ptr[i];
				}
			} else {
				Count++;
				for (i=0 ; i<DimBase ; i++) {
					ReconstSignal[i]+=ptr[i];
				}
			}

			mask[k]=!mask[k];
			if (Count==0) {
				for (i=0 ; i<DimBase ; i++) {
					ReconstSignal[i]=0.0;
				}
			}
		}
	}

	public final void setBook(StandardBookSegment book) {
		boolean rec=true;
		double ref[];
		int i,j,k;

		DimBase=(int)book.getSegmentLength();
		for (i=0 ; i<SizeX ; i++) {
			for (j=0,ref=Map[i] ; j<SizeY ; j++) {
				ref[j]=0.0F;
			}
		}

		minVal=maxVal=0.0;
		SamplingFreq=book.getSamplingFrequency();
		int BookSize=book.getAtomCount();

		SetWignerParameters();
		for (k=0 ; k<BookSize ; k++) {
			StandardBookAtom atom=book.getAtomAt(k);
			AddAtom(atom.getModulus(), (int)atom.getScale(), (int)atom.getPosition(), atom.getFrequency());
		}

		signal=new double[DimBase];
		if (rec) {
			RSignal(book, signal);
		} else {
			for (i=0 ; i<DimBase ; i++) {
				signal[i]=0.0F;
			}
		}
		SetMinMax();
	}

	public double []getSignal() {
		return signal;
	}

	public double []getReconstruction() {
		return ReconstSignal;
	}

	private void SetWignerParameters() {
		minT=0;
		maxT=DimBase-1;
		minF=(int)(0.5+FreqMin*(DimBase-1)/SamplingFreq);
		maxF=(int)(0.5+FreqMax*(DimBase-1)/SamplingFreq);
		ATime=(maxT-minT)/(SizeX-1);
		BTime=minT;
		AFreq=(maxF-minF)/(SizeY-1);
		BFreq=minF;
	}

	public void SetTrueSize(double StartFreq,double StopFreq) {
		if (StartFreq>=StopFreq) {
			return;
		}
		FreqMin=StartFreq;
		FreqMax=StopFreq;
	}

	public CopyOfWignerMap(int Sx,int Sy,int minTT,int maxTT,int minFF,int maxFF) {
		SizeX=Sx;
		SizeY=Sy;
		minT=minTT;
		maxT=maxTT;
		minF=minFF;
		maxF=maxFF;

		Map=null;
		NormMap=null;
		TimeAxis=null;
		FreqAxis=null;
		System.gc();
		Map=new double[SizeX][SizeY];
		NormMap=new double[SizeX][SizeY];
		TimeAxis=new double[SizeX];
		FreqAxis=new double[SizeY];

		ATime=(maxT-minT)/(SizeX-1);
		BTime=minT;
		AFreq=(maxF-minF)/(SizeY-1);
		BFreq=minF;
	}

	public final void setSigmaScale(double Dyst[]) {
		double Scale=(Dyst.length-1)/(maxVal-minVal),OldMinVal=minVal;
		double ftmp,ref[];
		int i,j;

		maxVal=minVal=Map[0][0]*Dyst[(int)(Scale*(Map[0][0]-minVal))];
		for (i=0 ; i<SizeX ; i++)
			for (j=0,ref=Map[i] ; j<SizeY ; j++) {
				ftmp=(ref[j]*=Dyst[(int)(Scale*(ref[j]-OldMinVal))]);
				if (maxVal<ftmp) maxVal=ftmp;
				if (minVal>ftmp) minVal=ftmp;
			}
	}

	public final void setSigmaScale(double alpha, double trans) {
		if (Map==null) return;
		double Scale=(double)(1.0/(maxVal-minVal));
		alpha*=Scale;
		trans=(double)(alpha*minVal-trans);
		maxVal=minVal=(double)(1.0/(1.0+Math.exp(-alpha*Map[0][0]+trans)));
		double ftmp,ref[];
		int i,j;

		for (i=0 ; i<SizeX ; i++)
			for (j=0,ref=Map[i] ; j<SizeY ; j++) {
				ftmp=ref[j]=(double)(1.0/(1.0+Math.exp(-alpha*ref[j]+trans)));
				if (maxVal<ftmp) maxVal=ftmp;
				if (minVal>ftmp) minVal=ftmp;
			}
	}

	public final void setSqrtScale() {
		if (Map==null)
			return;
		maxVal=minVal=(double)Math.sqrt(Map[0][0]);
		double ftmp,ref[];
		int i,j;

		for (i=0 ; i<SizeX ; i++)
			for (j=0,ref=Map[i] ; j<SizeY ; j++) {
				ftmp=ref[j]=(double)Math.sqrt(ref[j]);
				if (maxVal<ftmp) maxVal=ftmp;
				if (minVal>ftmp) minVal=ftmp;
			}
	}

	public final void setLogScale() {
		if (Map==null) return;
		maxVal=minVal=(double)Math.log(1.0F+Map[0][0]);
		double ftmp,ref[];
		int i,j;

		for (i=0 ; i<SizeX ; i++)
			for (j=0,ref=Map[i] ; j<SizeY ; j++) {
				ftmp=ref[j]=(double)Math.log(1.0F+ref[j]);
				if (maxVal<ftmp) maxVal=ftmp;
				if (minVal>ftmp) minVal=ftmp;
			}
	}

	public final void NormScale() {
		if (Map==null || NormMap==null) return;
		double ftmp,ref1[],ref2[];
		int i,j;

		for (i=0 ; i<SizeX ; i++)
			for (j=0,ref1=Map[i],ref2=NormMap[i] ; j<SizeY ; j++) {
				ftmp=ref1[j]=ref2[j];
				if (maxVal<ftmp) maxVal=ftmp;
				if (minVal>ftmp) minVal=ftmp;
			}
	}

	public double getMinVal() {
		return minVal;
	}

	public double getMaxVal() {
		return maxVal;
	}

	private void SetMinMax() {
		if (Map==null) return;
		double ftmp,ref1[],ref2[];
		int i,j;

		for (i=0 ; i<SizeX ; i++)
			for (j=0,ref1=NormMap[i],ref2=Map[i] ; j<SizeY ; j++) {
				ftmp=ref1[j]=ref2[j];
				if (ftmp>maxVal) maxVal=ftmp;
				if (ftmp<minVal) minVal=ftmp;
			}
	}

	private void SetExp(double sig[],double alpha,int trans,
	                    double modulus,int Max)
	{
		double Const=1.65*Math.sqrt(Math.log(modulus/(2.0*EPS))/(PI2M*alpha));

		Start=(int)(trans-Const)-1;
		Stop=(int)(trans+Const)+1;
		if (Start<0)  Start=0;
		if (Stop>Max) Stop=Max;
		if (Start>=Stop) return;
		MakeExpTable(sig,alpha,trans,Start,Stop);
		for (int i=Start ; i<=Stop ; i++) {
			sig[i]*=modulus;
		}
	}

	public void AddAtom(double modulus,int scale,int trans,double ffreq) {
		int i,j,freq;
		double ref[];

		if (scale!=0) {
			double dy=Math.PI/DimBase;
			double alphaTime=4.0*Math.PI/SQR(scale),
			       alphaFreq=4.0*Math.PI*SQR(dy*scale/PI2M);

			alphaTime*=(ATime*ATime);
			trans=(int)((trans-BTime)/ATime);
			alphaFreq*=(AFreq*AFreq);
			freq=(int)((ffreq-BFreq)/AFreq);

			if (scale==DimBase) {
				if (freq<0 || freq>=SizeY) {
					return;
				}
				modulus=SQR(modulus);
				for (i=0 ; i<SizeX ; i++) {
					Map[i][freq]+=modulus;
				}
				return;
			}

			SetExp(TimeAxis,alphaTime,trans,modulus,SizeX-1);
			int TimeStart=Start,TimeStop=Stop;
			SetExp(FreqAxis,alphaFreq,freq,modulus,SizeY-1);
			int FreqStart=Start,FreqStop=Stop;
			double dtmp;

			if (TimeStart<TimeStop && FreqStart<FreqStop)
				for (i=TimeStart ; i<=TimeStop ; i++) {
					dtmp=TimeAxis[i];
					for (j=FreqStart,ref=Map[i] ; j<=FreqStop ; j++) {
						ref[j]+=dtmp*FreqAxis[j];
					}
				}
		} else {
			trans=(int)((trans-BTime)/ATime);
			if (trans<0 || trans>=SizeX) {
				return;
			}
			modulus=SQR(modulus);
			for (i=0,ref=Map[trans] ; i<SizeY ; i++) {
				ref[i]+=modulus;
			}
		}
	}

	private static double SQR(double x) {
		return x*x;
	}

	private static void MakeExpTable(double ExpTab[],double alpha,int trans,
	                                 int start,int stop)
	{
		int left,right,itmp;
		double Factor,OldExp,ConstStep;

		if (start<trans && trans<stop) {
			ExpTab[trans]=OldExp=1.0;
			Factor=Math.exp(-alpha);
			ConstStep=SQR(Factor);

			for (left=trans-1,right=trans+1 ;
			                start<=left && right<=stop ;
			                left--,right++)
			{
				OldExp*=Factor;
				ExpTab[left]=ExpTab[right]=OldExp;
				Factor*=ConstStep;
			}

			if (left>=start)
				for (; start<=left ; left--) {
					ExpTab[left]=OldExp*=Factor;
					Factor*=ConstStep;
				}
			else
				for (; right<=stop; right++) {
					ExpTab[right]=OldExp*=Factor;
					Factor*=ConstStep;
				}
			return;
		}

		ConstStep=Math.exp(-2.0*alpha);
		if (trans>=stop) {
			itmp=trans-stop;
			ExpTab[stop]=OldExp=Math.exp(-alpha*SQR(itmp));
			Factor=Math.exp(-alpha*(double)((itmp << 1)+1));

			for (left=stop-1; start<=left ; left--) {
				ExpTab[left]=OldExp*=Factor;
				Factor*=ConstStep;
			}
		} else {
			itmp=start-trans;
			ExpTab[start]=OldExp=Math.exp(-alpha*SQR(itmp));
			Factor=Math.exp(-alpha*(double)((itmp << 1)+1));

			for (right=start+1; right<=stop ; right++) {
				ExpTab[right]=OldExp*=Factor;
				Factor*=ConstStep;
			}
		}
	}
}
