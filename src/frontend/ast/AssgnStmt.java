package frontend.ast;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off an assign statement
 */
public final class AssgnStmt extends Stmt {

    private Expr lvalue;
    private Expr expr;

    /**
     * Create a new AssgnStmt
     * 
     * @param lvalue
     *            left side of the assignment
     * @param expr
     *            right side of the assignment
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public AssgnStmt(final Expr lvalue, final Expr expr, final String file,
            final int line) {
        super(file, line);
        this.lvalue = lvalue;
        this.expr = expr;
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
        return ":=";
    }

    /**
     * Returns the lvalue (the left side) of the assignment
     */
    public final Expr getLValue() {
        return lvalue;
    }

    public final void setLValue(Expr l) {
        lvalue = l;
    }

    /**
     * Returns the right side of the assignment
     */
    public final Expr getExpr() {
        return expr;
    }

    public final void setExpr(Expr e) {
        expr = e;
    }
}
