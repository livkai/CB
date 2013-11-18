package frontend.ast;

import java.io.IOException;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off a constant number
 */
public final class Const extends Expr {

    private final String number;

    /**
     * Creates a new Const
     * 
     * @param number
     *            set the number to this
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public Const(final String number, final String file, final int line) {
        super(file, line);
        assert (number != null) : this.hashCode();
        this.number = number;
    }

    /**
     * Register an ASTVisitor at this ASTNode (visitor pattern)
     * 
     * @param visitor
     *            ASTVisitor to register at this ASTNode
     */
    public final <P, R> R accept(final ASTVisitor<P, R> visitor, final P param) {
        return visitor.visit(this, param);
    }

    /**
     * Returns the string representation for this class.
     * 
     * @return representation for this class.
     */
    public final String toString() {
        return "const: " + number;
    }
    
    /**
     * Returns the value of this Constant as Integer
     * 
     * @return the value of this Constant as Integer
     */
    public int toInt() {
    	int i = 0;
    	try {
    		i = Integer.parseInt(number);
    	}catch (NumberFormatException nfe) {
    		
    	}
    	// TODO: implement this Method!
    	return i;
    }
    
    public double toDouble(){
    	double i = 0;
    	try {
    		i = Double.parseDouble(number);
    	}catch (NumberFormatException nfe) {
    		
    	}
    	// TODO: implement this Method!
    	return i;
    }
    
    public String getNumber(){
    	return number;
    }
    
}
