package frontend.ast;

import frontend.visitors.ASTVisitor;

/**
 * The base class of all binary expressions
 */
public abstract class BinExpr extends Expr {
    protected Expr left;
    protected Expr right;

    /**
     * Create a new BinExpr
     *
     * @param left
     *            The left subexpression
     * @param right
     *            The right subexpression
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    protected BinExpr(final Expr left, final Expr right, final String file, final int line) {
        super(file, line);
        this.left = left;
        this.right = right;
    }

    /**
     * Return the left subexpression
     */
    public final Expr getLeft() {
        return left;
    }

    /**
     * Return the right subexpression
     */
    public final Expr getRight() {
        return right;
    }

    public void setLeft(Expr l) {
        left = l;
    }

    public void setRight(Expr r) {
        right = r;
    }
}
