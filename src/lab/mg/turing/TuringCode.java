package lab.mg.turing;
/**
 *  Created on 2013-3-7  
 */
public class TuringCode {
	private int tid; //chromosome tid2chr
	private int pos; //0-index
	private int bit;  //isStart or End , other to continue
	private int code;    // belong to which class;
	private double param;   // for future using
	private static double DEFAULT_PARAM=0.0;
	public TuringCode(int tid, int pos, int bit, int code, double param) {
		super();
		this.tid = tid;
		this.pos = pos;
		this.bit = bit;
		this.code = code;
		this.param = param;
	}
	public TuringCode(int tid, int pos, int bit, int code) {
		super();
		this.tid = tid;
		this.pos = pos;
		this.bit = bit;
		this.code = code;
		this.param=DEFAULT_PARAM;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public int getBit() {
		return bit;
	}
	public void setBit(int bit) {
		this.bit = bit;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public double getParam() {
		return param;
	}
	public void setParam(double param) {
		this.param = param;
	}
	@Override
	public String toString() {
		return "TuringCode [tid=" + tid + ", pos=" + pos + ", bit=" + bit
				+ ", code=" + code + ", param=" + param + "]";
	}
	
	

}
