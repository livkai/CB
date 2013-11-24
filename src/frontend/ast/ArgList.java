package frontend.ast;

import java.util.ArrayList;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off various parameters
 */
public final class ArgList extends ASTNode {

    private final ArrayList<Expr> params;

    /**
     * Creates a new, empty ArgList
     * 
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public ArgList(final String file, final int line) {
        super(file, line);
        params = new ArrayList<Expr>();
    }

    /**
     * Creates a new ArgList
     * 
     * @param e
     *            the first Expression in the list
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public ArgList(final Expr e, final String file, final int line) {
        this(file, line);
        params.add(e);
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
     * Add a parameter to the list
     *
     * @param param
     *            the parameter to add
     */
    public final void addParam(Expr param) {
        params.add(param);
    }
    
    public void addParam(Expr param, int i){
    	params.set(i, param);
    }

    /**
     * Return the parameters
     */
    public final Iterable<Expr> getParams() {
        return java.util.Collections.unmodifiableList(params);
    }

    public final int size() {
        return params.size();
    }

    public final Expr get(int i) {
        return params.get(i);
    }
}
