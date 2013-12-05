package frontend.visitors;


import java.util.ArrayList;
import java.util.Iterator;

import cil.CSUB;
import cil.IRFunction;
import cil.IRProgram;
import cil.RegisterOperand;
import cil.VirtualRegister;
import common.*;
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
public class CILGeneratorASTVisitor<P, R> extends ASTVisitorAdapter<P, R> implements ASTVisitor<P, R> {

	/** variable hold number of errors that occured during the Visitor run */
	protected int errors;
	/** name of the visitor */
	protected  String name;
	protected ArrayList<ASTNode> list;
	protected ArrayList<IRFunction> irfuncs;

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
	}

	/**
	 * Creates a new TypeCheckASTVisitor
	 * 
	 * @param name
	 *            set the name to this
	 */
	public CILGeneratorASTVisitor(final String name) {
		super(name);
		errors = 0;
		this.name = name;
		list = new ArrayList<ASTNode>();
		irfuncs = new ArrayList<IRFunction>();
	}
	
	

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final AssgnStmt n, final P param) {
		prolog(n);
		n.getLValue().accept(this, param);
		n.getExpr().accept(this, param);
		
		epilog(n);
		return null;
	}


	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final Const n, final P param) {
		prolog(n);
		epilog(n);
		return null;
	}

	
	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final FuncDecl n, final P param) {
		prolog(n);
		IRFunction irf = new IRFunction(n.getName());
		ParList params = n.getParameterList();
		for(int i = 0; i< params.size(); i++) {
			irf.addParam(params.get(i).getIdentifier().getVariable());
		}
		Iterator<VarDecl> vdli = n.getBody().getVarDeclList().getVarDecls().iterator();
		while(vdli.hasNext()){
			irf.addLocals(vdli.next().getIdentifier().getVariable());
		}
		irfuncs.add(irf);
//		StmtList sl = n.getBody().getStmtList();
//		Iterator<Stmt> sli = sl.getStatements().iterator();
		
		n.getParameterList().accept(this, param);
		n.getBody().accept(this, param);
		decl(n, param);
		epilog(n);
		return null;
	}


	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final Identifier n, final P param) {
		prolog(n);
		epilog(n);
		return null;
	}

	
	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final ArrayAccess n, final P param) {
		prolog(n);
		n.getIdentifier().accept(this, param);
		for(Expr it : n.getIndices()) {
			it.accept(this, param);
		}
		epilog(n);
		return null;
	}
	
	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final FuncCall n, final P param) {
		prolog(n);
		n.getIdentifier().accept(this, param);
		if(n.getArgList() != null) {
			n.getArgList().accept(this, param);
		}
		
		epilog(n);
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
		IRProgram irp = new IRProgram();
		//Iterator<Decl> iter = astnode.getDeclarations().iterator();
		Iterator<Decl> iter = astnode.getDeclarations().iterator();
		while(iter.hasNext()) {
			Decl dec = iter.next();
			if( dec instanceof VarDecl) {
				irp.addGlobal(((VarDecl) dec).getIdentifier().getVariable());
			}
		}
		for(int i=0; i < irfuncs.size(); i++){
			irp.addFunc(irfuncs.get(i));
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
	public R visit(final ReturnStmt n, final P param) {
		prolog(n);
		n.getReturnValue().accept(this, param);
		
		epilog(n);
		return null;
	}

	
	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final VarDecl n, final P param) {
		prolog(n);
		decl(n, param);
		if(n.getIdentifier().getVariable().getDepth() == 0) {
			
		}
		epilog(n);
		return null;
	}


	/**
	 * Visits a binary expression
	 * 
	 * @param astnode
	 *            The expression
	 */
	private void binexpr(final BinExpr n, final P param) {
		n.getLeft().accept(this, param);
		n.getRight().accept(this, param);
		
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
	public R visit(final SUBExpr astnode, final P param) {
		prolog(astnode);
		if(list.get(list.size()-2) instanceof BinExpr){
			VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
			if(astnode.getLeft() )
			CSUB cs = new CSUB(new RegisterOperand(vr),);
		}
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}
}