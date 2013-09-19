
package lab.mg.DBI;

import java.util.HashMap;

import net.sf.samtools.util.CloseableIterator;
import nextgen.core.annotation.Annotation;

/**
 *  Created on 2013-9-18 
 *  
 *   
 *   DB Interface
 *   
 *   config(HashMap<String,Object>)
 *	 config ( annoationType, annotationFactory, queryMethod )
 *   init() (load more data )
 *   add()? ( merging data ? meta db?)
 */

public interface DB<T> extends Iterable<T>{
	void setConfig(HashMap a);
	HashMap getConfig();
	CloseableIterator<T> query(Annotation a);

}
