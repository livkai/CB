package frontend.ast;

import java.util.ArrayList;

import frontend.visitors.ASTVisitor;

/**
 * AST representation off the whole program (root node of each AST)
 */
public final class Program extends ASTNode {

    ArrayList<Decl> declarations;

    /**
     * Creates a new Program
     * 
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public Program(final String file, final int line) {
        super(file, line);
        declarations = new ArrayList<Decl>();
    }

    /**
     * Creates a new Program
     * 
     * @param funcdecl
     *            the first declaration in the program
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public Program(final FuncDecl funcdecl, final String file, final int line) {
        this(file, line);
        declarations.add(funcdecl);
    }

    /**
     * Creates a new Program
     * 
     * @param vardecl
     *            the first declaration in the program
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public Program(final VarDecl vardecl, final String file, final int line) {
        this(file, line);
        declarations.add(vardecl);
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
     * Add a declaration to the program
     *
     * @param decl
     *            the declaration
     */
    public final void addDecl(Decl decl) {
        declarations.add(decl);
    }

    /**
     * Return the declarations
     */
    public final Iterable<Decl> getDeclarations() {
        return java.util.Collections.unmodifiableList(declarations);
    }
}
