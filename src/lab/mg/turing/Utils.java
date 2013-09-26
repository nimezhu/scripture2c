
package lab.mg.turing;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import net.sf.samtools.SAMRecord;
import broad.core.annotation.BED;
import broad.core.annotation.ShortBED;
import broad.core.datastructures.Pair;

/**
 *  Created on 2013-9-25  
 */

public class Utils {
	private static final Logger logger = Logger.getLogger(Utils.class);
	
	public static boolean compatibleWithGene(BED gene, Pair<SAMRecord> pair)
	{
		return compatibleWithGene(gene, pair.getValue1()) && compatibleWithGene(gene, pair.getValue2());
		
	}
	public static boolean compatibleWithGene(BED gene, SAMRecord sam)
	{
		logger.setLevel(Level.DEBUG);
		
		if (!gene.getChr().equalsIgnoreCase(sam.getReferenceName())) return false; // if not in same chromosome
		
		
		// TODO : add Strand Filter
		// IF NOT SAME STRAND : Return false;
		
		TuringCodes  turingCodes=new TuringCodes(gene.getChr());
		turingCodes.add(gene,1);
		turingCodes.add(sam,2);
		TuringState state = new  TuringState(0,0,new int[4]);
		/**
		 *   A SIMPLE Compatible Turing Machine First Try
		 *   tid: useless
		 *   pos: useless
		 *   
		 *   registers[4];
		 *      REGISTERS[0]: LAST_GENE POS
		 *      REGISTERS[1]: GENE ON OR OFF
		 *      
		 *      REGISTERS[2]: LAST READS POS
		 *      REGISTERS[3]: READS ON OR OFF
		 *       
		 *  Priorty: gene > read
		 *           end > block_end
		 *           
		 *  Version: TEST.
		 *  
		 *  
		 */
		for(TuringCode i : turingCodes)
		{
			logger.debug(i);
		switch(i.getCode())
		{//CODE
		
		case(1): //CODE IS GENE
			    logger.debug("in 1");
				switch(i.getBit()) 
				{
				case TuringCodeBook.BLOCK_START:
					        state.registers[1]=1;
					        logger.debug("gene block start");
					        state.registers[0]=i.getPos();
					        break;
				case TuringCodeBook.BLOCK_END:
					        state.registers[1]=0;
					        logger.debug("gene block end");
					        state.registers[0]=i.getPos();
				           break; 
				}
		        break;
		
	   case(2):
			    logger.debug("in 2");
				switch(i.getBit()){ 
				case TuringCodeBook.BLOCK_START:
				        	 logger.info("read block start"); 
				        	 if (state.registers[1]==0) { logger.debug("case 1");return false;} // gene is off but start a read
				             if (state.registers[1]==1 && state.registers[0]!=i.getPos() && state.registers[2]!=0) {logger.debug("case 2");logger.debug(state.registers[0]);logger.debug(i.getPos());return false; }// gene is on, start in middle of gene , not the read first Start
				        	 state.registers[3]=1;
				        	 state.registers[2]=i.getPos();
				        	 break;
				case TuringCodeBook.BLOCK_END:
				        	 logger.info("read block end"); 
				        	if(state.registers[1]==1) {logger.debug("case 3");return false;} //gene is on but end a read block and the block is not the last block.
				        	if(state.registers[1]==0 && state.registers[0]!=i.getPos()) {logger.debug("case 4");return false;} //gene is off, but earlier than read. 
					        state.registers[3]=0;
			        	    state.registers[2]=i.getPos();
					        break;
				case TuringCodeBook.END: //END has prioroty .
					logger.info("read END");
					if(state.registers[1]==1) return true;
					if(state.registers[1]==0 && state.registers[0]==i.getPos()) return true;
					break;
				} 
				break;
		}//CODE

		}//FOR
		
		
		return true;
			
	}
	
	
/**
 * Translate the SAMRecord to gene coordinates.	
 * @param read
 * @param gene
 * @return
 */
	public static String[] translateToGeneCoordinates(SAMRecord read,BED gene)

