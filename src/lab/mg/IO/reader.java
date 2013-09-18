
package lab.mg.IO;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import broad.core.annotation.AnnotationFactoryFactory;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.util.CloseableIterator;
import nextgen.core.general.TabbedReader;

/**
 *  Created on 2013-9-18  
 */



public class reader {
public Class StringToClass(String s)
{
	return null;
	
}
	
public static <T> CloseableIterator<T> read(File input,String format) throws IOException
 {
	CloseableIterator iter=null;
	if ("BAM".equalsIgnoreCase(format))
	{
	  iter = new SAMFileReader(input).iterator();
	}
	else
	{	
	iter = TabbedReader.read(input,AnnotationFactoryFactory.StringToFormat(format), AnnotationFactoryFactory.getFactory(format));
	}
	return iter; 
 }
 public static <T> CloseableIterator<T> read(String filename,String format) throws IOException
 
 {
	 File file=new File(filename);
	 return read(file,format);
 }
 
 public static <T> CloseableIterator<T> read(Iterator<T> iter,String format) throws IOException
 {
	 return null;
	 /**
	  * To do
	  */
	 
 }
}
