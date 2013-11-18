package frontend.visitors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import frontend.ast.*;

/**
 * class that visits all ASTNodes and writes to a .dot-file in order to dump the AST
 * <b>Note:</b> The visit methods always return <code>null</code>.
 *
 * @param <P>
 *          The type of the parameter used by each visit method
 * @param <R>
 *          The type of the value returned by each visit method
 */
public class DumpASTVisitor<P, R> extends ASTVisitorAdapter<P, R> implements ASTVisitor<P, R> {

	/** variable hold number of errors that occured during the Visitor run */
	protected int errors;
	/** name of the visitor */
	protected  String name;
	protected ArrayList<String> list;
	protected boolean end = false;
	protected int i= 0;
	protected int index = -1;


	/**
	 * prolog. Writes the visited nodes into the .dot-file
	 * @see frontend.visitors.ASTVisitor#prolog ASTVisitor.prolog
	 */
	public void prolog(ASTNode n) {
		if(n instanceof VarDeclList || n instanceof StmtList){
			return;
		}
		//create a unique id for each node
		String unique = "\"" + n.toString() +i+"\"";
		try {
			File file = null;
			name = name.replace(".e","");
			if(index != -1) {
				file = new File("./" + name + index + ".dot");
			}else {
				file = new File("./" + name + ".dot");
			}
			FileWriter fw = null; 
			BufferedWriter bw = null;
			boolean exists = false;
			
			//file already exists and we are at the beginning -> overwrites the existing file
			if(file.exists() && n.getClassName().compareTo("Program")==0) {
				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				String begin = "digraph ast { \n";
				bw.append(begin);
				bw.flush();
			}
			// if file doesn't exist, then create it
			if (!(file.exists())) {
				exists = true;
				file.createNewFile();
				fw = new FileWriter(file.getAbsoluteFile(),true);
				bw = new BufferedWriter(fw);
				String begin = "digraph ast { \n";
				bw.append(begin);
				bw.flush();
			}
			if(!exists){
				fw = new FileWriter(file.getAbsoluteFile(),true);
				bw = new BufferedWriter(fw);
			}
			bw.append(unique + "[label=\""+n.toString()+"\"]; \n");
			if(n.getClassName().compareTo("Program")!=0){
			int size = list.size();
			bw.append(list.get(size-1)+"->"+unique+"; \n");
			}
			bw.flush();
			list.add(unique);
			i++;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * epilog. 
	 * @see frontend.visitors.ASTVisitor#epilog ASTVisitor.epilog
	 */
	public void epilog(ASTNode n) {
		File file = null;
		if(n instanceof VarDeclList || n instanceof StmtList){
			return;
		}
		if(n.getClassName().compareTo("Program")==0) {
			if(index != -1) {
				file = new File("./" + name + index + ".dot");
			}else {
				file = new File("./" + name + ".dot");
			}
			FileWriter fw;
			//completes the .dot-file with "}"
			try {
				fw = new FileWriter(file.getAbsoluteFile(),true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.append("}");
				bw.flush();
				list.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		//removes the last inserted element
		else{
			list.remove(list.size()-1);
		}
	}

	/**
	 * Creates a new DumpASTVisitor
	 * 
	 * @param name
	 *            set the name to this
	 */
	public DumpASTVisitor(final String name) {
		super(name);
		list = new ArrayList<String>();
		errors = 0;
		this.name = name;
	}
	
	public DumpASTVisitor(final String name,int ind){
		super(name);
		list = new ArrayList<String>();
		errors = 0;
		this.name = name;
		index = ind;
		
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