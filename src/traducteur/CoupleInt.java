package traducteur;

import java.io.Serializable;

public class CoupleInt implements Serializable{
	private int i1;
	private int i2;
	
	public CoupleInt(int int1, int int2){
		i1 = int1;
		setI2(int2);
	}

	public int getI1() {
		return i1;
	}

	public void setI1(int i1) {
		this.i1 = i1;
	}

	public int getI2() {
		return i2;
	}

	public void setI2(int i2) {
		this.i2 = i2;
	}

	@Override
	public String toString() {
		return "(" + i1  + ", " + i2 + ")";
	}

}
