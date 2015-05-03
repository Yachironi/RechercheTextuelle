package traducteur;

import java.util.ArrayList;

public class ListCoupleInt extends ArrayList<CoupleInt>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 36940650441546807L;

	public ListCoupleInt(){
		new ArrayList<CoupleInt>();
	}
	
	
	/**
	 * Si i existe, alors cette methode retourne l'autre entier associe a i
	 * @param i
	 * @return
	 */
	public int getOtherInt(int i){
		for(CoupleInt c : this){
			if(c.getI1() == i){
				return c.getI2();
			}
			else if(c.getI2() == i){
				return c.getI1();
			}
		}
		return -1;
	}
	
	public boolean contains(int i){
		for(CoupleInt c : this){
			if(c.getI1() == i || c.getI2() == i){
				return true;
			}
		}
		return false;
	}
	
	public int get_i1(int i2){
		for(CoupleInt c : this){
			if(c.getI2() == i2){
				return c.getI1();
			}
		}
		return -1;
	}
	
	public int get_i2(int i1){
		for(CoupleInt c : this){
			if(c.getI1() == i1){
				return c.getI2();
			}
		}
		return -1;
	}
	
	public boolean contains_i1(int i1){
		for(CoupleInt c : this){
			if(c.getI1() == i1){
				return true;
			}
		}
		return false;
	}
	
	public boolean contains_i2(int i2){
		for(CoupleInt c : this){
			if(c.getI2() == i2){
				return true;
			}
		}
		return false;
	}
	
	public boolean contains_couple(CoupleInt val){
		for(CoupleInt c : this){
			if(val.getI1() == c.getI1() && val.getI2() == c.getI2()){
				return true;
			}
		}
		return false;
	}
	
	public int getPosi(CoupleInt val){
		int i=0;
		for(CoupleInt c : this){
			if( (val.getI1() == c.getI1() && val.getI2() == c.getI2()) || (val.getI1()==c.getI2() && val.getI2()==c.getI1())){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public CoupleInt getOtherIntAndPosition(int i){
		int pos=0;
		for(CoupleInt c : this){
			if(c.getI1() == i){
				return new CoupleInt(c.getI2(), pos);
			}
			else if(c.getI2() == i){
				return new CoupleInt(c.getI1(), pos);
			}
			pos++;
		}
		return null;
	}
}
