package frontend.visitors;

import frontend.ast.*;

/**
 * interface that provides methods to visit all ASTNodes
 * 
 * @param <P>
 *          The type of the parameter used by each visit method
 * @param <R>
 *          The type of the value returned by each visit method
 */
public interface ASTVisitor<P, R> {

    /**
     * Returns the number of errors that occurred during processing
     * 
     * @return number of errors
     */
    public abstract int getErrors();

    /**
     * Returns the name of the ASTVisitor
     * 
     * @return name
     */
    public abstract String getName();

    /**
     * Abstract method for the visitor pattern.
     * The default implementation calls this Method on every AST-Node in pre-order,
     * before recursing into the Node's children.
     *	
     * @param n
     */
    public abstract void prolog(ASTNode n);
    

    /**
     * Abstract method for the visitor pattern.
     * The default implementation calls this Method on every AST-Node in post-order, 
     * after finishing recursing into the Node's children	
     *
     * @param n
     */
    public abstract void epilog(ASTNode n);
    
    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final ANDExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */

    public abstract R visit(final Int2Real astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final Real2Int astnode, final P param);
    
    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final AssgnStmt astnode, final P param);
    
    
    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final Block astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final Const astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final DIVTerm astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final EQExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final FuncDecl astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final FuncCall astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final GEQExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final GTExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final Identifier astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final IfStmt astnode, final P param);
    
    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final WhileStmt astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final LEQExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final LTExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final ArrayAccess astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final SUBExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final NEQExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final ORExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final ParList astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final ArgList astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final ADDExpr astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final Program astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final ReturnStmt astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final StmtList astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final MULTerm astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final VarDecl astnode, final P param);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param astnode
     *            ASTNode to visit
     */
    public abstract R visit(final VarDeclList astnode, final P param);
}
