
package lab.mg.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

import broad.core.annotation.BED;
import broad.core.annotation.GenomicAnnotation;

import lab.mg.DBI.BAMDB;
import lab.mg.DBI.DB;
import lab.mg.DBI.DBFactory;
import lab.mg.IO.Reader;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;
import lab.mg.turing.Utils;

/**
 *  Created on 2013-9-26  
 *  TEST: Pass
 *  
 */

public class TranslateCoordinatesTestBAM extends CommandLineProgram{

private static final String PROGRAM_VERSION = "0.01";
@Usage
public static final String USAGE = "Usage: ";
@Option(doc = "input file", shortName = "i")
public File INPUT;

@Option(doc = "bam file", shortName = "b")
public File BAM;


@Option(doc = "out file", shortName = "o")
public String OUT="stdout";

public static void main(String[] argv) {
	System.exit(new TranslateCoordinatesTestBAM().instanceMain(argv));
}

@Override
protected int doWork() {
	PrintStream out=null;
	// TODO Auto-generated method stub
	try {
		if ("stdout".equalsIgnoreCase(OUT)) {out=System.out;}
		else {
		try {
			out=new PrintStream(new File(OUT));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
//		CloseableIterator t =  TabbedReader.read(INPUT,AnnotationFactoryFactory.StringToFormat(FORMAT), AnnotationFactoryFactory.getFactory(FORMAT)) ;
	CloseableIterator<BED> t = Reader.read(INPUT,"bed");
	DB bam = DBFactory.init(BAM,"bam",new HashMap<String,Object>());
	   while(t.hasNext())
	   {
		   BED a = t.next();
		   System.out.println(a);
		   CloseableIterator<SAMRecord> iter=bam.query(a);
		   while(iter.hasNext())
		   {
			   SAMRecord sam=iter.next();
			   if(Utils.compatibleWithGene(a, sam))
			   {
				   out.println();
				   out.println(sam.getAlignmentStart());
				   out.println(sam.getAlignmentEnd());
				   out.print(Arrays.toString(Utils.translateToGeneCoordinates(sam, a)));
			   }
		   }
		   
	   }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	
	return 0;
}
}
