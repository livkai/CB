package frontend.ast;

import common.Type;
import common.Variable;

import frontend.visitors.ASTVisitor;

/**
 * The base class of all expressions
 */
public abstract class Expr extends ASTNode {
    /*
     * More to come here.
     */
	protected Type type;
	protected Variable variable;

    /**
     * Create a new Expr
     *
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    protected Expr(final String file, final int line) {
        super(file, line);
    }
    
    public Type getType() {
    	return type;
    }
    
    public void setType(Type t) {
    	type = t;
    }
    
    public void setVariable(Variable v) {
    	variable = v;
    }
    
    public Variable getVariable() {
    	return variable;
    }
    
}
