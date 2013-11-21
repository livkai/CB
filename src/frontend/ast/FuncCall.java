package frontend.ast;

import java.util.ArrayList;

import frontend.visitors.ASTVisitor;

/**
 * AST representation of an function call
 */
public final class FuncCall extends Expr {

    private final Identifier identifier;
    private final ArgList arglist;

    /**
     * Creates a new FuncCall
     * 
     * @param identifier
     *            set the identifier to this
     * @param list
     *            set the ArgList-values
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public FuncCall(final Identifier identifier, ArgList l, final String file, final int line) {
        super(file, line);

        assert (identifier != null) : this.hashCode();
        this.identifier = identifier;
        this.arglist = l;
    }
    
    /**
     * Creates a new FuncCall
     * 
     * @param identifier
     *            set the identifier to this
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public FuncCall(final Identifier identifier, final String file, final int line) {
        super(file, line);

        assert (identifier != null) : this.hashCode();
        this.identifier = identifier;
        this.arglist = null;
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
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return getClassName();
    }

    /**
     * Return the argList. Return null if there is none.
     */
    public final ArgList getArgList() {
        return arglist;
    }
    
    /**
     * Return the identifier
     */
    public final Identifier getIdentifier() {
        return identifier;
    }
}
