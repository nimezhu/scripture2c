
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

public class xIterateBAM2 extends CommandLineProgram {

private static final String PROGRAM_VERSION = "0.01";
@Usage
public static final String USAGE = "Usage: ";
@Option(doc = "input file", shortName = "i")
public File INPUT;

public static void main(String[] argv) {
	System.exit(new xIterateBAM2().instanceMain(argv));
}

@Override
protected int doWork() {
	// TODO Auto-generated method stub
	
	HashMap<String,Object> config = new HashMap<String,Object>();
	try {
//		CloseableIterator t =  TabbedReader.read(INPUT,AnnotationFactoryFactory.StringToFormat(FORMAT), AnnotationFactoryFactory.getFactory(FORMAT)) ;
	DB t = DBFactory.init(INPUT,"bam2",config);	
	  for(Object i: t)
	   {
		  Pair<SAMRecord> b= (Pair<SAMRecord>)i;
		  
		   System.out.print(b.getValue1().getReferenceName());
		   System.out.print("\t");
		   System.out.print(b.getValue1().getAlignmentStart());
		   System.out.print("\t");
		   System.out.println(b.getValue2().getAlignmentEnd());
	   }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	
	
	
	return 0;
}
	

}
