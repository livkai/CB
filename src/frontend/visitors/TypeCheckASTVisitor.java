package frontend.visitors;


import java.util.ArrayList;
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
public class TypeCheckASTVisitor<P, R> extends ASTVisitorAdapter<P, R> implements ASTVisitor<P, R> {

	/** variable hold number of errors that occured during the Visitor run */
	protected int errors;
	/** name of the visitor */
	protected  String name;
	protected ArrayList<ASTNode> list;

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
			if(n instanceof BinExpr) {
				Type leftType = ((BinExpr) n).getLeft().getType();
				Type rightType = ((BinExpr) n).getRight().getType();
				//throw exception if one of the children is an identifier of a function declaration
				if(((BinExpr) n).getLeft().getVariable() != null && ((BinExpr) n).getLeft().getVariable().getFlag() != 1){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((Identifier)((BinExpr) n).getLeft()).getName() + " is a function, not a variable!");						
				}
				if(((BinExpr) n).getRight().getVariable() != null && ((BinExpr) n).getRight().getVariable().getFlag() != 1){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((Identifier)((BinExpr) n).getRight()).getName() + " is a function, not a variable!");						
				}
				if((leftType != null && leftType.isArrayType()) || (rightType != null && rightType.isArrayType())){
					//invalid type
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ "Invalid arithmetic operation between arraytype and primitive type");
				}
				if(n instanceof ADDExpr || n instanceof SUBExpr || n instanceof MULTerm || n instanceof DIVTerm){
					//add type-info from child-nodes
					if(leftType.equals(rightType)) {
						((BinExpr) n).setType(leftType);
					}else {
						//children have different types -> add cast node
						if(leftType.equals(Type.getIntType())) {
							Int2Real i2r = new Int2Real(((BinExpr) n).getLeft().getFile(), ((BinExpr) n).getLeft().getLine(), ((BinExpr) n).getLeft());
							((BinExpr) n).setLeft(i2r);
							((BinExpr) n).setType(Type.getRealType());
						}else if(leftType.equals(Type.getRealType())){
							Int2Real i2r = new Int2Real(((BinExpr) n).getRight().getFile(), ((BinExpr) n).getRight().getLine(), ((BinExpr) n).getRight());
							((BinExpr) n).setRight(i2r);
							((BinExpr) n).setType(Type.getRealType());
						}
						
					}
				}
			}
			else if(n instanceof Const) {
				//add type-info to constants
				if(((Const) n).getNumber().contains(".")) {
					((Const) n).setType(Type.getRealType());
				}else {
					((Const) n).setType(Type.getIntType());
				}
			}else if(n instanceof Identifier) {
				//add type-info to Identifiers
				((Identifier) n).setType(((Identifier) n).getVariable().getType());
			}else if(n instanceof AssgnStmt) {
				Type leftType = ((AssgnStmt) n).getLValue().getType();
				Type rightType = ((AssgnStmt) n).getExpr().getType();
				
				//throw exception if the left side of the assignment is an identifier of a function declaration or a funcCall
				if((((AssgnStmt) n).getLValue().getVariable() != null && ((AssgnStmt) n).getLValue().getVariable().getFlag() != 1) || ((AssgnStmt) n).getLValue() instanceof FuncCall){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ "Cannot assign a value to a function!");											
				}//throw exception if the right side of the assignment is an identifier of a function declaration
				if(((AssgnStmt) n).getExpr().getVariable()!= null && ((AssgnStmt) n).getExpr().getVariable().getFlag() != 1){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((Identifier)((AssgnStmt) n).getExpr()).getName() + " is a function, not a variable!");
				}
				//children have different types -> add cast node
				if(!(leftType.equals(rightType))) {
					if(leftType.equals(Type.getIntType())) {
						Real2Int r2i = new Real2Int(((AssgnStmt) n).getExpr().getFile(), ((AssgnStmt) n).getExpr().getLine(), ((AssgnStmt) n).getExpr());
						((AssgnStmt) n).setExpr(r2i);
					}else if(leftType.equals(Type.getRealType())){
						Int2Real i2r = new Int2Real(((AssgnStmt) n).getExpr().getFile(), ((AssgnStmt) n).getExpr().getLine(), ((AssgnStmt) n).getExpr());
						((AssgnStmt) n).setExpr(i2r);
					}else {
						//invalid type
						throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ "Invalid assignment between arraytype and primitive type");
					}
				}
			}else if(n instanceof ReturnStmt){
				Type ret = ((ReturnStmt) n).getReturnValue().getType();
				//only primitive types allowed in ReturnStmt and also no FuncCalls allowed 
				/*if(((ReturnStmt) n).getReturnValue().getVariable() != null) {
					System.out.println("getRetVariable: "  + ((ReturnStmt) n).getReturnValue().getVariable() + "my flag: " + ((ReturnStmt) n).getReturnValue().getVariable().getFlag());
					
				}
				System.out.println("blubb!");
				*/if(!ret.isPrimitiveType() || 
				  (((ReturnStmt) n).getReturnValue().getVariable() != null && ((ReturnStmt) n).getReturnValue().getVariable().getFlag() != 1)){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ "Returntype must be a primitive type");
				} 
