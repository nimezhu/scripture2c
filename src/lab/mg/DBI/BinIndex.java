
package lab.mg.DBI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


import net.sf.samtools.util.CloseableIterator;
import nextgen.core.annotation.Annotation;

import org.apache.log4j.Logger;

/**
 *  Created on 2013-9-22  
 *  
 *  TODO: need to implement iterator()!.
 */

public class BinIndex<T extends Annotation> implements DB<T> {
	/**
	 * Bin Index Numbers
	 */
	private static final Logger logger = Logger.getLogger(BinIndex.class);
	
	public static int binOffsets[]={512+64+8+1,64+8+1,8+1,1,0};
	public static int binFirstShift=17;
	public static int binNextShift=3;
	public static int binLength=4096+512+64+8+1;
	private HashMap<String,Object> config;
	
	/**
	 * Data
	 */
	private HashMap<String,ArrayList<ArrayList<T>>> data;
	
	private static Integer rangeToBin(int start,int end)
	{
		int startBin=start;
	    int endBin=end-1;
	    startBin >>= binFirstShift;
	    endBin >>= binFirstShift;
	    for(int i=0;i<binOffsets.length;i++)
	    {
	    	if (startBin==endBin)
	    	{
	    		return binOffsets[i]+startBin;
	    	}
	    	startBin >>= binNextShift;
	        endBin >>= binNextShift;
	    }
	    logger.error(String.format("from %d to %d  is out of binindex range",start,end));       
		return null;
	}
	
	
	
	private static Integer[] rangeToOverlapBins(int start,int end)
	{
		ArrayList<Integer> retv=new ArrayList<Integer>();
		int startBin=start;
		int endBin=end-1;
		startBin >>= binFirstShift;
		endBin >>= binFirstShift;
		for(int i=0;i<binOffsets.length;i++)
		{
			for(int j=startBin;j<=endBin;j++)
			{
				retv.add(j+binOffsets[i]);
			}
			startBin >>= binNextShift;
			endBin >>= binNextShift;
		}
		return retv.toArray(new Integer[retv.size()]);
	}



	public BinIndex() {
		super();
		data= new HashMap<String,ArrayList<ArrayList<T>>>();
	}
	public BinIndex(Iterator<T> iter)
	{
		super();
		data= new HashMap<String,ArrayList<ArrayList<T>>>();
		read(iter);
	}
	public BinIndex(Iterable<T> handle)
	{
		super();
		data= new HashMap<String,ArrayList<ArrayList<T>>>();
		read(handle);
	}
	public BinIndex(Iterator<T> iter, HashMap c)
	{
		super();
		data= new HashMap<String,ArrayList<ArrayList<T>>>();
		config=c;
		read(iter);
	}
	
	public BinIndex(Iterable<T> handle, HashMap c)
	{
		super();
		data= new HashMap<String,ArrayList<ArrayList<T>>>();
		config=c;
		read(handle);
	}
	
	public void add( T annotation)
	{
		/*
		 	a=bed
            bin=binindex.range2bin(a.start,a.stop)
            if not self.data.has_key(a.chr):
                self.data[a.chr]=[[] for row in range(binindex.binLength)]
            self.data[a.chr][bin].append(a)
		 */
		int bin=rangeToBin(annotation.getStart(),annotation.getEnd());
		
		if (!data.containsKey(annotation.getChr()))
		{
			ArrayList<ArrayList<T>> a=new ArrayList<ArrayList<T>>();
			for(int i=0;i<binLength;i++)
			{
			ArrayList<T> b= new ArrayList<T>();
			a.add(b);
			}
			data.put(annotation.getChr(), a);
		}
		data.get(annotation.getChr()).get(bin).add(annotation);
	}
	/**
	 * reading into binindex structrue
	 * @param iter
	 */
	
	public void read(Iterator<T> iter)
	{
		while(iter.hasNext())
		{
		  this.add(iter.next());	
		}
		
	}
	
	public void read(Iterable<T> handle)
	{
		this.read(handle.iterator());
	}
	
	
	public CloseableIterator<T> query(Annotation annotation)
	{	//TODO
		/*
		 * if not self.data.has_key(bed.chr):
                 raise StopIteration
            D=self.data[bed.chr]
            for bin in binindex.iter_range_overlap_bins(bed.start,bed.stop):
                for f in D[bin]:
                    if f.start < bed.stop and f.stop > bed.start:
                        yield f
            raise StopIteration
		 */
	Query a = new Query(annotation);
		return a;
		
	}



	@Override
	public CloseableIterator<T> iterator() {
		// TODO Auto-generated method stub!! TODO!!!
		return null;
	}



	@Override
	public void setConfig(HashMap a) {
		config=a;
	}



	@Override
	public HashMap getConfig() {
		return config;
	};	
	
	

	private class Query implements CloseableIterator<T>
	{

	private Integer bins[];
	private T curr;
	private ArrayList<ArrayList<T>> chrData;
	private int iBin;
	private int iList;
	private int start;
	private int end;
	public Query(Annotation a)
	{
	  if (!data.containsKey(a.getChr())) {curr=null;}
	  else
	  {
		  chrData=data.get(a.getChr());
		  start=a.getStart();
		  end=a.getEnd();
		  bins=rangeToOverlapBins(start,end);
		  iBin=0;
		  iList=-1; //iList++ in first.
		  advance();
	  }
	}
	
	private void advance()
	{
		T a=null;
		while(true)
		{
		 a=readNextInBin();
		 
		if (a==null) break;
		if (a.getStart() < end && a.getEnd() > start) 
			{
			break;
			}
		}
		curr=a;
		
	}
	private T readNextInBin() //read next in bin, no need to be overlapping
	{
		iList++;
		if (iBin >= bins.length) return null;

		while(iList>chrData.get(bins[iBin]).size()-1)
		{
			iBin++;
			if (iBin >= bins.length) return null;
		    iList=0;	
		}
		return chrData.get(bins[iBin]).get(iList);
		
		
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		if (curr==null) return false;
		else return true;
		    
	}

	@Override
	public T next() {
		// TODO Auto-generated method stub
		T buffer=curr; //remember the handle instead clone(); 
		advance();
		return buffer; 
	}

	@Override
	public void remove() {
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	}


}


	
 




