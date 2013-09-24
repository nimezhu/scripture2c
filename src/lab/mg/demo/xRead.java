
package lab.mg.demo;

import java.io.File;
import java.io.IOException;

import lab.mg.IO.Reader;


import broad.core.annotation.AnnotationFactoryFactory;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.util.CloseableIterator;
import nextgen.core.general.TabbedReader;

/**
 *  Created on 2013-9-18
 *  
 *  Simple Demonstration of how to use lab.mg.IO.reader
 *  
 */

public class xRead extends CommandLineProgram {

private static final String PROGRAM_VERSION = "0.01";
@Usage
public static final String USAGE = "Usage: ";
@Option(doc = "input file", shortName = "i")
public File INPUT;
@Option(doc = "input file format", shortName ="f")
public String FORMAT;

public static void main(String[] argv) {
	System.exit(new xRead().instanceMain(argv));
}

@Override
protected int doWork() {
	// TODO Auto-generated method stub
	
	try {
//		CloseableIterator t =  TabbedReader.read(INPUT,AnnotationFactoryFactory.StringToFormat(FORMAT), AnnotationFactoryFactory.getFactory(FORMAT)) ;
	CloseableIterator t = Reader.read(INPUT,FORMAT);	
	   while(t.hasNext())
	   {
		   System.out.println(t.next());
	   }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	
	
	
	return 0;
}
	

}
