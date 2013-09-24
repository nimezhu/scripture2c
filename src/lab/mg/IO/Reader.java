
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
 *  @author zhuxp
 *  @version 0.1
 *  @see
 *  Example:
 *  ClosableIterator iter=reader.read(file,"bed");
 *  while(iter.hasNext())
 *  {
 *    System.out.println(iter.next())
 *  }
 *  
 *  It is an IO interface for integrate all the format.
 *  Right now it use AnnotaitonFactoryFactory 
 *  and TabbedReader
 *  For bam file it use SAMFileReader.iterator(); 
 *   
 */



public class Reader {

public static Class StringToClass(String s)
{
	return null;
	
}
	
public static <T> CloseableIterator<T> read(File input,String format) throws IOException
{
	return read(input,format,null);
}


public static <T> CloseableIterator<T> read(File input,String format, String method) throws IOException
 {
	CloseableIterator iter=null;
	if ("BAM".equalsIgnoreCase(format))
	{	
	  iter = new SAMFileReader(input).iterator(); //convert to annotation?
	  
	}
	else  //default : txt; now format becomes type;
	{	
	iter = TabbedReader.read(input,AnnotationFactoryFactory.StringToType(format), AnnotationFactoryFactory.getFactory(format));
	}
	return iter; 
 }
 

public static <T> CloseableIterator<T> read(String filename,String format) throws IOException
 
 {
	 File file=new File(filename);
	 return read(file,format);
 }
 
 
 public static <T> CloseableIterator<T> read(String filename,String format,String method) throws IOException
 
 {
	 File file=new File(filename);
	 return read(file,format,method);
 }
 
 
 
 public static <T> CloseableIterator<T> read(Iterator<T> iter,String format) throws IOException
 {
	 return read(iter,format,null);
	 
 }
 public static <T> CloseableIterator<T> read(Iterator<T> iter,String format,String method) throws IOException
 {
	 return null;
	 /**
	  * To do
	  */
	 
 }
}
