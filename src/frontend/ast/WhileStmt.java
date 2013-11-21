package frontend.ast;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off an while statement
 */
public final class WhileStmt extends Stmt {

    private final Expr condition;
    private final Block whileblock;

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
    public WhileStmt(final Expr condition, final Block block, final String file, final int line) {
        super(file, line);
        this.condition = condition;
        this.whileblock = block;
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
        return "WHILE";
    }

    /**
     * Return the condition
     */
    public final Expr getCondition() {
        return condition;
    }

    /**
     * Return the block
     */
    public final Block getWhileBlock() {
        return whileblock;
    }
}
