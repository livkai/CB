package frontend.ast;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off a return statement
 */
public final class ReturnStmt extends Stmt {

    private Expr retval;

    /**
     * Creates a new ReturnStamt
     * 
     * @param retval 
     *            the expression to return
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public ReturnStmt(final Expr retval, final String file, final int line) {
        super(file, line);
        this.retval = retval;
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
        return "RETURN";
    }

    public final Expr getReturnValue() {
        return retval;
    }

    public final void setReturnValue(Expr r) {
        retval = r;
    }
}
