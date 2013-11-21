package frontend.ast;

import java.util.ArrayList;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off various parameters
 */
public final class ParList extends ASTNode {

    private final ArrayList<VarDecl> params;

    /**
     * Creates a new, empty ParList
     * 
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public ParList(final String file, final int line) {
        super(file, line);
        params = new ArrayList<VarDecl>();
    }

    /**
     * Creates a new ParList
     * 
     * @param vardecl
     *            the first VarDecl in the list
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public ParList(final VarDecl vardecl, final String file, final int line) {
        this(file, line);
        params.add(vardecl);
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
    public final void addParam(VarDecl param) {
        params.add(param);
    }

    /**
     * Return the parameters
     */
    public final Iterable<VarDecl> getParams() {
        return java.util.Collections.unmodifiableList(params);
    }

    public final int size() {
        return params.size();
    }

    public final VarDecl get(int i) {
        return params.get(i);
    }
}
