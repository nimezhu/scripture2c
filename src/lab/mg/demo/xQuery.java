
package lab.mg.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import lab.mg.DBI.DBFactory;
import lab.mg.DBI.DB;
import lab.mg.IO.reader;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;
import nextgen.core.annotation.Annotation;
/**
 *  Created on 2013-9-18  
 */
public class xQuery extends CommandLineProgram {
	
private static final String PROGRAM_VERSION = "0.01";
@Usage
public static final String USAGE = "Usage: ";
@Option(doc = "input file", shortName = "i")
public File INPUT;
@Option(doc = "input file format", shortName = "j")
public String INPUT_FORMAT="guess";

@Option(doc = "output file", shortName = "o")
public String OUT="stdout";
@Option(doc = "database file", shortName="d")
public File DB_FILE;
@Option(doc = "database format [tabix bam or bigwig or 2bit or txt]", shortName="e")
public String DB_FORMAT="txt";
@Option(doc = "query method interface", shortName="m")
public String QR_METHOD=null;
@Option(doc = "annotation type [bed or vcf or gff ...] interface", shortName="t")
public String TYPE="bed";

public static void main(String[] argv) {
	System.exit(new xQuery().instanceMain(argv));
}

@Override
protected int doWork() {
	// TODO Auto-generated method stub
	PrintStream out=null;
	System.out.println("");
	if ("stdout".equalsIgnoreCase(OUT)) {out=System.out;}
	else {
	try {
		out=new PrintStream(new File(OUT));
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}}
	HashMap<String,Object> config=new HashMap<String, Object>();
	config.put("type",TYPE);
	config.put("method",QR_METHOD);
	
	
	DB dbi=null;
	try {
		dbi = DBFactory.init(DB_FILE,DB_FORMAT,config);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	try {
//		CloseableIterator t =  TabbedReader.read(INPUT,AnnotationFactoryFactory.StringToFormat(FORMAT), AnnotationFactoryFactory.getFactory(FORMAT)) ;
	CloseableIterator t = reader.read(INPUT,INPUT_FORMAT);	
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
