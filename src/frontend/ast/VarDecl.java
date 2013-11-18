package frontend.ast;

import common.Type;
import frontend.visitors.ASTVisitor;

/**
 * AST representation off a variable declaration
 */
public final class VarDecl extends Decl {

    /**
     * Creates a new VarDecl
     * 
     * @param type
     *            set the type to this
     * @param identifier
     *            set the identifier to this
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public VarDecl(final Type type, final Identifier identifier, final String file, final int line) {
        super(type, identifier, file, line);
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
        return getClassName() + ": " + getType() + " " + getIdentifier();
    }
}
