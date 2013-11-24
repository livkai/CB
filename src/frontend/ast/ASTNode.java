package frontend.ast;

import common.Variable;

import frontend.visitors.ASTVisitor;

/**
 * Abstract class that represents a single AST node
 */
public abstract class ASTNode {

    private final int line;

    private final String file;
    
    private  Variable var;

    /**
     * Creates a new ASTNode
     * 
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    protected ASTNode(final String file, final int line) {
        this.file = file;
        this.line = line;
        var = null;
    }

    /**
     * Abstract method for the visitor pattern
     * 
     * @param visitor
     *            ASTVisitor to accept
     */
    public abstract <P, R> R accept(ASTVisitor<P, R> visitor, P param);

    /**
     * Returns the name of the class
     * 
     * @return name of the class
     */
    public final String getClassName() {
        String cn = this.getClass().getName();
        return cn.substring(cn.lastIndexOf('.') + 1, cn.length());
    }

    /**
     * Returns the line in which this ASTNode occured
     * 
     * @return line number of this ASTNode
     */
    public final int getLine() {
        return line;
    }

    /**
     * Returns the file in which this ASTNode occurred.
     * 
     * @return file name.
     */
    public final String getFile() {
        return file;
    }
    
    /**
     * Returns the file in which this ASTNode occurred.
     * 
     * @return file name.
     */
    public final Variable getVariable() {
        return var;
    }
    
    public void setVariable(Variable var){
    	this.var = var;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public abstract String toString();
}
