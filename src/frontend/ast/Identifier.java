package frontend.ast;

import common.Variable;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off an identifier
 */
public final class Identifier extends Expr {

    private final String name;
    private Variable variable;

    /**
     * Creates a new Identifier
     * 
     * @param name
     *            set the name to this
     * @param file
     *            set the file number
     * @param line
     *            set the line to this
     */
    public Identifier(final String name, final String file, final int line) {
        super(file, line);
        assert (name != null) : this.hashCode();
        this.name = name;
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
     * Returns the name of the identifier
     * 
     * @return name of the identifier
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Returns the string representation for this class.
     * 
     * @return representation for this class.
     */
    public final String toString() {
        return "id: " + name;
    }
    
    public void setVariable(Variable v) {
    	variable = v;
    }
    
}
