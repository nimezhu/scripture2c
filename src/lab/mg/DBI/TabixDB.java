
package lab.mg.DBI;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.broad.tribble.readers.TabixReader;
import org.broad.tribble.readers.TabixReader.Iterator;

import broad.core.annotation.AnnotationFactory;
import broad.core.annotation.AnnotationFactoryFactory;




import net.sf.samtools.util.CloseableIterator;
import nextgen.core.annotation.Annotation;


/**
 *  Created on 2013-9-18 
 *  @author zhuxp
 *  @version $REVISION
 *  
 *  @see
 *  INTRODUCTION:
 *  
 *  How to initialize database:
 *  
 *  Please use interface in DBFactory.
 *  
 *  HashMap config=new HashMap();
 *  config.put("type","bed");
 *  DB db = DBFacotry.init(file,"tabix",config);
 *  
 *  Query Database:
 *  
 *  Annotation a;
 *  CloseableIterator iter=db.query(a);
 *  while(iter.hasNext())
 *  {
 *     print iter.next();
 *  }
 *  
 *  
 *  Config Options Update:
 *  "type":"bed" or any format in AnnotationFactoryFactory.getFactory() 
 *         "bed" "shortbed" "gff" "bedgraph" 
 *  BUG:
 *  
 *  
 *  TODO: 
 */

public class TabixDB<T> implements DB<T> {

	
	private TabixReader data;
	private HashMap<String,Object> config;
	private String type; //annotationType
	private AnnotationFactory factory; //annotationFacotry
	public TabixDB(File file, String type) throws IOException
	{
		this.data= new TabixReader(file.getAbsolutePath());
		setType(type);
	}
	
	public TabixDB(String filename,String type) throws IOException
	{
		this.data = new TabixReader(filename);
		setType(type);
	}
	
	public TabixDB(String filename,HashMap config) throws IOException
	{
		this.data = new TabixReader(filename);
		setConfig(config);
	}	
	
	public TabixDB(File file, HashMap config) throws IOException {
		this.data = new TabixReader(file.getAbsolutePath());
		setConfig(config);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type=type;
		this.factory=AnnotationFactoryFactory.getFactory(type);
	}

	@Override
	public CloseableIterator<T> query(Annotation a) {
		
		Iterator iter= data.query(a.toUCSC());
		
		return new SimpleCloseableIterator(iter,factory);
	}

	

	@Override
	public CloseableIterator<T> iterator() {
		
		return new TabixFileCloseableIterator<T>(this.data, this.factory);
	}
	
	@Override
	public void setConfig(HashMap a) {
		// TODO Auto-generated method stub
		this.config=a;
		if (config.containsKey("type")) 
		{
			setType((String)config.get("type"));
		}
	}
	@Override
	public HashMap getConfig() {
		// TODO Auto-generated method stub
		return this.config;
	}
	
	

}


class SimpleCloseableIterator<T> implements CloseableIterator<T>
{
	private Iterator iter;
	private String buffer;
	@SuppressWarnings("rawtypes")
	private AnnotationFactory factory;
	public SimpleCloseableIterator(Iterator iter, @SuppressWarnings("rawtypes") AnnotationFactory f)
	{
		this.iter=iter;
		this.factory=f;
		advance();
	}
	
	
	public void advance() 
	{
		try {
			buffer=iter.next();
		} catch (IOException e) {
			// e.printStackTrace();
			buffer=null;
		}
	}
	@Override
	public boolean hasNext() {
		if (buffer!=null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public T next() {
		String a = buffer;
		advance();
		return (T) factory.create(a.split("\t"));
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}


	
}


	
class TabixFileCloseableIterator<T> implements CloseableIterator<T>
{
	private TabixReader iter;
	private String buffer;
	@SuppressWarnings("rawtypes")
	private AnnotationFactory factory;
	public TabixFileCloseableIterator(TabixReader iter, @SuppressWarnings("rawtypes") AnnotationFactory f)
	{
		this.iter=iter;
		this.factory=f;
		advance();
	}
	
	
	public void advance() 
	{
		try {
			buffer=iter.readLine();
		} catch (IOException e) {
			// e.printStackTrace();
			buffer=null;
		}
		
	}
	@Override
	public boolean hasNext() {
		if (buffer!=null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public T next() {
		String a = buffer;
		advance();
		return (T) factory.create(a.split("\t"));
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	
}	
