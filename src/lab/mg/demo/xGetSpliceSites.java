
package lab.mg.demo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import lab.mg.DBI.DB;
import lab.mg.DBI.DBFactory;
import lab.mg.IO.Reader;


import broad.core.annotation.AnnotationFactoryFactory;
import broad.core.datastructures.Pair;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;
import nextgen.core.general.TabbedReader;

/**
 *  Created on 2013-9-18
 *  
 *  Simple Demonstration of how to use lab.mg.DBI.DB
 *  Now DB is Iterable.
 *  Try This
 *  
 *  DB db = DBFactory.init(INPUT,FORMAT,config);
 *  for (Object i : db) 
 *  {
 *   // do something
 *  }
 *  
 */

public class xGetSpliceSites extends CommandLineProgram {

private static final String PROGRAM_VERSION = "0.01";
@Usage
public static final String USAGE = "Usage: ";
@Option(doc = "input file", shortName = "i")
public File INPUT;
@Option(doc =" if first mate is the forward strand", shortName = "f")
public Boolean isFirst = false;

public static void main(String[] argv) {
	System.exit(new xGetSpliceSites().instanceMain(argv));
}

@Override
protected int doWork() {
	// TODO Auto-generated method stub
	
	HashMap<String,Object> config = new HashMap<String,Object>();
	HashMap<String,Integer> donorSites = new HashMap<String,Integer>();
	HashMap<String,Integer> acceptorSites = new HashMap<String,Integer>();
	try {
//		CloseableIterator t =  TabbedReader.read(INPUT,AnnotationFactoryFactory.StringToFormat(FORMAT), AnnotationFactoryFactory.getFactory(FORMAT)) ;
	DB<SAMRecord> t = DBFactory.init(INPUT,"bam",config);	
	  
	for(SAMRecord i: t)
	{
		int blockSize=i.getAlignmentBlocks().size();
	    if (blockSize<=1) continue;
	    
	    //STRAND PART
	    int  strand=1; //strand positive. if strand==-1  it is negative;
	    
	    if (isFirst)
	    {
	    	if(i.getFirstOfPairFlag())
	    	{
	    		if (i.getReadNegativeStrandFlag()) strand=-1;
	    		else strand=1;
	    	}
	    	else
	    	{
	    		if (i.getReadNegativeStrandFlag()) strand=1;
	    		else strand=-1;
	    	}
	    }
	    else
	    {
	    	if(i.getFirstOfPairFlag())
	    	{
	    		if (i.getReadNegativeStrandFlag()) strand=1;
	    		else strand=-1;
	    	}
	    	else
	    	{
	    		if (i.getReadNegativeStrandFlag()) strand=-1;
	    		else strand=1;
	    	}
	    }
	    //END OF ASSIGN STRAND
	    
	    //POSITIVE STRAND
	    if (strand==1)
	    {
	    	
	        int j=0;
	    	for(AlignmentBlock block:i.getAlignmentBlocks())
			{
				j++;
				if (j!=1)
				{
				//add start	
					String key=i.getReferenceName()+"\t"+String.valueOf(block.getReferenceStart()-3)+"\t"+String.valueOf(block.getReferenceStart()-1)+"\t+";
					if (acceptorSites.containsKey(key))
					{
						acceptorSites.put(key, acceptorSites.get(key)+1);
					}
					else
					{
						acceptorSites.put(key, 1);
					}
				}		
				if (j!=blockSize)
				{
					//add end
					int donorStart=block.getReferenceStart()-1+block.getLength();
					String key=i.getReferenceName()+"\t"+String.valueOf(donorStart)+"\t"+String.valueOf(donorStart+2)+"\t+";
					if (donorSites.containsKey(key))
					{
						donorSites.put(key, donorSites.get(key)+1);
					}
					else
					{
						donorSites.put(key, 1);
					}
				}
	    }
	    }
	    //NEGATIVE STRAND
	    else
	    {
	    	 int j=0;
		    	for(AlignmentBlock block:i.getAlignmentBlocks())
				{
					j++;
					if (j!=1)
					{
					//add start	
						String key=i.getReferenceName()+"\t"+String.valueOf(block.getReferenceStart()-3)+"\t"+String.valueOf(block.getReferenceStart()-1)+"\t-";
						if (donorSites.containsKey(key))
						{
							donorSites.put(key, donorSites.get(key)+1);
						}
						else
						{
							donorSites.put(key, 1);
						}
					}		
					if (j!=blockSize)
					{
						//add end
						int acceptorStart=block.getReferenceStart()-1+block.getLength();
						String key=i.getReferenceName()+"\t"+String.valueOf(acceptorStart)+"\t"+String.valueOf(acceptorStart+2)+"\t-";
						if (acceptorSites.containsKey(key))
						{
							acceptorSites.put(key, acceptorSites.get(key)+1);
						}
						else
						{
							acceptorSites.put(key, 1);
						}
					}
		       }
	    }
	}
		
		
	   
	
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	int j=0;
	for(String key:donorSites.keySet())
	{
		j++;
		String[] a=key.split("\t");
		
		System.out.println(a[0]+"\t"+a[1]+"\t"+a[2]+"\tDonorSite_"+String.valueOf(j)+"\t"+String.valueOf(donorSites.get(key))+"\t"+a[3]);
	}
	j=0;
	for(String key:acceptorSites.keySet())
	{
		j++;
		String[] a=key.split("\t");
		System.out.println(a[0]+"\t"+a[1]+"\t"+a[2]+"\tAcceptorSite_"+String.valueOf(j)+"\t"+String.valueOf(acceptorSites.get(key))+"\t"+a[3]);
	}
	
	
	return 0;
}
	

}
