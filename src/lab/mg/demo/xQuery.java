
package lab.mg.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

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
public class xQuery extends CommandLineProgram {
	
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
@Option(doc = "database format [tabix bam or txt or bam2(for paired end) ]", shortName="e")
public String DB_FORMAT="txt";
@Option(doc = "query method interface [for future interface]", shortName="m")
public String QR_METHOD="fetch";
@Option(doc = "annotation type [bed or gff ...] interface", shortName="t")
public String TYPE="bed";

public static void main(String[] argv) {
	System.exit(new xQuery().instanceMain(argv));
}

@Override
protected int doWork() {
	// TODO Auto-generated method stub
	PrintStream out=null;
	System.out.println("");
	
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
	DB dbi=null;
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
		   Object a=t.next();
		   out.print("QR\t");
		   out.println(a);
		   CloseableIterator iter=dbi.query((Annotation)a);
		   while(iter.hasNext())
		   {
			   out.print("HT\t");
			   out.println(iter.next());
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
