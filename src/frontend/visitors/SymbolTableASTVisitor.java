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
		
	}

	/**
	 * epilog.
	 * @see frontend.visitors.ASTVisitor#epilog ASTVisitor.epilog
	 */
	public void epilog(ASTNode n) {
		if(n instanceof Program || n instanceof Block || n instanceof FuncDecl) {
			//check whether a function 'main' was declared
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
	public R visit(final Program astnode, final P param) {
		prolog(astnode);
		//create a new block
		st.enterBlock();
		//add predefined functions
		Variable v0 = new Variable("readInt",  new FuncType(Type.getIntType(),new ArrayList<Type>()));
		v0.setDepth(0);
		Variable v1 = new Variable("readChar", new FuncType(Type.getIntType(),new ArrayList<Type>()));
		v1.setDepth(0);
		Variable v2 = new Variable("readReal", new FuncType(Type.getRealType(),new ArrayList<Type>()));
		v2.setDepth(0);
		ArrayList<Type> params = new ArrayList<Type>();
		ArrayList<Type> params2 = new ArrayList<Type>();
		params.add(Type.getIntType());
		Variable v3 = new Variable("writeInt", new FuncType(Type.getIntType(),params));
		v3.setDepth(0);
		Variable v4 = new Variable("writeChar", new FuncType(Type.getIntType(),params));
		v4.setDepth(0);
		params2.add(Type.getRealType());
		Variable v5 = new Variable("writeReal", new FuncType(Type.getIntType(),params2));
		v5.setDepth(0);
		st.addVariable(v0);
		st.addVariable(v1);
		st.addVariable(v2);
		st.addVariable(v3);
		st.addVariable(v4);
		st.addVariable(v5);
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
	public R visit(final Block astnode, final P param) {
		prolog(astnode);
		st.enterBlock();
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
	public R visit(final FuncDecl astnode, final P param) {
		prolog(astnode);
		//register variable in symboltable
		ArrayList<Type> params = new ArrayList<Type>();
		for(int i=0;i< astnode.getParameterList().size();i++){
			params.add(astnode.getParameterList().get(i).getType());
		}
		FuncType ft = new FuncType(astnode.getType(),params);
		Variable fd = new Variable(astnode.getName(), ft, astnode.getFile(), astnode.getLine());
		fd.setDepth(0);
		st.addVariable(fd);
		st.enterBlock();
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
	public R visit(final VarDecl astnode, final P param) {
		prolog(astnode);
		//register variable in symboltable
		Variable vd = new Variable(astnode.getName(), astnode.getType(), astnode.getFile(), astnode.getLine());
		vd.setDepth(st.getDepth());
		st.addVariable(vd);
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
	
	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final Identifier astnode, final P param) {
		prolog(astnode);
		//search for variable
		Variable v = st.getVariable(astnode.getName());
		if(v != null) {
			//found: add it in identifier
			astnode.setVariable(v);
		}else {
			throw new InternalCompilerErrorRuntimeException(astnode.getFile() + ": "+ astnode.getLine() + ": "+ astnode.getName() + " cannot be resolved to a variable!");
		}
		epilog(astnode);
		return null;
	}



}