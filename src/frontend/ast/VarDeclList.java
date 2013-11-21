package frontend.ast;

import java.util.ArrayList;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off various VarDecls
 */
public final class VarDeclList extends ASTNode {

    private ArrayList<VarDecl> vardecls;

    /**
     * Creates a new, empty VarDeclList
     * 
     * @param vardecl
     *            the first declaration in the list
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public VarDeclList(final String file, final int line) {
        super(file, line);
        vardecls = new ArrayList<VarDecl>();
    }

    /**
     * Creates a new VarDeclList
     * 
     * @param vardecl
     *            the first declaration in the list
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public VarDeclList(final VarDecl vardecl, final String file, final int line) {
        this(file, line);
        vardecls.add(vardecl);
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
     * Add a new VarDecl.
     *
     * @param vardecl
     *            the declaration
     */
    public final void addVarDecl(VarDecl vardecl) {
        vardecls.add(vardecl);
    }

    /**
     * Return the variable declarations
     */
    public final Iterable<VarDecl> getVarDecls() {
        return java.util.Collections.unmodifiableList(vardecls);
    }
}
