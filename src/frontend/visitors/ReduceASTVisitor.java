package frontend.visitors;


import java.util.ArrayList;
import java.util.Iterator;

import frontend.ast.*;

/**
 * Adapter class that visits all ASTNodes and performs NO action on them.
 * <b>Note:</b> The visit methods always return <code>null</code>.
 *
 * @param <P>
 *          The type of the parameter used by each visit method
 * @param <R>
 *          The type of the value returned by each visit method
 */
public class ReduceASTVisitor<P, R> extends ASTVisitorAdapter<P, R> implements ASTVisitor<P, R> {

	/** variable hold number of errors that occured during the Visitor run */
	protected int errors;
	/** name of the visitor */
	protected  String name;
	protected ArrayList<ASTNode> list;
	protected boolean end = false;
	protected int i= 0;
	protected int index = -1;


	/**
	 * prolog. 
	 * @see frontend.visitors.ASTVisitor#prolog ASTVisitor.prolog
	 */
	public void prolog(ASTNode n) {
		list.add(n);
	}

	/**
	 * epilog.  
	 * @see frontend.visitors.ASTVisitor#epilog ASTVisitor.epilog
	 */
	public void epilog(ASTNode n) {
		list.remove(list.size()-1);
		if(!list.isEmpty()) {
			ASTNode parent = list.get(list.size()-1);
			//test if n is an expression which can be simplified
			if(n instanceof ADDExpr || n instanceof SUBExpr || n instanceof MULTerm || n instanceof DIVTerm){
				if(((BinExpr) n).getLeft() instanceof Const  && ((BinExpr) n).getRight() instanceof Const) {
					Const newConst = null;
					if(n instanceof ADDExpr) { 
						//tests if value is from type 'real' or 'int'
						if(((Const) ((ADDExpr) n).getLeft()).getNumber().contains(".") || ((Const) ((ADDExpr) n).getRight()).getNumber().contains(".")){
							Double newNumber = new Double(((Const) ((ADDExpr) n).getLeft()).toDouble() + ((Const) ((ADDExpr) n).getRight()).toDouble());
							newConst = new Const(newNumber.toString(),n.getFile(),n.getLine());
						}else{
							Integer newNumber = new Integer(((Const) ((ADDExpr) n).getLeft()).toInt() + ((Const) ((ADDExpr) n).getRight()).toInt());
							//create the new constant
							newConst = new Const(newNumber.toString(),n.getFile(),n.getLine());
						}	
					}
					else if(n instanceof SUBExpr) {
						if(((Const) ((SUBExpr) n).getLeft()).getNumber().contains(".") || ((Const) ((SUBExpr) n).getRight()).getNumber().contains(".")){
							Double newNumber = new Double(((Const) ((SUBExpr) n).getLeft()).toDouble() - ((Const) ((SUBExpr) n).getRight()).toDouble());
							newConst = new Const(newNumber.toString(),n.getFile(),n.getLine());
						}else{
							Integer newNumber = new Integer(((Const) ((SUBExpr) n).getLeft()).toInt() - ((Const) ((SUBExpr) n).getRight()).toInt());
							newConst = new Const(newNumber.toString(),n.getFile(),n.getLine());
						}
					}
					else if(n instanceof MULTerm) {
						if(((Const) ((MULTerm) n).getLeft()).getNumber().contains(".") || ((Const) ((MULTerm) n).getRight()).getNumber().contains(".")){
							Double newNumber = new Double(((Const) ((MULTerm) n).getLeft()).toDouble() * ((Const) ((MULTerm) n).getRight()).toDouble());
							newConst = new Const(newNumber.toString(),n.getFile(),n.getLine());
						}else{
							Integer newNumber = new Integer(((Const) ((MULTerm) n).getLeft()).toInt() * ((Const) ((MULTerm) n).getRight()).toInt());
							newConst = new Const(newNumber.toString(),n.getFile(),n.getLine());
						}
					}
					else if(n instanceof DIVTerm) {
						if(((Const) ((DIVTerm) n).getRight()).toDouble() != 0) {
							if(((Const) ((DIVTerm) n).getLeft()).getNumber().contains(".") || ((Const) ((DIVTerm) n).getRight()).getNumber().contains(".")){
								Double newNumber = new Double(((Const) ((DIVTerm) n).getLeft()).toDouble() / ((Const) ((DIVTerm) n).getRight()).toDouble());
								newConst = new Const(newNumber.toString(),n.getFile(),n.getLine());
							}else{
								Integer newNumber = new Integer(((Const) ((DIVTerm) n).getLeft()).toInt() / ((Const) ((DIVTerm) n).getRight()).toInt());
								newConst = new Const(newNumber.toString(),n.getFile(),n.getLine());
							}
						} 
					}	
					if(parent instanceof BinExpr) {
						//reduction in left subtree
						if(((BinExpr) parent).getLeft().equals(n)) {
							((BinExpr) parent).setLeft(newConst);
						}
						//reduction in right subtree
						else if(((BinExpr) parent).getRight().equals(n)) {
							((BinExpr) parent).setRight(newConst);
						}
					}
					
					if(parent instanceof AssgnStmt) {
						if(((AssgnStmt) parent).getExpr().equals(n)) {
							((AssgnStmt) parent).setExpr(newConst);
						}
					}
					
					if(parent instanceof ReturnStmt) {
						if(((ReturnStmt) parent).getReturnValue().equals(n)) {
							((ReturnStmt) parent).setReturnValue(newConst);
						}
					}
					
					if(parent instanceof ArrayAccess) {
						Iterable<Expr> iter = ((ArrayAccess) parent).getIndices();
						int count = -1;
						for(Expr ex : iter) {
							count++;
							if(((ArrayAccess) parent).getIndex(count).equals(n)) {
								((ArrayAccess) parent).setIndex(count, newConst);
							}
						}
					}
					if(parent instanceof VarDecl){
						Iterable<Expr> iter = ((VarDecl) parent).getType().getDimensions();
						int count = -1;
						for(Expr ex : iter){
							count++;
							if(((VarDecl) parent).getType().getDim(count).equals(n)){
								((VarDecl) parent).getType().setDim(count, newConst);
							}
						}
					}
				} 
			}
		}
	}

