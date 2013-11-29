package frontend.ast;

import java.util.ArrayList;

import frontend.visitors.ASTVisitor;

/**
 * AST representation of an array access
 */
public final class ArrayAccess extends Expr {

    private final Identifier identifier;
    private final ArrayList<Expr> indices;

    /**
     * Creates a new ArrayAccess
     * 
     * @param identifier
     *            set the identifier to this
     * @param index
     *            set the first index
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public ArrayAccess(final Identifier identifier, Expr index, final String file, final int line) {
        super(file, line);

        assert (identifier != null) : this.hashCode();
        this.identifier = identifier;
        indices = new ArrayList<Expr>();
        indices.add(index);
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
     * Add an index to this LValue. The rightmost index is always the
     * last element in the list. The leftmost index is always first.
     */
    public final void addIndex(Expr index) {
        indices.add(index);
    }

    /**
     * Return the indices.
     */
    public final Iterable<Expr> getIndices() {
        return java.util.Collections.unmodifiableList(indices);
    }

    public final int getNumIndices() {
        return indices.size();
    }

    public final Expr getIndex(int i) {
        return indices.get(i);
    }

    public final void setIndex(int i, Expr x) {
        indices.set(i, x);
    }

    /**
     * Return the identifier
     */
    public final Identifier getIdentifier() {
        return identifier;
    }
}
