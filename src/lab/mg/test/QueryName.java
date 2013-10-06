
package lab.mg.test;

import lab.mg.DBI.DBFactory;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.util.CloseableIterator;
import nextgen.core.annotation.Annotation;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import broad.core.annotation.ShortBED;
/**
 *  Created on 2013-10-6  
 */

public class QueryName extends CommandLineProgram{

private static final String PROGRAM_VERSION = "0.01";
@Usage
public static final String USAGE = "Usage: ";
@Option(doc = "data file", shortName = "d")
public File DB;
@Option (doc = "db format", shortName ="e")
public String DBFORMAT;
@Option (doc = "annotation type", shortName = "t")
public String TYPE;
@Option(doc = "query name", shortName = "q")
public String NAME;

public static void main(String[] argv) {
	System.exit(new QueryName().instanceMain(argv));
}

@Override
protected int doWork() {
	HashMap<String,Object> config=new HashMap<String, Object>();
    config.put("type",TYPE);
    lab.mg.DBI.DB dbi=null;
    try {
        dbi = DBFactory.init(DB,DBFORMAT,config);
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+DB.getAbsolutePath()+".sqlitedb");
        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("select * from annotation where name==\""+NAME+"\";"); 
        while (rs.next()) {
            String chr=rs.getString("chr");
            String name=rs.getString("name");
            Integer start=rs.getInt("start");
            Integer end=rs.getInt("end");
            System.out.println(String.format("%s\t%d\t%d\t%s", chr,start,end,name));
            ShortBED shortbed= new ShortBED(name,chr,start,end);
            CloseableIterator<Annotation> iter = dbi.query(shortbed);
            while(iter.hasNext())
            {
            	Annotation a=iter.next();
            	if(a.getName().equalsIgnoreCase(name))
            	{
            		System.out.println(a.toBED());
            	}
            }
            		
            
        }
        
    
    } catch (Exception e1) {
        e1.printStackTrace();
    }
	return 0;
}
}
