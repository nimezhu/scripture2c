
package lag.mg.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import broad.core.annotation.BED;
import broad.core.annotation.GenomicAnnotation;

import lab.mg.IO.Reader;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.util.CloseableIterator;
import lab.mg.turing.Utils;

/**
 *  Created on 2013-9-26  
 *  TEST: Pass
 *  
 */

public class TranslateCoordinates extends CommandLineProgram{

private static final String PROGRAM_VERSION = "0.01";
@Usage
public static final String USAGE = "Usage: ";
@Option(doc = "input file", shortName = "i")
public File INPUT;

public static void main(String[] argv) {
	System.exit(new TranslateCoordinates().instanceMain(argv));
}

@Override
protected int doWork() {
	// TODO Auto-generated method stub
	try {
//		CloseableIterator t =  TabbedReader.read(INPUT,AnnotationFactoryFactory.StringToFormat(FORMAT), AnnotationFactoryFactory.getFactory(FORMAT)) ;
	CloseableIterator<BED> t = Reader.read(INPUT,"bed");	
	   while(t.hasNext())
	   {
		   BED a = t.next();
		   System.out.println(a);
		   System.out.println(Arrays.toString(Utils.translateToGeneCoordinates(a,a)));
		   for(GenomicAnnotation b : a.getBlocks())
		   {
		   //System.out.println(b.toBED());
			   
		   System.out.println(Arrays.toString(Utils.translateToGeneCoordinates(new BED(b.toBED().split("\t")),a)));
		   }
		   
	   }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	
	return 0;
}
}
