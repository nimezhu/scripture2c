
package lab.mg.turing;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import net.sf.samtools.SAMRecord;
import nextgen.core.annotation.Annotation;
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
		return compatibleWithGene(gene,sam,true);
	}
	public static boolean compatibleWithGene(BED gene, SAMRecord sam, boolean contained)
	{
		logger.setLevel(Level.WARN);
		
		if (!gene.getChr().equalsIgnoreCase(sam.getReferenceName())) return false; // if not in same chromosome
		if(contained && (gene.getStart() > sam.getAlignmentStart()-1 || gene.getEnd() < sam.getAlignmentEnd()))
			{
				return false;
			}
		
		// TODO : add Strand Filter
		// IF NOT SAME STRAND : Return false;
		
		TuringCodes  turingCodes=new TuringCodes(gene.getChr());
		turingCodes.add(gene,1);
		turingCodes.add(sam,2);
		
		return overlapCompatible(turingCodes);
			
	}
	
	
/**
 * Translate the SAMRecord to gene coordinates.	
 * @param read
 * @param gene
 * @return
 */
	public static String[] translateToGeneCoordinates(SAMRecord read,BED gene)

	{
        logger.setLevel(Level.WARN);
		
		if (!gene.getChr().equalsIgnoreCase(read.getReferenceName())) return null; // if not in reade chromosome
		
		
		// TODO : add Strand Filter
		// IF NOT SAME STRAND : Return false;
		
		TuringCodes  turingCodes=new TuringCodes(gene.getChr());
		turingCodes.add(gene,1);
		turingCodes.add(read,2);
		int[] coords=translateCoordinates(turingCodes,gene.isNegativeStrand());
			String[] retv = {gene.getName(),Integer.toString(coords[0]),Integer.toString(coords[1]),read.getReadName()};
		   return retv;
		
}

		

private static int[] translateCoordinates(TuringCodes turingCodes,boolean isNegativeStrand)
{
	int[] results = new int[2];
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
	TuringState state = new  TuringState(0,0,new int[16]);
	final int GENE = 1;
	final int READ = 2;
	final int GENE_BLOCK_ON=1;
	final int GENE_POS=0;
	final int READ_BLOCK_ON=3;
	final int READ_POS=2;
	final int GENE_LENGTH_COUNTER=4;
	final int READ_LENGTH_COUNTER=5;
	final int READ_START=6;
	final int READ_END=7;
	
	
	for(TuringCode i : turingCodes)
	{
    logger.debug(i);
	switch(i.getCode())
	{//CODE
	
	case(GENE): //CODE IS GENE
		    logger.debug("in 1");
			switch(i.getBit()) 
			{
	        case TuringCodeBook.BLOCK_START:
				        state.registers[GENE_BLOCK_ON]=1;
				        logger.debug("gene block start");
				        state.registers[GENE_POS]=i.getPos();
				        break;
			case TuringCodeBook.BLOCK_END:
				        state.registers[GENE_BLOCK_ON]=0;
				        logger.debug("gene block end");
				        state.registers[GENE_LENGTH_COUNTER]+=i.getPos()-state.registers[GENE_POS];
				        state.registers[GENE_POS]=i.getPos();
			           break; 
			}
			break;
   case(READ):
		    logger.debug("in 2");
			switch(i.getBit())
			{
			case TuringCodeBook.START:
				int start_pos=state.registers[GENE_LENGTH_COUNTER]+i.getPos()-state.registers[GENE_POS];
				state.registers[READ_START] = start_pos;
				break;
			case TuringCodeBook.BLOCK_START:
			        	 logger.info("read block start"); 
			        	 state.registers[READ_BLOCK_ON]=1;
			        	 state.registers[READ_POS]=i.getPos();
			        	 break;
				
			case TuringCodeBook.BLOCK_END:
			        	logger.info("read block end"); 
				        state.registers[READ_BLOCK_ON]=0;
				        state.registers[READ_LENGTH_COUNTER]+=i.getPos()-state.registers[READ_POS];
		        	    state.registers[READ_POS]=i.getPos();
				        break;
			case TuringCodeBook.END: //END has prioroty .
				
                int end_pos = state.registers[GENE_LENGTH_COUNTER]+i.getPos()-state.registers[GENE_POS];
                state.registers[READ_END]=end_pos;
				logger.info("read END");
				break;
			} 
			break;
	}//CODE

	}//FOR
	if(isNegativeStrand)
	{
		results[0]=state.registers[GENE_LENGTH_COUNTER]-state.registers[READ_END];
		results[1]=state.registers[GENE_LENGTH_COUNTER]-state.registers[READ_START];
	}
	else
	{
	results[0]=state.registers[READ_START];
	results[1]=state.registers[READ_END];
	}
	return results;
}
/**
 * TEST  
 * @param read
 * @param gene
 * @return
 */
public static String[] translateToGeneCoordinates(BED read,BED gene)

