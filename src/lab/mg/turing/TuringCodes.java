package lab.mg.turing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;
import net.sf.samtools.util.SortingCollection;
import nextgen.core.annotation.Annotation;

/**
 *  Created on 2013-3-7  
 *  Revised on 2013-9-25
 */
public class TuringCodes implements Iterable<TuringCode>{
	static Logger logger = Logger.getLogger(TuringCodes.class.getName());
	private HashMap<String,Integer> chr2tid;
	//private ArrayList<String> tid2chr;
	private SortingCollection<TuringCode> sortingArray;
	private static int MAX_NUM=500000;
	private static File tmpDir = new File(System.getProperty("java.io.tmpdir"));
	
	
	//private TuringState turingState;
	int dataCode=0;
	
	
    public TuringCodes(HashMap<String,Integer> chr2tid) {
		super();
		this.chr2tid=chr2tid;
		this.sortingArray = SortingCollection.newInstance(TuringCode.class, new TuringCodec(), new TuringCodeComparator(), MAX_NUM , tmpDir);
	}
    
    /**
     * only one chromosome. ignore tid;
     */
    /*
    public TuringCodes()
    {
    	super();
    	HashMap<String,Integer> chr2tid= new HashMap<String,Integer>();
    	chr2tid.put("chr", 0); //only one chromosome
    	this.chr2tid=chr2tid;
		this.sortingArray = SortingCollection.newInstance(TuringCode.class, new TuringCodec(), new TuringCodeComparator(), MAX_NUM , tmpDir);
    }
	*/
	public TuringCodes(String chr)
    {
    	super();
    	HashMap<String,Integer> chr2tid= new HashMap<String,Integer>();
    	chr2tid.put(chr, 0); //only one chromosome
    	this.chr2tid=chr2tid;
		this.sortingArray = SortingCollection.newInstance(TuringCode.class, new TuringCodec(), new TuringCodeComparator(), MAX_NUM , tmpDir);
    }
	
	public void add(Iterator<? extends Annotation> iter)
	{
		logger.info("reading Iterator to array " + iter);
		add(iter,dataCode);
		dataCode+=1;
		
	}
	public void add(Iterator<? extends Annotation> iter, int dataCode)
	{
		
		while (iter.hasNext())
		{
			this.add(iter.next(),dataCode);
		}
	}
	
	public void add(Annotation a, int dataCode)
	{
		
		 TuringCode  start=new TuringCode(chr2tid.get(a.getChr()),a.getStart(),TuringCodeBook.START, dataCode);
		 TuringCode  stop= new TuringCode(chr2tid.get(a.getChr()),a.getEnd(),TuringCodeBook.END, dataCode);
		 //improve for blocks!
		 sortingArray.add(start);
		 sortingArray.add(stop);
		 List<? extends Annotation> blocks = a.getBlocks();
		 for(int i=0; i < blocks.size(); i++)
		 {
			 sortingArray.add(new TuringCode(chr2tid.get(a.getChr()),blocks.get(i).getStart(),TuringCodeBook.BLOCK_START, dataCode));
			 sortingArray.add(new TuringCode(chr2tid.get(a.getChr()),blocks.get(i).getEnd(),TuringCodeBook.BLOCK_END, dataCode));
		 }
	}
	
    public void add(SAMRecord a, int dataCode)
	{
		
		 TuringCode  start=new TuringCode(chr2tid.get(a.getReferenceName()),a.getAlignmentStart()-1,TuringCodeBook.START, dataCode);
		 TuringCode  stop= new TuringCode(chr2tid.get(a.getReferenceName()),a.getAlignmentEnd(),TuringCodeBook.END, dataCode);
		 //improve for blocks!
		 sortingArray.add(start);
		 sortingArray.add(stop);
		 List<AlignmentBlock> blocks = a.getAlignmentBlocks();
		 for(int i=0; i < blocks.size(); i++)
		 {
			 sortingArray.add(new TuringCode(chr2tid.get(a.getReferenceName()),blocks.get(i).getReferenceStart()-1,TuringCodeBook.BLOCK_START, dataCode));
			 sortingArray.add(new TuringCode(chr2tid.get(a.getReferenceName()),blocks.get(i).getReferenceStart()-1+blocks.get(i).getLength(),TuringCodeBook.BLOCK_END, dataCode));
		 }
	}
	
	
	public void add(TuringCode a)
	{
		sortingArray.add(a);
	}
	public CloseableIterator<TuringCode> iterator()
	{
		//sortingArray.doneAdding();
		return sortingArray.iterator();
		
	}
	


}
