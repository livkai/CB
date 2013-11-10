package common;

import java.util.ArrayList;
import frontend.ast.Expr;
import frontend.ast.Const;

/**
 * A representation of a variable.
 */
public class Variable {
	private String name;
	private Type type;
   
	public Variable(String name, Type t) {
		this.name = name;
		this.type = t;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Type getType() {
		return this.type;
	}
	
	
}

