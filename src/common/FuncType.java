package common;

import java.util.ArrayList;

public class FuncType extends Type {
	
	private Type retType;
	private ArrayList<Type> parTypes;
	
	public FuncType(Type retType, ArrayList<Type> parTypes){
		this.retType = retType;
		this.parTypes = parTypes;
	}
	
	public Type getRetType(){
		return retType;
	}
	
	public Type getParType(int i){
		return parTypes.get(i);
	}
}
