package frontend.ast;

import common.Type;
import frontend.visitors.ASTVisitor;

/**
 * AST representation off a function declarartion
 */
public final class FuncDecl extends Decl {

    private final ParList parameters;
    private Block body;

    /**
     * Creates a new FuncDecl
     * 
     * @param type
     *            set the type to this
     * @param identifier
     *            set the identifier to this
     * @param body
     *            the body of the function
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public FuncDecl(final Type type, final Identifier identifier,
            final Block body, final String file, final int line) {
        this(type, identifier, new ParList(file, line), body, file, line);
    }

    /**
     * Creates a new FuncDecl
     * 
     * @param type
     *            set the type to this
     * @param identifier
     *            set the identifier to this
     * @param parlist
     *            the list of parameters
     * @param body
     *            the body of the function
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public FuncDecl(final Type type, final Identifier identifier,
            final ParList parlist, final Block body, final String file, final int line) {
        super(type, identifier, file, line);
        this.parameters = parlist;
        this.body = body;
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
        return getClassName() + ": " + getType() + " " + getIdentifier();
    }

    /**
     * Returns the parameter list
     */
    public ParList getParameterList() {
        return parameters;
    }

    /**
     * Returns the body of the function
     */
    public Block getBody() {
        return body;
    }
}
