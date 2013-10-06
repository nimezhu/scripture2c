
package lab.mg.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import lab.mg.IO.Reader;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.util.CloseableIterator;
import nextgen.core.annotation.Annotation;

/**
 *  Created on 2013-10-6  
 */

public class IndexName extends CommandLineProgram{

private static final String PROGRAM_VERSION = "0.01";
@Usage
public static final String USAGE = "Usage: ";
@Option(doc = "input file", shortName = "i")
public static File INPUT;
@Option(doc = "annotation type file", shortName = "j")
public static String TYPE="bed";

public static void main(String[] argv) {
	System.exit(new IndexName().instanceMain(argv));
	
}

@Override
protected int doWork() {
	// TODO Auto-generated method stub
	try {
	Class.forName("org.sqlite.JDBC");
    Connection conn = DriverManager.getConnection("jdbc:sqlite:"+INPUT.getAbsolutePath()+".sqlitedb");
    Statement stat = conn.createStatement();
    stat.executeUpdate("drop table if exists annotation;");
    stat.executeUpdate("create table annotation (name, chr, start, end);");
    PreparedStatement prep = conn.prepareStatement(
        "insert into annotation values (?, ?, ?, ?);");
	
	       CloseableIterator<Annotation> t = Reader.read(INPUT,TYPE); 
	       while(t.hasNext())
	       {
	    	   Annotation a= t.next();
	    	   prep.setString(1, a.getName());
	           prep.setString(2, a.getChr());
	           prep.setInt(3, a.getStart());
	           prep.setInt(4, a.getEnd());
	           prep.addBatch();
	       }
	       prep.executeBatch();
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
    
    
    return 0;
}
}