{
    logger.setLevel(Level.WARN);
	
	if (!gene.getChr().equalsIgnoreCase(read.getChr())) return null; // if not in reade chromosome
	
	
	// TODO : add Strand Filter
	// IF NOT SAME STRAND : Return false;
	
	TuringCodes  turingCodes=new TuringCodes(gene.getChr());
	turingCodes.add(gene,1);
	turingCodes.add(read,2);
	int[] coords=translateCoordinates(turingCodes,gene.isNegativeStrand());
	String[] retv = {gene.getName(),Integer.toString(coords[0]),Integer.toString(coords[1]),read.getName()};
   return retv;
	
    }







public static boolean overlapCompatible(Annotation bedA, Annotation bedB)
/**
 * return true only overlap and compatible
 * merge to large blocks
 * 
 */
{
	final int A_BLOCK=1;
	final int B_BLOCK=2;
	if (!bedA.getChr().equalsIgnoreCase(bedB.getReferenceName())) return false; // if not in bedBe chromosome
	
	
	// IF NOT SAME STRAND : Return false;
	if (bedA.getStrand()!=bedB.getStrand()) return false;
	
	TuringCodes  turingCodes=new TuringCodes(bedA.getChr());
	turingCodes.add(bedA, A_BLOCK);
	turingCodes.add(bedB, B_BLOCK);
	return overlapCompatible(turingCodes);
		
}

private static boolean overlapCompatible(TuringCodes turingCodes)
{
	logger.setLevel(Level.DEBUG);
	TuringState state = new  TuringState(0,0,new int[16]);
	/**
	 *   A SIMPLE Compatible Turing Machine First Try
	 *   tid: useless
	 *   pos: useless
	 *   
	 *   registers[4];
	 *      REGISTERS[0]: LAST_BEDA POS
	 *      REGISTERS[1]: BEDA ON OR OFF
	 *      
	 *      REGISTERS[2]: LAST BEDB POS
	 *      REGISTERS[3]: BEDB ON OR OFF
	 *       
	 *  Priorty: bedA > bedB 
	 *           end > block_end
	 *           
	 *  Version: TEST.
	 *  
	 *  
	 *     REGISTERS[4]: LAST POS (BOTH A AND B)
	 *     REGESTERS[5]: blocks on or off 1,0
	 *  
	 */
	
	final int A_BLOCK=1;
	final int B_BLOCK=2;
	
	
	final int  A_POS=0;
	final int  A_BLOCK_ON=1;
	final int  B_POS=2;
	final int  B_BLOCK_ON=3;
	
	
//	final int  POS=4;
//	final int  BLOCK_ON=5;
	
	final int A_ON=6;
	final int B_ON=7;
	
//	final int A_START_POS=8;
//	final int A_END_POS=9;
	
	
	
	
	int last_pos=0;
	
	for(TuringCode i : turingCodes)
	{
	logger.debug(i);
	if (i.getPos()!=last_pos)
			{
		      //judge state (all the codes in same position have been processed)
		      if(state.registers[A_ON]==1 && state.registers[B_ON]==1)
		      {
		    	  if(state.registers[A_BLOCK_ON]!=state.registers[B_BLOCK_ON])
		    	  {
		    		  logger.debug(state.registers[A_BLOCK_ON] );
		    		  logger.debug(state.registers[B_BLOCK_ON] );
		    		  
		    		  return false;
		    	  }
		    	  
		      }
		      last_pos=i.getPos();
			}
	switch(i.getCode())
	{//CODE
	
	case(A_BLOCK): //CODE IS A 
		    logger.debug("in 1");
			switch(i.getBit()) 
			{
			case TuringCodeBook.BLOCK_START:
				        state.registers[A_BLOCK_ON]=1;
				        logger.debug("A block start");
				        state.registers[A_POS]=i.getPos();
				        break;
			case TuringCodeBook.BLOCK_END:
				        state.registers[A_BLOCK_ON]=0;
				        logger.debug("A block end");
				        state.registers[A_POS]=i.getPos();
			           break; 
			case TuringCodeBook.START:
				state.registers[A_ON]=1;
				break;
			case TuringCodeBook.END:
				state.registers[A_ON]=0;
				break;
			
			}
	        break;
	
   case(B_BLOCK): // CODE IS B
		    logger.debug("in 2");
			switch(i.getBit()){ 
			case TuringCodeBook.BLOCK_START:
			        	 logger.info("B block start"); 
			        	 state.registers[B_BLOCK_ON]=1;
			        	 state.registers[B_POS]=i.getPos();
			        	 break;
			case TuringCodeBook.BLOCK_END:
			        	 logger.info("B block end"); 
				        state.registers[B_BLOCK_ON]=0;
		        	    state.registers[B_POS]=i.getPos();
				        break;
			case TuringCodeBook.END: //END has prioroty .
				logger.info("read END");
				state.registers[B_ON]=0;
				break;
			case TuringCodeBook.START:
				state.registers[B_ON]=1;
			} 
			
		break;
	}//CODE
	}//FOR
	
	
	return true;
}

}

		
	