	/**
	 * Creates a new ReduceASTVisitor
	 * 
	 * @param name
	 *            set the name to this
	 */
	public ReduceASTVisitor(final String name) {
		super(name);
		list = new ArrayList<ASTNode>();
		errors = 0;
		this.name = name;
	}
	
	
	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final ADDExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final ANDExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final AssgnStmt astnode, final P param) {
		prolog(astnode);
		astnode.getLValue().accept(this, param);
		astnode.getExpr().accept(this, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final Block astnode, final P param) {
		prolog(astnode);
		astnode.getVarDeclList().accept(this, param);
		astnode.getStmtList().accept(this, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final Const astnode, final P param) {
		prolog(astnode);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final DIVTerm astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final EQExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final FuncDecl astnode, final P param) {
		prolog(astnode);
		astnode.getParameterList().accept(this, param);
		astnode.getBody().accept(this, param);
		decl(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final GEQExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final GTExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final Identifier astnode, final P param) {
		prolog(astnode);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final IfStmt astnode, final P param) {
		prolog(astnode);
		astnode.getCondition().accept(this, param);
		astnode.getThenBlock().accept(this, param);
		Block elseblock = astnode.getElseBlock();
		if (elseblock != null) {
			elseblock.accept(this, param);
		}
		epilog(astnode);
		return null;
	}
	
	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final WhileStmt astnode, final P param) {
		prolog(astnode);
		astnode.getCondition().accept(this, param);
		astnode.getWhileBlock().accept(this, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final LEQExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final LTExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final ArrayAccess astnode, final P param) {
		prolog(astnode);
		astnode.getIdentifier().accept(this, param);
		for(Expr it : astnode.getIndices()) {
			it.accept(this, param);
		}
		epilog(astnode);
		return null;
	}
	
	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final FuncCall astnode, final P param) {
		prolog(astnode);
		astnode.getIdentifier().accept(this, param);
		if(astnode.getArgList() != null) {
			astnode.getArgList().accept(this, param);
		}
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final MULTerm astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final NEQExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final ORExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final ParList astnode, final P param) {
		prolog(astnode);
		for(VarDecl it : astnode.getParams()) {
			it.accept(this, param);
		}
		epilog(astnode);
		return null;
	}
	
	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final ArgList astnode, final P param) {
		prolog(astnode);
		for(Expr it : astnode.getParams()) {
			it.accept(this, param);
		}
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final Program astnode, final P param) {
		prolog(astnode);
		for(Decl it : astnode.getDeclarations()) {
			it.accept(this, param);
		}
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final ReturnStmt astnode, final P param) {
		prolog(astnode);
		astnode.getReturnValue().accept(this, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final StmtList astnode, final P param) {
		prolog(astnode);
		for(Stmt it : astnode.getStatements()) {
			it.accept(this, param);
		}
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final SUBExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final VarDecl astnode, final P param) {
		prolog(astnode);
		decl(astnode, param);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final VarDeclList astnode, final P param) {
		prolog(astnode);
		for(VarDecl it : astnode.getVarDecls()) {
			it.accept(this, param);
		}
		epilog(astnode);
		return null;
	}

	/**
	 * Visits a binary expression
	 * 
	 * @param astnode
	 *            The expression
	 */
	private void binexpr(final BinExpr astnode, final P param) {
		astnode.getLeft().accept(this, param);
		astnode.getRight().accept(this, param);
		return;
	}

	/**
	 * Visits a declaration
	 *
	 * @param astnode
	 *              The declaration
	 */
	private void decl(final Decl astnode, final P param) {
		astnode.getIdentifier().accept(this, param);
		for(Expr it : astnode.getType().getDimensions()) {
			it.accept(this, param);
		}
	}


}