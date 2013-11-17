package common;


/**
 * A representation of a variable.
 */
public class Variable {
	private String name;
	private Type type;
	private int depth;
	
	private String file;
	private int line;
   
	public Variable(String name, Type t, String file, int line) {
		this.name = name;
		this.type = t;
		this.file = file;
		this.line = line;
	}	
	public Variable(String name, Type t) {
		this.name = name;
		this.type = t;
		this.file = null;
		this.line = -1;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public int getDepth(){
		return depth;
	}
	
	public void setDepth(int depth){
		this.depth = depth;
	}
	
	public String getFile(){
		return file;
	}
	
	public int getLine(){
		return line;
	}
	
	
	
}

