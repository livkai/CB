package frontend.ast;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off a subtraction
 */
public final class SUBExpr extends BinExpr {

    /**
     * Create a new SUBExpr
     * 
     * @param left
     *            left subexpression
     * @param right
     *            right subexpression
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public SUBExpr(final Expr left, final Expr right, final String file, final int line) {
        super(left, right, file, line);
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
        return "-";
    }
}
