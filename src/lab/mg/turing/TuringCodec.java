package lab.mg.turing;
/**
 *  Created on 2013-3-7  
 */

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;


import net.sf.samtools.util.BinaryCodec;
import net.sf.samtools.util.RuntimeEOFException;
import net.sf.samtools.util.SortingCollection;
public class TuringCodec implements SortingCollection.Codec<TuringCode>{
	static Logger logger = Logger.getLogger(TuringCodec.class.getName());
	private final BinaryCodec binaryCodec = new BinaryCodec();
	@Override
	public TuringCode decode() {
		// TODO Auto-generated method stub
		int tid;
		try {
		 tid=this.binaryCodec.readInt();
        }
        catch (RuntimeEOFException e) {   //VERY IMPORTANT , TO TELL THAT THE FILE IS ENDING
            return null;
        }
		int pos=this.binaryCodec.readInt();
		int bit=this.binaryCodec.readInt();
		int code=this.binaryCodec.readInt();
		double param=this.binaryCodec.readDouble();
		TuringCode a= new TuringCode(tid,pos,bit,code,param);
		return  a;
	}

	@Override
	public void encode(TuringCode a) {
		// TODO Auto-generated method stub
		this.binaryCodec.writeInt(a.getTid());
		this.binaryCodec.writeInt(a.getPos());
		this.binaryCodec.writeInt(a.getBit());
		this.binaryCodec.writeInt(a.getCode());
		this.binaryCodec.writeDouble(a.getParam());
	}

	@Override
	public void setInputStream(InputStream is) {
		// TODO Auto-generated method stub
		this.binaryCodec.setInputStream(is);
		
	}

	@Override
	public void setOutputStream(OutputStream os) {
		// TODO Auto-generated method stub
		this.binaryCodec.setOutputStream(os);
		
	}
	public TuringCodec clone()
	{
		return new TuringCodec();
	}

}

