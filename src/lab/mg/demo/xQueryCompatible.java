
package lab.mg.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

import broad.core.annotation.BED;
import broad.core.datastructures.Pair;

import lab.mg.DBI.DBFactory;
import lab.mg.DBI.DB;
import lab.mg.IO.Reader;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;
import nextgen.core.annotation.Annotation;
/**
 *  Created on 2013-9-18  
 *  Demonstration Program for using API DBI.init() and dbi.query()
 *  
 *  @author zhuxp
 *  
 *  @see
 *  
 *  
 */
public class xQueryCompatible extends CommandLineProgram {
	
private static final String PROGRAM_VERSION = "0.01";
@Usage
public static final String USAGE = "Usage: ";
@Option(doc = "input file", shortName = "i")
public File INPUT;
@Option(doc = "input file  type [bed or gff]", shortName = "j")
public String INPUT_TYPE="bed"; //so far not support bam because SAMRecord is not an Annotation Class;
                                // support gzip file due to change in TabbedReader.
@Option(doc = "output file", shortName = "o")
public String OUT="stdout";
@Option(doc = "database file", shortName="d")
public File DB_FILE;
@Option(doc = "annotation type [bed or gff ...] interface", shortName="t")
public String TYPE="bed";


public static void main(String[] argv) {
	System.exit(new xQueryCompatible().instanceMain(argv));
}

@Override
protected int doWork() {
	// TODO Auto-generated method stub
	PrintStream out=null;
	System.out.println("");
	String DB_FORMAT="bam";
	String QR_METHOD="fetch";// for future interface 
	
	/*
	 * output template
	 */
	if ("stdout".equalsIgnoreCase(OUT)) {out=System.out;}
	else {
	try {
		out=new PrintStream(new File(OUT));
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}}
	
	/*
	 * config template
	 */
	HashMap<String,Object> config=new HashMap<String, Object>();
	config.put("type",TYPE);
	config.put("method",QR_METHOD);
	
	
	/*
	 * initialize DB
	 */
	DB<SAMRecord> dbi=null;
	try {
		dbi = DBFactory.init(DB_FILE,DB_FORMAT,config);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	/*
	 * Query
	 */
	try {
	CloseableIterator t = Reader.read(INPUT,INPUT_TYPE);	
	   while(t.hasNext())
	   {
		   BED a=(BED)t.next();
		   out.print("QR\t");
		   out.println(a);
		   CloseableIterator<SAMRecord> iter=dbi.query((Annotation)a);
		   while(iter.hasNext())
		   {
			   SAMRecord sam = iter.next();
			   
			   if (lab.mg.turing.Utils.compatibleWithGene(a, sam))
			   {
				   out.println("CT\t");
				   out.println(Arrays.toString(lab.mg.turing.Utils.translateToGeneCoordinates(sam, a)));
				   out.print(sam.getSAMString());
			   }
			   else
			   {
				   out.print("NC\t");
				   out.print(sam.getSAMString());
			   }
		   }
		   iter.close();
		   out.println("//");
	   }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	out.close();
	return 0;
}


}
