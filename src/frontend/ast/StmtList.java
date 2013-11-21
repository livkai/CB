package frontend.ast;

import java.util.ArrayList;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off various statements
 */
public final class StmtList extends ASTNode {

    private final ArrayList<Stmt> statements;

    /**
     * Creates a new, empty StmtList
     * 
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public StmtList(final String file, final int line) {
        super(file, line);
        statements = new ArrayList<Stmt>();
    }

    /**
     * Creates a new StmtList
     * 
     * @param stmt
     *            the first statement
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public StmtList(final Stmt stmt, final String file, final int line) {
        this(file, line);
        statements.add(stmt);
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
     * Add a new statement
     *
     * @param stmt
     *            the statement
     */
    public final void addStatement(Stmt stmt) {
        statements.add(stmt);
    }

    /**
     * Return the statements
     */
    public final Iterable<Stmt> getStatements() {
        return java.util.Collections.unmodifiableList(statements);
    }
}
