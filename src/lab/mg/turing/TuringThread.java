package lab.mg.turing;
/**
 *  Created on 2013-3-14  
 */
public interface TuringThread {

	//private  int[] codes;
	
	void process(TuringCode c, TuringState s);
	void update(TuringState newState);
	public TuringState getThreadState();
	public void setThreadState(TuringState state);
	
	String output();
	public int[] getMemory();
	public void setMemory(int[] a);
	int getId();
	void setId(int id);
	TuringState getLastState();
	void setLastState(TuringState lastState);
	TuringState getFirstState();
	void setFirstState(TuringState firstState);
	void setLastMemory(int[] a);
	int[] getLastMemory();
	void cleanMemory();
	
}
