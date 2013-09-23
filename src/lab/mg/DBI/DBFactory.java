
package lab.mg.DBI;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import lab.mg.IO.reader;

/**
 *  Created on 2013-9-18  
 *  
 *  DBI.init(file,"format",method)
 *  or 
 *  DBI.init(list,"format",method) TO DO 
 *  or
 *  DBI.init(iterator,"format",method) 
 *  
 *  
 *  Most Important Interface !
 */

public class DBFactory {

	public static DB init(File file,String format) throws IOException
	{
		
	 return init(file,format,null);	
	}
	public static DB init(File file,String format, HashMap config) throws IOException
	{
		if ("bam".equalsIgnoreCase(format))
		{
		return new BAMDB(file,config);	
		}
		else if ("bam2".equalsIgnoreCase(format))
		{
			return new BAM2DB(file,config);
		}
		else if ("tabix".equalsIgnoreCase(format))
		{
		return new TabixDB(file,config);
		}
		else if ("txt".equalsIgnoreCase(format))
		{
			
			return new BinIndex(reader.read(file,(String)config.get("type")),config);
		}
		return null;
	}
	
	
}