/*				if(((ReturnStmt) n).getReturnValue().getVariable() != null) {
					if(((ReturnStmt) n).getReturnValue().getVariable().getFlag() != 1){
						throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ "Returntype must be a primitive type");
					}
				}*/
				for(int i = list.size()-1;i>0;i--){
					if(list.get(i) instanceof FuncDecl){
						FuncDecl fd = (FuncDecl) list.get(i);
						//no arrays allowed in FuncDecl
			/*			if(fd.getType().isArrayType()){
							throw new InternalCompilerErrorRuntimeException(fd.getFile() + ": "+ fd.getLine() + ": "+ fd.getIdentifier().getName() + ": Returntype must be a primitive type");
						}*/
						//test if ReturnStmt has same type as defined in FuncDecl
						// -> if not, add cast node
						if(!ret.equals(fd.getType())){
							if(fd.getType().equals(Type.getIntType())){
								Real2Int r2i = new Real2Int(((ReturnStmt) n).getReturnValue().getFile(), ((ReturnStmt) n).getReturnValue().getLine(), ((ReturnStmt) n).getReturnValue());
								((ReturnStmt) n).setReturnValue(r2i);
							}else{
								Int2Real i2r = new Int2Real(((ReturnStmt) n).getReturnValue().getFile(), ((ReturnStmt) n).getReturnValue().getLine(), ((ReturnStmt) n).getReturnValue());
								((ReturnStmt) n).setReturnValue(i2r);
							}
						}
					}
				}
			} else if(n instanceof FuncCall) {
				if(((FuncCall) n).getIdentifier().getVariable().getFlag() != 0){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + " is a variable, not a function!");						
				}
				//add type-info to FuncCall
				((FuncCall) n).setType(((FuncCall) n).getIdentifier().getType());
				Program root = (Program) list.get(0);
				Iterable<Decl> it = root.getDeclarations();
				//search in all declarations for the corresponding FuncDecl 
				for(Decl dec: it) {
					if(dec instanceof FuncDecl){
						if(dec.getIdentifier().getName().compareTo(((FuncCall) n).getIdentifier().getName())== 0){
							ParList pl = ((FuncDecl) dec).getParameterList();
							ArgList al = ((FuncCall) n).getArgList();
							//no match between FuncCall and FuncDecl if only one has no arguments
							if((pl.size() == 0 && al != null) || (pl.size() != 0 && al == null)) {
								throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + ": Wrong number of arguments!");
							}else{
								//no match between FuncCall and FuncDecl -> different number of arguments
								if((al != null) && (pl.size() != al.size())){
									throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + ": Wrong number of arguments! Right number of arguments is: " + ((FuncDecl)dec).getParameterList().size());			
								}else{
									for(int i=0;i<pl.size();i++){
									/*	//parameter must be a primitive type
										if(!(pl.get(i).getType().isPrimitiveType()) || 
										     pl.get(i).getIdentifier().getVariable().getFlag() != 1){
											throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + "Only primitive types are allowed as parameters in declaration of function");
										}*/
										//argument must be a primitive type					
										if(!(al.get(i).getType().isPrimitiveType()) || 
										  ((al.get(i).getVariable() != null) && al.get(i).getVariable().getFlag() != 1)){
											throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + ": Only primitive types are allowed as arguments in function call");
										} 
										else if(!pl.get(i).getType().equals(al.get(i).getType())){
											//children have different types -> add cast node
											if(pl.get(i).getType().isIntType()){
												Real2Int r2i = new Real2Int(al.get(i).getFile(),al.get(i).getLine(),al.get(i));
												al.addParam(r2i, i);
											}else {
												Int2Real i2r = new Int2Real(al.get(i).getFile(),al.get(i).getLine(),al.get(i));
												al.addParam(i2r, i);
											}
										}
									}
								}
							}
						}
					}
				}
			} else if(n instanceof ArrayAccess) {
				//add type-info for ArrayAccess
				((ArrayAccess) n).setType(((ArrayAccess) n).getIdentifier().getType().getArrayElementType());
				//check dimension of the array
				if(((ArrayAccess)n).getNumIndices() != ((ArrayAccess) n).getIdentifier().getType().getNumDimensions()){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + ": Wrong number of indices, array has " + ((ArrayAccess) n).getIdentifier().getType().getNumDimensions() + " dimensions");
				}
				for(int i = 0; i < ((ArrayAccess)n).getNumIndices(); i++) {
					//indices must be from type int
					if(((ArrayAccess) n).getIndex(i).getVariable() != null && ((ArrayAccess) n).getIndex(i).getVariable().getFlag() != 1){
						throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((Identifier)((ArrayAccess) n).getIndex(i)).getName() + " is a function, not a variable!");						
					}
					if(!((ArrayAccess) n).getIndex(i).getType().isIntType()) {
						throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ "Indices of array must be an int type");
					}
				}
			}else if(n instanceof FuncDecl){
				ParList pl = ((FuncDecl) n).getParameterList();
				//parameter must be a primitive type
				for(int i=0; i< pl.size();i++){
					if(!pl.get(i).getType().isPrimitiveType()){
						throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((VarDecl) pl.get(i)).getIdentifier().getName() + ": Only primitive types are allowed as parameters in declaration of function");
					}
				}
				//return type of function must be a primitive type
				if(((FuncDecl) n).getType().isArrayType()){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncDecl) n).getIdentifier().getName() + ": Only primitive types are allowed as return type of a function!");
				}
			}else if(n instanceof VarDecl){
				if(((VarDecl) n).getType().isArrayType()){
					//arraysize must be constant
					for(int i=0;i<((VarDecl) n).getType().getNumDimensions();i++){
						if(!(((VarDecl) n).getType().getDim(i) instanceof Const)){
							throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((VarDecl) n).getIdentifier().getName() + ": The size of the dimensions of an array must be a constant!");
						}
					}
				}
			}
			
		}
	}

	/**
	 * Creates a new TypeCheckASTVisitor
	 * 
	 * @param name
	 *            set the name to this
	 */
	public TypeCheckASTVisitor(final String name) {
		super(name);
		errors = 0;
		this.name = name;
		list = new ArrayList<ASTNode>();
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