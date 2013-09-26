package lab.mg.turing;

import java.util.Arrays;

/**
 *  Created on 2013-3-14  
 */

public class TuringState {
    protected int tid;
    protected int pos;
    protected int[] registers;
	public TuringState(int tid, int pos, int[] registers) {
		super();
		this.tid = tid;
		this.pos = pos;
		this.registers = registers.clone();
	}
	public TuringState(int tid, int pos) {
		super();
		this.tid = tid;
		this.pos = pos;
		registers=new int[TuringConstant.SIZE];
	}
	public TuringState(TuringState t)
	{
		this(t.tid,t.pos,t.registers);
	}
	@Override
	public String toString() {
		return "TuringState [tid=" + tid + ", pos=" + pos + ", registers="
				+ Arrays.toString(registers) + "]";
	}
    @Override 
    public TuringState clone()
    {
       return new TuringState(tid,pos,registers);
    }
    
    
}