package frontend.visitors;


import java.util.ArrayList;
import java.util.Iterator;

import cil.*;
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
	protected VirtualRegister leftTmp;
	protected ArrayList<ASTNode> list;
	protected ArrayList<IRFunction> irfuncs;
	protected ArrayList<VirtualRegister> vrList;
	protected ArrayList<VirtualRegister> arrayTmps;

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
		vrList = new ArrayList<VirtualRegister>();
		arrayTmps = new ArrayList<VirtualRegister>();
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
		vrList = new ArrayList<VirtualRegister>();
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
		ArrayList<Operand> opList = new ArrayList<Operand>();
		for(Expr it : n.getIndices()) {
			it.accept(this, param);
			if(it instanceof Const) {
				opList.add(new ConstOperand(((Const) it).getNumber(), it.getType()));
			}else if(it instanceof Identifier) {
				opList.add(new VariableOperand(it.getVariable()));
			}else {
				opList.add(new RegisterOperand(vrList.get(vrList.size()-1)));
			}
		}
		for(int i = 0; i < opList.size()-1; i++) {
			VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
			vrList.add(vr);
			ConstOperand cons = new ConstOperand(((Const) n.getIdentifier().getType().getDim(i)).getNumber(), Type.getIntType());
			CMUL mul = new CMUL(new RegisterOperand(vr), cons, opList.get(i));
			irfuncs.get(irfuncs.size()-1).add(mul);
			if(i!= 0) {
				VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
				vrList.add(vr2);
				CADD add = new CADD(new RegisterOperand(vr2), new RegisterOperand(vr), new RegisterOperand(vrList.get(vrList.size()-3)));
				irfuncs.get(irfuncs.size()-1).add(add);
			}
			
		}
		VirtualRegister vr3 = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
		vrList.add(vr3);
		CADD addLast = new CADD(new RegisterOperand(vr3), new RegisterOperand(vrList.get(vrList.size()-2)), opList.get(opList.size()-1));
		irfuncs.get(irfuncs.size()-1).add(addLast);
		arrayTmps.add(vr3);
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
		Operand op = null;
		//if(list.get(list.size()-2) instanceof AssgnStmt) {
			/*if(((AssgnStmt) list.get(list.size()-2)).getLValue() instanceof Identifier) {
				op = new VariableOperand(((Identifier)((AssgnStmt)list.get(list.size()-2)).getLValue()).getVariable());
			}*/
		//}
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(n.getType());
		vrList.add(vr);
		op = new RegisterOperand(vr);
		CCALL call = new CCALL(n.getIdentifier().getName(), op);
		irfuncs.get(irfuncs.size()-1).add(call);
		epilog(n);
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
			Operand op = null;
			if(it instanceof Identifier) {
				op = new VariableOperand(it.getVariable());
			}else if(it instanceof Const) {
				op = new ConstOperand(((Const) it).getNumber(), it.getType());
			}else {
				op = new RegisterOperand(vrList.get(vrList.size()-1));
			}
			CPUSH push = new CPUSH(op);
			irfuncs.get(irfuncs.size()-1).add(push);
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
		return (R) irp;
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
		Type ret = null;
		for(int i=0; i< list.size();i++){
			if(list.get(i) instanceof FuncDecl){
				ret = ((FuncDecl)list.get(i)).getType();
				break;
			}
		}
		Operand retOp = null;
		if(n.getReturnValue() instanceof Const){
			retOp = new ConstOperand(((Const)n.getReturnValue()).getNumber(),ret); 
		}else if(n.getReturnValue() instanceof Identifier){
			retOp = new VariableOperand(n.getReturnValue().getVariable());
		}else if(n.getReturnValue() instanceof ArrayAccess){
			VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(ret);
			retOp = new RegisterOperand(vr);
			CLOAD load = new CLOAD(retOp,new VariableOperand(((ArrayAccess)n.getReturnValue()).getIdentifier().getVariable()),new RegisterOperand(vrList.get(vrList.size()-1)));
			irfuncs.get(irfuncs.size()-1).add(load);
		}else{
			retOp = new RegisterOperand(vrList.get(vrList.size()-1));
		}
		CRET cret = new CRET(retOp);
		irfuncs.get(irfuncs.size()-1).add(cret);
		epilog(n);
		return null;
	}

	public R visit(Int2Real astnode, P param) {
		// TODO Auto-generated method stub
		prolog(astnode);
		astnode.getExpr().accept(this, param);
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getRealType());
		vrList.add(vr);
		Operand toCast = null;
		if(astnode.getExpr() instanceof Const){
			toCast = new ConstOperand(((Const)astnode.getExpr()).getNumber(),((Const)astnode.getExpr()).getType());
		}else if( astnode.getExpr() instanceof Identifier){
			toCast = new VariableOperand(((Identifier)astnode.getExpr()).getVariable());
		}else if(astnode.getExpr() instanceof ArrayAccess){
			VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
			toCast = new RegisterOperand(vr2);
			CLOAD load = new CLOAD(toCast,new VariableOperand(((ArrayAccess)astnode.getExpr()).getIdentifier().getVariable()),new RegisterOperand(arrayTmps.get(0)));
			irfuncs.get(irfuncs.size()-1).add(load);
		}else{
			toCast = new RegisterOperand(vrList.get(vrList.size()-2));
		}
		CI2R i2r = new CI2R(new RegisterOperand(vr),toCast);
		irfuncs.get(irfuncs.size()-1).add(i2r);
		arrayTmps.clear();
		epilog(astnode);
		return null;
	}
	
	public R visit(Real2Int astnode, P param) {
		// TODO Auto-generated method stub
		prolog(astnode);
		astnode.getExpr().accept(this, param);
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
		vrList.add(vr);
		Operand toCast = null;
		if(astnode.getExpr() instanceof Const){
			toCast = new ConstOperand(((Const)astnode.getExpr()).getNumber(),((Const)astnode.getExpr()).getType());
		}else if( astnode.getExpr() instanceof Identifier){
			toCast = new VariableOperand(((Identifier)astnode.getExpr()).getVariable());
		}else if(astnode.getExpr() instanceof ArrayAccess){
			VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getRealType());
			toCast = new RegisterOperand(vr2);
			CLOAD load = new CLOAD(toCast,new VariableOperand(((ArrayAccess)astnode.getExpr()).getIdentifier().getVariable()),new RegisterOperand(arrayTmps.get(0)));
			irfuncs.get(irfuncs.size()-1).add(load);
		}else{
			toCast = new RegisterOperand(vrList.get(vrList.size()-2));
		}
		CR2I r2i = new CR2I(new RegisterOperand(vr),toCast);
		irfuncs.get(irfuncs.size()-1).add(r2i);
		arrayTmps.clear();
		epilog(astnode);
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
		if(n.getLeft() instanceof BinExpr || n.getLeft() instanceof FuncCall || n.getLeft() instanceof Int2Real || n.getLeft() instanceof Real2Int){
			leftTmp = vrList.get(vrList.size()-1);
		}
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
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
		vrList.add(vr);
		Operand opLeft = null;
		Operand opRight = null;
		if(astnode.getLeft() instanceof Const) {
			opLeft = new ConstOperand(((Const) astnode.getLeft()).getNumber(), astnode.getLeft().getType());
		}else if(astnode.getLeft() instanceof Identifier) {
			opLeft = new VariableOperand(astnode.getLeft().getVariable());
		}else if(astnode.getLeft() instanceof ArrayAccess) {
			VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
			//vrList.add(vr2);
			opLeft = new RegisterOperand(vr2);
			CLOAD load = new CLOAD(opLeft, new VariableOperand(((ArrayAccess) astnode.getLeft()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(0)));
			irfuncs.get(irfuncs.size()-1).add(load);
		}else {
			opLeft = new RegisterOperand(leftTmp);
			/*if(astnode.getRight() instanceof BinExpr || astnode.getRight() instanceof FuncCall) {
				opLeft = new RegisterOperand(vrList.get(vrList.size()-3));
				//opRight = new RegisterOperand(vrList.get(vrList.size()-2));
			}else {
				opLeft = new RegisterOperand(vrList.get(vrList.size()-2));
			}*/
		}
		//if(opRight == null) {
			if(astnode.getRight() instanceof Const) {
				opRight = new ConstOperand(((Const) astnode.getRight()).getNumber(), astnode.getRight().getType());
			}else if(astnode.getRight() instanceof Identifier) {
				opRight = new VariableOperand(astnode.getRight().getVariable());
			}else if(astnode.getRight() instanceof ArrayAccess) {
				VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
				//vrList.add(vr2);
				opRight = new RegisterOperand(vr2);
				CLOAD load;
				if(astnode.getLeft() instanceof ArrayAccess){
					load = new CLOAD(opRight, new VariableOperand(((ArrayAccess) astnode.getRight()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(1)));
				}else{
					load = new CLOAD(opRight, new VariableOperand(((ArrayAccess) astnode.getRight()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(0)));
				}
				irfuncs.get(irfuncs.size()-1).add(load);
			}else {
					opRight = new RegisterOperand(vrList.get(vrList.size()-2));
			}
				
		//}
		arrayTmps.clear();
		CMUL cm = new CMUL(new RegisterOperand(vr), opLeft, opRight);
		irfuncs.get(irfuncs.size()-1).add(cm);
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
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
		vrList.add(vr);
		Operand opLeft = null;
		Operand opRight = null;
		if(astnode.getLeft() instanceof Const) {
			opLeft = new ConstOperand(((Const) astnode.getLeft()).getNumber(), astnode.getLeft().getType());
		}else if(astnode.getLeft() instanceof Identifier) {
			opLeft = new VariableOperand(astnode.getLeft().getVariable());
		}else if(astnode.getLeft() instanceof ArrayAccess) {
			VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
			//vrList.add(vr2);
			opLeft = new RegisterOperand(vr2);
			CLOAD load = new CLOAD(opLeft, new VariableOperand(((ArrayAccess) astnode.getLeft()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(0)));
			irfuncs.get(irfuncs.size()-1).add(load);
		}else {
			opLeft = new RegisterOperand(leftTmp);
			/*if(astnode.getRight() instanceof BinExpr || astnode.getRight() instanceof FuncCall) {
				opLeft = new RegisterOperand(vrList.get(vrList.size()-3));
				//opRight = new RegisterOperand(vrList.get(vrList.size()-2));
			}else {
				opLeft = new RegisterOperand(vrList.get(vrList.size()-2));
			}*/
		}
		//if(opRight == null) {
			if(astnode.getRight() instanceof Const) {
				opRight = new ConstOperand(((Const) astnode.getRight()).getNumber(), astnode.getRight().getType());
			}else if(astnode.getRight() instanceof Identifier) {
				opRight = new VariableOperand(astnode.getRight().getVariable());
			}else if(astnode.getRight() instanceof ArrayAccess) {
				VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
				//vrList.add(vr2);
				opRight = new RegisterOperand(vr2);
				CLOAD load;
				if(astnode.getLeft() instanceof ArrayAccess){
					load = new CLOAD(opRight, new VariableOperand(((ArrayAccess) astnode.getRight()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(1)));
				}else{
					load = new CLOAD(opRight, new VariableOperand(((ArrayAccess) astnode.getRight()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(0)));
				}
				irfuncs.get(irfuncs.size()-1).add(load);
			}else {
					opRight = new RegisterOperand(vrList.get(vrList.size()-2));
			}
				
		//}
		arrayTmps.clear();
		CDIV cd = new CDIV(new RegisterOperand(vr), opLeft, opRight);
		irfuncs.get(irfuncs.size()-1).add(cd);
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
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
		vrList.add(vr);
		Operand opLeft = null;
		Operand opRight = null;
		if(astnode.getLeft() instanceof Const) {
			opLeft = new ConstOperand(((Const) astnode.getLeft()).getNumber(), astnode.getLeft().getType());
		}else if(astnode.getLeft() instanceof Identifier) {
			opLeft = new VariableOperand(astnode.getLeft().getVariable());
		}else if(astnode.getLeft() instanceof ArrayAccess) {
			VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
			//vrList.add(vr2);
			opLeft = new RegisterOperand(vr2);
			CLOAD load = new CLOAD(opLeft, new VariableOperand(((ArrayAccess) astnode.getLeft()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(0)));
			irfuncs.get(irfuncs.size()-1).add(load);
		}else {
			opLeft = new RegisterOperand(leftTmp);
			/*if(astnode.getRight() instanceof BinExpr || astnode.getRight() instanceof FuncCall) {
				opLeft = new RegisterOperand(vrList.get(vrList.size()-3));
				//opRight = new RegisterOperand(vrList.get(vrList.size()-2));
			}else {
				opLeft = new RegisterOperand(vrList.get(vrList.size()-2));
			}*/
		}
		//if(opRight == null) {
			if(astnode.getRight() instanceof Const) {
				opRight = new ConstOperand(((Const) astnode.getRight()).getNumber(), astnode.getRight().getType());
			}else if(astnode.getRight() instanceof Identifier) {
				opRight = new VariableOperand(astnode.getRight().getVariable());
			}else if(astnode.getRight() instanceof ArrayAccess) {
				VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
				//vrList.add(vr2);
				opRight = new RegisterOperand(vr2);
				CLOAD load;
				if(astnode.getLeft() instanceof ArrayAccess){
					load = new CLOAD(opRight, new VariableOperand(((ArrayAccess) astnode.getRight()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(1)));
				}else{
					load = new CLOAD(opRight, new VariableOperand(((ArrayAccess) astnode.getRight()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(0)));
				}
				irfuncs.get(irfuncs.size()-1).add(load);
			}else {
					opRight = new RegisterOperand(vrList.get(vrList.size()-2));
			}
				
		//}
		arrayTmps.clear();
		CADD ca = new CADD(new RegisterOperand(vr), opLeft, opRight);
		irfuncs.get(irfuncs.size()-1).add(ca);
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
	//	if(list.get(list.size()-2) instanceof BinExpr){
			VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
			vrList.add(vr);
			Operand opLeft = null;
			Operand opRight = null;
			if(astnode.getLeft() instanceof Const) {
				opLeft = new ConstOperand(((Const) astnode.getLeft()).getNumber(), astnode.getLeft().getType());
			}else if(astnode.getLeft() instanceof Identifier) {
				opLeft = new VariableOperand(astnode.getLeft().getVariable());
			}else if(astnode.getLeft() instanceof ArrayAccess) {
				VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
				//vrList.add(vr2);
				opLeft = new RegisterOperand(vr2);
				CLOAD load = new CLOAD(opLeft, new VariableOperand(((ArrayAccess) astnode.getLeft()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(0)));
				irfuncs.get(irfuncs.size()-1).add(load);
			}else {
				opLeft = new RegisterOperand(leftTmp);
				/*if(astnode.getRight() instanceof BinExpr || astnode.getRight() instanceof FuncCall) {
					opLeft = new RegisterOperand(vrList.get(vrList.size()-3));
					//opRight = new RegisterOperand(vrList.get(vrList.size()-2));
				}else {
					opLeft = new RegisterOperand(vrList.get(vrList.size()-2));
				}*/
			}
			//if(opRight == null) {
				if(astnode.getRight() instanceof Const) {
					opRight = new ConstOperand(((Const) astnode.getRight()).getNumber(), astnode.getRight().getType());
				}else if(astnode.getRight() instanceof Identifier) {
					opRight = new VariableOperand(astnode.getRight().getVariable());
				}else if(astnode.getRight() instanceof ArrayAccess) {
					VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
					//vrList.add(vr2);
					opRight = new RegisterOperand(vr2);
					CLOAD load;
					if(astnode.getLeft() instanceof ArrayAccess){
						load = new CLOAD(opRight, new VariableOperand(((ArrayAccess) astnode.getRight()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(1)));
					}else{
						load = new CLOAD(opRight, new VariableOperand(((ArrayAccess) astnode.getRight()).getIdentifier().getVariable()), new RegisterOperand(arrayTmps.get(0)));
					}
					irfuncs.get(irfuncs.size()-1).add(load);
				}else {
						opRight = new RegisterOperand(vrList.get(vrList.size()-2));
				}
					
			//}
			arrayTmps.clear();
			CSUB cs = new CSUB(new RegisterOperand(vr), opLeft, opRight);
			irfuncs.get(irfuncs.size()-1).add(cs);
			
		//}
		epilog(astnode);
		return null;
	}
}