	{
        logger.setLevel(Level.DEBUG);
		
		if (!gene.getChr().equalsIgnoreCase(read.getReferenceName())) return null; // if not in reade chromosome
		
		
		// TODO : add Strand Filter
		// IF NOT SAME STRAND : Return false;
		
		TuringCodes  turingCodes=new TuringCodes(gene.getChr());
		turingCodes.add(gene,1);
		turingCodes.add(read,2);
		TuringState state = new  TuringState(0,0,new int[16]);
		/**
		 *   A SIMPLE Turing Machine To Translate the Coordinates.
		 *   tid: useless
		 *   pos: useless
		 *   
		 *   registers[4];
		 *      REGISTERS[0]: LAST_GENE POS
		 *      REGISTERS[1]: GENE ON OR OFF
		 *      
		 *      REGISTERS[2]: LAST READS POS
		 *      REGISTERS[3]: READS ON OR OFF
		 *      
		 *      REGISTERS[4]: GENE cDNA LENGTH COUNTER
		 *      
		 *      REGISTERS[5]: READ LENGTH COUNTER 
		 *      
		 *       
		 *      REGISTERS[6]: READ START 
		 *      REGISTERS[7]: READ END
		 *  Priorty: gene > read
		 *           end > block_end
		 *           
		 *  Version: TEST.
		 *  
		 *  
		 */
		for(TuringCode i : turingCodes)
		{
	    logger.debug(i);
		switch(i.getCode())
		{//CODE
		
		case(1): //CODE IS GENE
			    logger.debug("in 1");
				switch(i.getBit()) 
				{
		        case TuringCodeBook.BLOCK_START:
					        state.registers[1]=1;
					        logger.debug("gene block start");
					        state.registers[0]=i.getPos();
					        break;
				case TuringCodeBook.BLOCK_END:
					        state.registers[1]=0;
					        logger.debug("gene block end");
					        state.registers[4]+=i.getPos()-state.registers[0];
					        state.registers[0]=i.getPos();
				           break; 
				}
				break;
	   case(2):
			    logger.debug("in 2");
				switch(i.getBit())
				{
				case TuringCodeBook.START:
					int start_pos=state.registers[4]+i.getPos()-state.registers[0];
					state.registers[6] = start_pos;
					break;
				case TuringCodeBook.BLOCK_START:
				        	 logger.info("read block start"); 
				        	 state.registers[3]=1;
				        	 state.registers[2]=i.getPos();
				        	 break;
					
				case TuringCodeBook.BLOCK_END:
				        	logger.info("read block end"); 
					        state.registers[3]=0;
					        state.registers[5]+=i.getPos()-state.registers[2];
			        	    state.registers[2]=i.getPos();
					        break;
				case TuringCodeBook.END: //END has prioroty .
					
                    int end_pos = state.registers[4]+i.getPos()-state.registers[0];
                    state.registers[7]=end_pos;
					logger.info("read END");
					break;
				} 
				break;
		}//CODE

		}//FOR
		
		int start=state.registers[6];
		int end=state.registers[7];
		int length=state.registers[4];
		int rlen=state.registers[5];
		if (gene.isNegativeStrand()) 
		{
			String[] retv = {gene.getName(),Integer.toString(length-end),Integer.toString(length-start),read.getReadName(),Integer.toString(rlen)};
		   return retv;
		}
		else
		{
			 String[] retv = {gene.getName(),Integer.toString(start),Integer.toString(end),read.getReadName(),Integer.toString(rlen)};
		   return retv;
		}
}

		


/**
 * TEST  
 * @param read
 * @param gene
 * @return
 */
public static String[] translateToGeneCoordinates(BED read,BED gene)

{
    logger.setLevel(Level.DEBUG);
	
	if (!gene.getChr().equalsIgnoreCase(read.getChr())) return null; // if not in reade chromosome
	
	
	// TODO : add Strand Filter
	// IF NOT SAME STRAND : Return false;
	
	TuringCodes  turingCodes=new TuringCodes(gene.getChr());
	turingCodes.add(gene,1);
	turingCodes.add(read,2);
	TuringState state = new  TuringState(0,0,new int[16]);
	/**
	 *   A SIMPLE Turing Machine To Translate the Coordinates.
	 *   tid: useless
	 *   pos: useless
	 *   
	 *   registers[4];
	 *      REGISTERS[0]: LAST_GENE POS
	 *      REGISTERS[1]: GENE ON OR OFF
	 *      
	 *      REGISTERS[2]: LAST READS POS
	 *      REGISTERS[3]: READS ON OR OFF
	 *      
	 *      REGISTERS[4]: GENE cDNA LENGTH COUNTER
	 *      
	 *      REGISTERS[5]: READ LENGTH COUNTER 
	 *      
	 *       
	 *      REGISTERS[6]: READ START 
	 *      REGISTERS[7]: READ END
	 *  Priorty: gene > read
	 *           end > block_end
	 *           
	 *  Version: TEST.
	 *  
	 *  
	 */
	for(TuringCode i : turingCodes)
	{
    logger.debug(i);
	switch(i.getCode())
	{//CODE
	
	case(1): //CODE IS GENE
		    logger.debug("in 1");
			switch(i.getBit()) 
			{
	        case TuringCodeBook.BLOCK_START:
				        state.registers[1]=1;
				        logger.debug("gene block start");
				        state.registers[0]=i.getPos();
				        break;
			case TuringCodeBook.BLOCK_END:
				        state.registers[1]=0;
				        logger.debug("gene block end");
				        state.registers[4]+=i.getPos()-state.registers[0];
				        state.registers[0]=i.getPos();
			           break; 
			}
			break;
   case(2):
		    logger.debug("in 2");
			switch(i.getBit())
			{
			case TuringCodeBook.START:
				int start_pos=state.registers[4]+i.getPos()-state.registers[0];
				state.registers[6] = start_pos;
				break;
			case TuringCodeBook.BLOCK_START:
			        	 logger.info("read block start"); 
			        	 state.registers[3]=1;
			        	 state.registers[2]=i.getPos();
			        	 break;
				
			case TuringCodeBook.BLOCK_END:
			        	logger.info("read block end"); 
				        state.registers[3]=0;
				        state.registers[5]+=i.getPos()-state.registers[2];
		        	    state.registers[2]=i.getPos();
				        break;
			case TuringCodeBook.END: //END has prioroty .
				
                int end_pos = state.registers[4]+i.getPos()-state.registers[0];
                state.registers[7]=end_pos;
				logger.info("read END");
				break;
			} 
			break;
	}//CODE

	}//FOR
	
	int start=state.registers[6];
	int end=state.registers[7];
	int length=state.registers[4];
	int rlen=state.registers[5];
	if (gene.isNegativeStrand()) 
	{
		String[] retv = {gene.getName(),Integer.toString(length-end),Integer.toString(length-start),read.getName(),Integer.toString(rlen)};
	   return retv;
	}
	else
	{
		 String[] retv = {gene.getName(),Integer.toString(start),Integer.toString(end),read.getName(),Integer.toString(rlen)};
	   return retv;
	}
    }





}

		
	






