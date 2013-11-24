package frontend.visitors;


import java.util.ArrayList;
import common.*;

import java.util.Iterator;

import frontend.ast.*;
import frontend.util.Symboltable;

/**
 * Adapter class that visits all ASTNodes and performs NO action on them.
 * <b>Note:</b> The visit methods always return <code>null</code>.
 *
 * @param <P>
 *          The type of the parameter used by each visit method
 * @param <R>
 *          The type of the value returned by each visit method
 */
public class SymbolTableASTVisitor<P, R> extends ASTVisitorAdapter<P, R> implements ASTVisitor<P, R> {

	/** variable hold number of errors that occured during the Visitor run */
	protected int errors;
	/** name of the visitor */
	protected  String name;
	protected Symboltable st;

	/**
	 * prolog. 
	 * @see frontend.visitors.ASTVisitor#prolog ASTVisitor.prolog
	 */
	public void prolog(ASTNode n) {
		if(n instanceof Program) {
			//create a new block
			st.enterBlock();
			//add predefined functions
			Variable v0 = new Variable("readInt", Type.getIntType(),0);
			v0.setDepth(0);
			Variable v1 = new Variable("readChar", Type.getIntType(),0);
			v1.setDepth(0);
			Variable v2 = new Variable("readReal", Type.getRealType(),0);
			v2.setDepth(0);
			Variable v3 = new Variable("writeInt", Type.getIntType(),0);
			v3.setDepth(0);
			Variable v4 = new Variable("writeChar", Type.getIntType(),0);
			v4.setDepth(0);
			Variable v5 = new Variable("writeReal", Type.getIntType(),0);
			v5.setDepth(0);
			st.addVariable(v0);
			st.addVariable(v1);
			st.addVariable(v2);
			st.addVariable(v3);
			st.addVariable(v4);
			st.addVariable(v5);
			
		} else if( n instanceof Block ){
			st.enterBlock();
		} else if(n instanceof Identifier) {
			//search for variable
			Variable v = st.getVariable(((Identifier) n).getName());
			if(v != null) {
				//found: add it in identifier
				((Identifier) n).setVariable(v);
			}else {
				throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((Identifier) n).getName() + " cannot be resolved to a variable!");
			}
		} else if(n instanceof FuncDecl){
			//register variable in symboltable
			int flag = 0;
			Variable fd = new Variable(((FuncDecl) n).getName(), ((FuncDecl) n).getType(), n.getFile(), n.getLine(), flag);
			fd.setDepth(0);
			st.addVariable(fd);
			st.enterBlock();
		} else if(n instanceof VarDecl) {
			int flag = 1;
			//register variable in symboltable
			Variable vd = new Variable(((VarDecl) n).getName(), ((VarDecl) n).getType(), n.getFile(), n.getLine(), flag);
			vd.setDepth(st.getDepth());
			st.addVariable(vd);
		}
		
	}

	/**
	 * epilog.
	 * @see frontend.visitors.ASTVisitor#epilog ASTVisitor.epilog
	 */
	public void epilog(ASTNode n) {
		if(n instanceof Program || n instanceof Block || n instanceof FuncDecl) {
			//check wether a function 'main' was declared
			if(n instanceof Program){
				if(st.getVariable("main") == null){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": The function 'main' is not declared!");
				}
			}
			st.leaveBlock();
		}
	}

	/**
	 * Creates a new SympbolTableASTVisitor
	 * 
	 * @param name
	 *            set the name to this
	 */
	public SymbolTableASTVisitor(final String name) {
		super(name);
		errors = 0;
		this.name = name;
		st = new Symboltable();
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