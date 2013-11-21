package frontend.ast;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off a block
 */
public class Block extends Stmt {

    private final VarDeclList vardecllist;
    private final StmtList stmtlist;

    /**
     * Creates a new, empty Block
     * 
     * @param type
     *            set the type to this
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public Block(final String file, final int line) {
        this(new VarDeclList(file, line), new StmtList(file, line), file, line);
    }

    /**
     * Creates a new Block
     * 
     * @param stmtlist
     *            the list of statements in this block
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public Block(final StmtList stmtlist, final String file, final int line) {
        this(new VarDeclList(file, line), stmtlist, file, line);
    }

    /**
     * Creates a new Block
     * 
     * @param vardecllist
     *            the list of declarations in this block
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public Block(final VarDeclList vardecllist, final String file, final int line) {
        this(vardecllist, new StmtList(file, line), file, line);
    }

    /**
     * Creates a new Block
     * 
     * @param vardecllist
     *            the list of declarations in this block
     * @param stmtlist
     *            the list of statements in this block
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public Block(final VarDeclList vardecllist, final StmtList stmtlist, final String file,
            final int line) {
        super(file, line);
        this.vardecllist = vardecllist;
        this.stmtlist = stmtlist;
    }

    /**
     * Register an ASTVisitor at this ASTNode (visitor pattern)
     * 
     * @param visitor
     *            ASTVisitor to register at this ASTNode
     */
    public <P, R> R accept(final ASTVisitor<P, R> visitor, final P param) {
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
     * Returns the variable declaration list
     */
    public final VarDeclList getVarDeclList() {
        return vardecllist;
    }

    /**
     * Returns the statement list
     */
    public final StmtList getStmtList() {
        return stmtlist;
    }
}
