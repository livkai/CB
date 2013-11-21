package frontend.ast;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off an if statement
 */
public final class IfStmt extends Stmt {

    private final Expr condition;
    private final Block thenblock;
    private final Block elseblock;

    /**
     * Creates a new IfStmt
     * 
     * @param condition
     *            the condition the if-statement must evaluate
     * @param block
     *            the then-block
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public IfStmt(final Expr condition, final Block block, final String file, final int line) {
        super(file, line);
        this.condition = condition;
        this.thenblock = block;
        this.elseblock = null;
    }

    /**
     * Creates a new IfStmt
     * 
     * @param condition
     *            the condition the if-statement must evaluate
     * @param block1
     *            the then-block
     * @param block2
     *            the else-block
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public IfStmt(final Expr condition, final Block block1,
            final Block block2, final String file, final int line) {
        super(file, line);
        this.condition = condition;
        this.thenblock = block1;
        this.elseblock = block2;
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
        return "IF";
    }

    /**
     * Return the condition
     */
    public final Expr getCondition() {
        return condition;
    }

    /**
     * Return the then-block
     */
    public final Block getThenBlock() {
        return thenblock;
    }


    /**
     * Return the else-block. Returns null if there is none.
     */
    public final Block getElseBlock() {
        return elseblock;
    }
}
