package frontend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import common.InternalCompilerErrorRuntimeException;
import common.Variable;



public class Symboltable {

	protected ArrayList<Hashtable<String,Variable>> stack;
	protected int depth;
	
	public Symboltable(){
		stack = new ArrayList<Hashtable<String,Variable>>();
		depth = -1;
	}
	
	public void enterBlock(){
		stack.add(new Hashtable<String,Variable>());
		depth++;
	}
	
	public void leaveBlock(){
		stack.remove(stack.size()-1);
		depth--;
	}
	
	public void addVariable(Variable v){
		if(stack.get(depth).containsKey(v.getName())){
			throw new InternalCompilerErrorRuntimeException("File: " + v.getFile() + ": "+ "Line: " + v.getLine()+ " : " + "Variable " + v.getName() +" is already defined in this scope! First defined here: " + "Line: " + stack.get(depth).get(v.getName()).getLine());
		}else{
			v.setDepth(depth);
			stack.get(depth).put(v.getName(), v);
		}
	}

	public Variable getVariable(String name){
		for(int i=depth; i>=0; i--){
			if(stack.get(i).containsKey(name)){
				return stack.get(i).get(name);
			}
		}
		return null;
	}
	
	public int getDepth() {
		return depth;
	}

}
