package frontend.ast;

/**
 * abstract class that represnts any statement in an AST
 */
public abstract class Stmt extends ASTNode {

    /**
     * Creates a new Stmt
     * 
     * @param type
     *            set the type to this
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    protected Stmt(final String file, final int line) {
        super(file, line);
    }
}
