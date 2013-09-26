package lab.mg.turing;

import java.util.Comparator;

import broad.core.annotation.ShortBED;

/**
 *  Created on 2013-3-7  
 */
public class TuringCodeComparator 
	implements Comparator<TuringCode>
	{
		@Override
		public int compare(TuringCode a, TuringCode b) {
			if (a.getTid()!=b.getTid())
			{
				return a.getTid()-b.getTid();
			}
			else
			{
				if(a.getPos()!=b.getPos())
				{
				return a.getPos()-b.getPos();
				}
				else
				{
					if(a.getCode()!=b.getCode())
						return a.getCode()-b.getCode();
					else
						return a.getBit() - b.getBit(); //Stop is First ( 0 is first).
					
				}
			 }
			}
	}
		
	
