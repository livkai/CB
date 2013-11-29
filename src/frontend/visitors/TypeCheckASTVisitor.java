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
	public R visit(final AssgnStmt n, final P param) {
		prolog(n);
		n.getLValue().accept(this, param);
		n.getExpr().accept(this, param);
		Type leftType = ((AssgnStmt) n).getLValue().getType();
		Type rightType = ((AssgnStmt) n).getExpr().getType();
		//throw exception if the left side of the assignment is an identifier of a function declaration or a funcCall
		if((((AssgnStmt) n).getLValue().getVariable() != null && ((AssgnStmt) n).getLValue().getVariable().getType() instanceof FuncType) || ((AssgnStmt) n).getLValue() instanceof FuncCall){
			throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ "Cannot assign a value to a function!");											
		}//throw exception if the right side of the assignment is an identifier of a function declaration
		if(((AssgnStmt) n).getExpr().getVariable()!= null && ((AssgnStmt) n).getExpr().getVariable().getType() instanceof FuncType){
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
		//add type-info to constants
		if(((Const) n).getNumber().contains(".")) {
			((Const) n).setType(Type.getRealType());
		}else {
			((Const) n).setType(Type.getIntType());
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
	public R visit(final FuncDecl n, final P param) {
		prolog(n);
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
		//add type-info to Identifiers
		((Identifier) n).setType(((Identifier) n).getVariable().getType());
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
		//add type-info for ArrayAccess
		((ArrayAccess) n).setType(((ArrayAccess) n).getIdentifier().getType().getArrayElementType());
		//check dimension of the array
		if(((ArrayAccess)n).getNumIndices() != ((ArrayAccess) n).getIdentifier().getType().getNumDimensions()){
			throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ n.getIdentifier().getName() + ": Wrong number of indices, array has " + ((ArrayAccess) n).getIdentifier().getType().getNumDimensions() + " dimensions");
		}
		for(int i = 0; i < ((ArrayAccess)n).getNumIndices(); i++) {
			//indices must be from type int
			if(((ArrayAccess) n).getIndex(i).getVariable() != null && ((ArrayAccess) n).getIndex(i).getVariable().getType() instanceof FuncType){
				throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((Identifier)((ArrayAccess) n).getIndex(i)).getName() + " is a function, not a variable!");						
			}
			if(!(n.getIndex(i).getType().isIntType())) {
				throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ "Indices of array must be an int type");
			}
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
		if(!(((FuncCall) n).getIdentifier().getType() instanceof FuncType)){
			throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + " is a variable, not a function!");						
		}
	
		ArgList al = ((FuncCall) n).getArgList();
		FuncType ft = (FuncType) n.getIdentifier().getType();
		n.setType(ft.getRetType());
		if((ft.getParSize() == 0 && al.size() != 0) || (ft.getParSize() != 0 && al.size() == 0)) {
			throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + ": Wrong number of arguments!");
		}else{
			//no match between FuncCall and FuncDecl -> different number of arguments
			if(ft.getParSize() != al.size()){
				throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + ": Wrong number of arguments! Right number of arguments is: " + ft.getParSize());			
			}else{
				for(int i=0;i<ft.getParSize();i++){
					//argument must be a primitive type					
					if(!(al.get(i).getType().isPrimitiveType()) || 
					  ((al.get(i).getVariable() != null) && al.get(i).getVariable().getType() instanceof FuncType)){
						throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((FuncCall) n).getIdentifier().getName() + ": Only primitive types are allowed as arguments in function call");
					} 
					else if(!ft.getParType(i).equals(al.get(i).getType())){
						//children have different types -> add cast node
						if(ft.getParType(i).isIntType()){
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
		Type ret = ((ReturnStmt) n).getReturnValue().getType();
		//only primitive types allowed in ReturnStmt and also no FuncCalls allowed 
		if(!ret.isPrimitiveType() || 
		  (((ReturnStmt) n).getReturnValue().getVariable() != null && ((ReturnStmt) n).getReturnValue().getVariable().getType() instanceof FuncType)){
			throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ "Returntype must be a primitive type");
		} 
		for(int i = list.size()-1;i>0;i--){
			if(list.get(i) instanceof FuncDecl){
				FuncDecl fd = (FuncDecl) list.get(i);
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
		if(((VarDecl) n).getType().isArrayType()){
			//arraysize must be constant
			for(int i=0;i<((VarDecl) n).getType().getNumDimensions();i++){
				if(!(((VarDecl) n).getType().getDim(i) instanceof Const)){
					throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((VarDecl) n).getIdentifier().getName() + ": The size of the dimensions of an array must be a constant!");
				}
			}
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
		
		Type leftType = ((BinExpr) n).getLeft().getType();
		Type rightType = ((BinExpr) n).getRight().getType();
		//throw exception if one of the children is an identifier of a function declaration
		if(((BinExpr) n).getLeft().getVariable() != null && ((BinExpr) n).getLeft().getVariable().getType() instanceof FuncType){
			throw new InternalCompilerErrorRuntimeException(n.getFile() + ": "+ n.getLine() + ": "+ ((Identifier)((BinExpr) n).getLeft()).getName() + " is a function, not a variable!");						
		}
		if(((BinExpr) n).getRight().getVariable() != null && ((BinExpr) n).getRight().getVariable().getType() instanceof FuncType){
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
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}
}