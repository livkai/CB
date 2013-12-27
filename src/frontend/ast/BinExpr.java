package frontend.ast;

import frontend.visitors.ASTVisitor;
import cil.CLABEL;

/**
 * The base class of all binary expressions
 */
public abstract class BinExpr extends Expr {
    protected Expr left;
    protected Expr right;
    protected boolean condExpr;
    protected CLABEL condLabel;

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
    
    //set the result of a condition-expression
    public void setCondResult(boolean res) {
    	condExpr = res;
    }
    
    public boolean getCondResult() {
    	return condExpr;
    }
    
    //set the label a condition-expression
    public void setCondLabel(CLABEL label) {
    	condLabel = label;
    }
    
    public CLABEL getCondLabel() {
    	return condLabel;
    }
}
