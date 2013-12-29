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
	
	//list with ASTNodes
	protected ArrayList<ASTNode> list;
	//list with all IRFunctions
	protected ArrayList<IRFunction> irfuncs;
	//list which contains all VirtualRegister of a IRFunction
	protected ArrayList<VirtualRegister> vrList;
	
	public static IRProgram irp;
	
	protected ArrayList<CLABEL> trueLabels;
	protected ArrayList<CLABEL> falseLabels;
	protected boolean trueFlag;
	protected boolean falseFlag;
	protected boolean visitTrueFlag;
	protected boolean visitFalseFlag;
	
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
		irp = null;
		trueLabels = new ArrayList<CLABEL>();
		falseLabels = new ArrayList<CLABEL>();
	}
	
	public static IRProgram getIRProgram() {
		return irp;
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
		VirtualRegister Ltmp = null;
		if(n.getLValue() instanceof ArrayAccess){
			Ltmp = vrList.get(vrList.size()-1);
		}
		n.getExpr().accept(this, param);
		Operand opRight = null;
		Operand opLeft = null;
		//check which Operand we need for the right side of the assignment
		if(n.getExpr() instanceof Const){
			opRight = new ConstOperand(((Const)n.getExpr()).getNumber(),n.getExpr().getType());
	    }else if(n.getExpr() instanceof Identifier){
	    	opRight = new VariableOperand(n.getExpr().getVariable());
	    }else if(n.getExpr() instanceof ArrayAccess){
	            VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(n.getExpr().getType());
	            opRight = new RegisterOperand(vr);
	            CLOAD load;
	            load = new CLOAD(opRight,new VariableOperand(((ArrayAccess)n.getExpr()).getIdentifier().getVariable()), new RegisterOperand(vrList.get(vrList.size()-1)));   
	            irfuncs.get(irfuncs.size()-1).add(load);
	    }else{
	    	opRight = new RegisterOperand(vrList.get(vrList.size()-1));
	    }
		//check left side
		if(!(n.getLValue() instanceof ArrayAccess)) {
			opLeft = new VariableOperand(n.getLValue().getVariable());
			//create and add new CASSGN-icode
		    CASSGN assgn = new CASSGN(opLeft, opRight);
		    irfuncs.get(irfuncs.size()-1).add(assgn);
		 //ArrayAccess
		}else {
			CSTORE store = new CSTORE(new VariableOperand(((ArrayAccess)n.getLValue()).getIdentifier().getVariable()), new RegisterOperand(Ltmp),opRight);
	    	irfuncs.get(irfuncs.size()-1).add(store);
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
		//create a new IRFunction
		IRFunction irf = new IRFunction(n.getName());
		//create array with VirtualRegisters for this function
		vrList = new ArrayList<VirtualRegister>();
		ParList params = n.getParameterList();
		//add parameters to IRFunction
		for(int i = 0; i< params.size(); i++) {
			irf.addParam(params.get(i).getIdentifier().getVariable());
		}
		//add local variables to IRFunction
		/*Iterator<VarDecl> vdli = n.getBody().getVarDeclList().getVarDecls().iterator();
		while(vdli.hasNext()){
			irf.addLocals(vdli.next().getIdentifier().getVariable());
		}*/
		//add IRFunction in list for all IRFunctions
		irfuncs.add(irf);
		
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
			//check which Operand we need for each expression and add it to the OperandList
			if(it instanceof Const) {
				opList.add(new ConstOperand(((Const) it).getNumber(), it.getType()));
			}else if(it instanceof Identifier) {
				opList.add(new VariableOperand(it.getVariable()));
			}else {
				opList.add(new RegisterOperand(vrList.get(vrList.size()-1)));
			}
		}
		for(int i = 0; i < opList.size()-1; i++) {
			//create for each operand a new VirtualRegister
			VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
			vrList.add(vr);
			//get the size of the i-th dimension of the array
			ConstOperand cons = new ConstOperand(((Const) n.getIdentifier().getType().getDim(i+1)).getNumber(), Type.getIntType());
			//multiply next dimension and current index
			RegisterOperand regOp = new RegisterOperand(vr);
			CMUL mul = new CMUL(regOp, cons, opList.get(i));
			irfuncs.get(irfuncs.size()-1).add(mul);
			//multiply all following dimension with current tmp
			for(int j = i+1; j < opList.size()-1; j++) {
				ConstOperand cons2 = new ConstOperand(((Const) n.getIdentifier().getType().getDim(j+1)).getNumber(), Type.getIntType());
				CMUL mul2 = new CMUL(regOp, cons2, regOp);
				irfuncs.get(irfuncs.size()-1).add(mul2);
			}
			if(i!= 0) {
				VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
				vrList.add(vr2);
				//add result of the last multiplication to the current result-value
				CADD add = new CADD(new RegisterOperand(vr2), regOp, new RegisterOperand(vrList.get(vrList.size()-3)));
			
				irfuncs.get(irfuncs.size()-1).add(add);
			}
		}
		VirtualRegister vr3 = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
		vrList.add(vr3);
		//add last arrayindex to result
		if(opList.size() >= 2) {
			CADD addLast = new CADD(new RegisterOperand(vr3), new RegisterOperand(vrList.get(vrList.size()-2)), opList.get(opList.size()-1));
			irfuncs.get(irfuncs.size()-1).add(addLast);
		//array has only one dimension
		}else {
			CASSGN asgn = new CASSGN(new RegisterOperand(vr3), opList.get(0));
			irfuncs.get(irfuncs.size()-1).add(asgn);
		}
		
		//save the result
		vrList.add(vr3);
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
		
		//create a new VirtualRegister for the current IRFunction
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(n.getType());
		vrList.add(vr);
		//create a new RegisterOperand
		op = new RegisterOperand(vr);
		//create a CCALL-icode; result of FuncCall will be saved in op
		CCALL call = new CCALL(n.getIdentifier().getName(), op);
		//add the new icode
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
			//check which kind of operand we need
			if(it instanceof Identifier) {
				op = new VariableOperand(it.getVariable());
			}else if(it instanceof Const) {
				op = new ConstOperand(((Const) it).getNumber(), it.getType());
			}else {
				op = new RegisterOperand(vrList.get(vrList.size()-1));
			}
			
			//create new CPUSH-icode
			CPUSH push = new CPUSH(op);
			//add the new icode
			irfuncs.get(irfuncs.size()-1).add(push);
		}
		epilog(astnode);
		return null;
	}


	/**
	 * Visit this ASTNode (visitor pattern)ihr eine VarDecl seht, muesst ihr pruefen, obs eine
  lokale Variable ist und die Variable mit addLocal der
  IRFunction hinzufuegen. Das
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final Program astnode, final P param) {
		prolog(astnode);
		for(Decl it : astnode.getDeclarations()) {
			it.accept(this, param);
		}
		irp = new IRProgram();
		//add global variables to IRProgram
		Iterator<Decl> iter = astnode.getDeclarations().iterator();
		while(iter.hasNext()) {
			Decl dec = iter.next();
			if( dec instanceof VarDecl) {
				irp.addGlobal(((VarDecl) dec).getIdentifier().getVariable());
			}
		}
		//add all IRFunctions to IRProgram
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
     * ASTNode to visit
     */
    public R visit(final ReturnStmt n, final P param) {
            prolog(n);
            n.getReturnValue().accept(this, param);
            Type ret = null;
            // search for return type of current function
            for(int i=0; i< list.size();i++){
                    if(list.get(i) instanceof FuncDecl){
                            ret = ((FuncDecl)list.get(i)).getType();
                            break;
                    }
            }
            Operand retOp = null;
            //check wich operand we need for CRET
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
            // create ICode for return and add it to the ICodeList of the current IRFunction
            CRET cret = new CRET(retOp);
            irfuncs.get(irfuncs.size()-1).add(cret);
            epilog(n);
            return null;
    }

    public R visit(Int2Real astnode, P param) {
            prolog(astnode);
            astnode.getExpr().accept(this, param);
            //create new virtual register for the result of the cast
            VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getRealType());
            vrList.add(vr);
            Operand toCast = null;
            //check which operand we have to cast
            if(astnode.getExpr() instanceof Const){
                    toCast = new ConstOperand(((Const)astnode.getExpr()).getNumber(),((Const)astnode.getExpr()).getType());
            }else if( astnode.getExpr() instanceof Identifier){
                    toCast = new VariableOperand(((Identifier)astnode.getExpr()).getVariable());
            }else if(astnode.getExpr() instanceof ArrayAccess){
                    VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
                    toCast = new RegisterOperand(vr2);
                    CLOAD load = new CLOAD(toCast,new VariableOperand(((ArrayAccess)astnode.getExpr()).getIdentifier().getVariable()),new RegisterOperand(vrList.get(vrList.size()-2)));
                    irfuncs.get(irfuncs.size()-1).add(load);
            }else{
                    toCast = new RegisterOperand(vrList.get(vrList.size()-2));
            }
            //create ICode
            CI2R i2r = new CI2R(new RegisterOperand(vr),toCast);
            irfuncs.get(irfuncs.size()-1).add(i2r);
            epilog(astnode);
            return null;
    }
    
    public R visit(Real2Int astnode, P param) {
            prolog(astnode);
            astnode.getExpr().accept(this, param);
            //create virtual register for the result of the cast
            VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getIntType());
            vrList.add(vr);
            Operand toCast = null;
            //check which operand we have to cast
            if(astnode.getExpr() instanceof Const){
                    toCast = new ConstOperand(((Const)astnode.getExpr()).getNumber(),((Const)astnode.getExpr()).getType());
            }else if( astnode.getExpr() instanceof Identifier){
                    toCast = new VariableOperand(((Identifier)astnode.getExpr()).getVariable());
            }else if(astnode.getExpr() instanceof ArrayAccess){
                    VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(Type.getRealType());
                    toCast = new RegisterOperand(vr2);
                    CLOAD load = new CLOAD(toCast,new VariableOperand(((ArrayAccess)astnode.getExpr()).getIdentifier().getVariable()),new RegisterOperand(vrList.get(vrList.size()-2)));
                    irfuncs.get(irfuncs.size()-1).add(load);
            }else{
                    toCast = new RegisterOperand(vrList.get(vrList.size()-2));
            }
            //create ICode
            CR2I r2i = new CR2I(new RegisterOperand(vr),toCast);
            irfuncs.get(irfuncs.size()-1).add(r2i);
            //arrayTmps.clear();
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
		//add local variable
		if(n.getIdentifier().getVariable().getDepth() != 0) {
			irfuncs.get(irfuncs.size()-1).addLocals(n.getIdentifier().getVariable());
		}
		decl(n, param);
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
		System.out.println("FalseFlag: "+falseFlag);
		System.out.println("TrueFlag: "+trueFlag);
		if(n instanceof ORExpr) {
			System.out.println("ORExpr:");
			//check whether an ORExpr is an ancestor of the current node
			/*boolean and = false;
			for(ASTNode ast : list){
				if(ast instanceof ANDExpr && !ast.equals(n)){
					and = true;
					break;
				}
			}*/
			//if(!trueFlag || (and && !(list.get(list.size()-2) instanceof ORExpr))/* && !(list.get(list.size()-3) instanceof ORExpr))&& !falseFlag || (!(!trueFlag && !falseFlag))*/){
				/*CLABEL label = irfuncs.get(irfuncs.size()-1).getLabel();
				trueLabels.add(label);
				System.out.println("trueLabel added! Truesize: " + trueLabels.size());
			}
			//|| ((BinExpr)n.getLeft()).getLeft() instanceof ANDExpr || ((BinExpr)n.getLeft()).getRight() instanceof ORExpr || ((BinExpr)n.getRight()).getLeft() instanceof ANDExpr || ((BinExpr)n.getRight()).getRight() instanceof ORExpr
			if(n.getLeft() instanceof ANDExpr || n.getLeft() instanceof ORExpr || n.getRight() instanceof ANDExpr || n.getRight() instanceof ORExpr){
				trueFlag = true;
			}*/
			n.getLeft().accept(this, param);
			irfuncs.get(irfuncs.size()-1).add(falseLabels.get(falseLabels.size()-1));
			falseLabels.remove(falseLabels.size()-1);
			System.out.println("falseLabel removed! Falsesize: " + falseLabels.size() + "inhalt: " + falseLabels.toString());
			n.getRight().accept(this, param);
		}
		if(n instanceof ANDExpr) {
			System.out.println("ANDExpr");
			//check whether an ANDExpr is an ancestor of the current node
			/*boolean or = false;
			for(ASTNode ast : list){
				if(ast instanceof ORExpr && !ast.equals(n)){
					or = true;
					break;
				}
			}*/
			//if(!falseFlag || (or && !(list.get(list.size()-2)instanceof ANDExpr)) /* && !(list.get(list.size()-3)instanceof ANDExpr) )&& !trueFlag || (!(!trueFlag && !falseFlag))*/){
				/*CLABEL label = irfuncs.get(irfuncs.size()-1).getLabel();
				falseLabels.add(label);
				System.out.println("falseLabel added! Falsesize: " + falseLabels.size());
			}
			if(n.getLeft() instanceof ANDExpr || n.getLeft() instanceof ORExpr || n.getRight() instanceof ANDExpr || n.getRight() instanceof ORExpr){
				falseFlag = true;
			}*/
			n.getLeft().accept(this, param);
			irfuncs.get(irfuncs.size()-1).add(trueLabels.get(trueLabels.size()-1));
			trueLabels.remove(trueLabels.size()-1);
			System.out.println("trueLabel removed! Truesize: " + trueLabels.size()+ "inhalt: " + trueLabels.toString());
			n.getRight().accept(this, param);
		}
		return;
	}
	
	/**
	 * Visits a binary expression
	 * 
	 * @param astnode
	 *            The expression
	 */
	private void binexpr(final BinExpr n, final P param, VirtualRegister[] tmp) {
		n.getLeft().accept(this, param);
		//save value of VirtualRegister of the left child before creating VirtualRegisters for right child
		if(n.getLeft() instanceof BinExpr || n.getLeft() instanceof FuncCall || n.getLeft() instanceof Int2Real || n.getLeft() instanceof Real2Int || n.getLeft() instanceof ArrayAccess){
			tmp[0] = vrList.get(vrList.size()-1);
		}
		n.getRight().accept(this, param);
		//save value of VirtualRegister of the right child 
		if(n.getRight() instanceof BinExpr || n.getRight() instanceof FuncCall || n.getRight() instanceof Int2Real || n.getRight() instanceof Real2Int || n.getRight() instanceof ArrayAccess){
			tmp[1] = vrList.get(vrList.size()-1);
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
		// create memory for tmps of left and right subtree
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);	
		vrList.add(vr);
		//create new icode for the arithmetic expression
		CMUL cm = new CMUL(new RegisterOperand(vr), ops[0], ops[1]);
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
	public R visit(final DIVTerm astnode, final P param) {
		prolog(astnode);
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);		
		vrList.add(vr);
		CDIV cd = new CDIV(new RegisterOperand(vr), ops[0], ops[1]);
		irfuncs.get(irfuncs.size()-1).add(cd);
		epilog(astnode);
		return null;
	}

	
	
	public R visit(final ADDExpr astnode, final P param) {
		prolog(astnode);
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);	
		vrList.add(vr);
		CADD ca = new CADD(new RegisterOperand(vr), ops[0], ops[1]);
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
	public R visit(final SUBExpr astnode, final P param) {
		prolog(astnode);
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		VirtualRegister vr = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getType());
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);	
		vrList.add(vr);
		CSUB cs = new CSUB(new RegisterOperand(vr), ops[0], ops[1]);
		irfuncs.get(irfuncs.size()-1).add(cs);
		epilog(astnode);
		return null;
	}
	
	public void getOps(BinExpr astnode, VirtualRegister[] tmp, Operand[] ops) {
		Operand opLeft = null;
		Operand opRight = null;
		if(astnode.getLeft() instanceof Const) {
			opLeft = new ConstOperand(((Const) astnode.getLeft()).getNumber(), astnode.getLeft().getType());
		}else if(astnode.getLeft() instanceof Identifier) {
			opLeft = new VariableOperand(astnode.getLeft().getVariable());
		}else if(astnode.getLeft() instanceof ArrayAccess) {
			VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getLeft().getType());
			vrList.add(vr2);
			opLeft = new RegisterOperand(vr2);
			CLOAD load = new CLOAD(opLeft, new VariableOperand(((ArrayAccess) astnode.getLeft()).getIdentifier().getVariable()), new RegisterOperand(tmp[0]));
			irfuncs.get(irfuncs.size()-1).add(load);
		}else {
			opLeft = new RegisterOperand(tmp[0]);
		}
			if(astnode.getRight() instanceof Const) {
				opRight = new ConstOperand(((Const) astnode.getRight()).getNumber(), astnode.getRight().getType());
			}else if(astnode.getRight() instanceof Identifier) {
				opRight = new VariableOperand(astnode.getRight().getVariable());
			}else if(astnode.getRight() instanceof ArrayAccess) {
				VirtualRegister vr2 = irfuncs.get(irfuncs.size()-1).getVirtReg(astnode.getRight().getType());
				vrList.add(vr2);
				opRight = new RegisterOperand(vr2);
				CLOAD load;
				load = new CLOAD(opRight, new VariableOperand(((ArrayAccess) astnode.getRight()).getIdentifier().getVariable()), new RegisterOperand(tmp[1]));
				irfuncs.get(irfuncs.size()-1).add(load);
			}else {
					opRight = new RegisterOperand(tmp[1]);
					
			}
			ops[0] = opLeft;
			ops[1] = opRight;
				
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
		irfuncs.get(irfuncs.size()-1).add(trueLabels.get(trueLabels.size()-1));
		astnode.getThenBlock().accept(this, param);
		Block elseblock = astnode.getElseBlock();
		if (elseblock != null) {
			CLABEL label = irfuncs.get(irfuncs.size()-1).getLabel();
			CBRA br = new CBRA(label);
			irfuncs.get(irfuncs.size()-1).add(br);
			irfuncs.get(irfuncs.size()-1).add(falseLabels.get(falseLabels.size()-1));
			elseblock.accept(this, param);
			irfuncs.get(irfuncs.size()-1).add(label);
		}else{
			irfuncs.get(irfuncs.size()-1).add(falseLabels.get(falseLabels.size()-1));
		}
		falseLabels.remove(falseLabels.size()-1);
		System.out.println("in Ifstmt after remove falseLabel, size= " + falseLabels.size());
		trueLabels.remove(trueLabels.size()-1);
		System.out.println("in Ifstmt after remove trueLabel, size= " + trueLabels.size());
		falseFlag = false;
		trueFlag = false;
		visitFalseFlag = false;
		visitTrueFlag = false;
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
		CLABEL label = irfuncs.get(irfuncs.size()-1).getLabel();
		irfuncs.get(irfuncs.size()-1).add(label);
		astnode.getCondition().accept(this, param);
		irfuncs.get(irfuncs.size()-1).add(trueLabels.get(trueLabels.size()-1));
		astnode.getWhileBlock().accept(this, param);
		CBRA br = new CBRA(label);
		irfuncs.get(irfuncs.size()-1).add(br);
		irfuncs.get(irfuncs.size()-1).add(falseLabels.get(falseLabels.size()-1));
		falseLabels.remove(falseLabels.size()-1);
		trueLabels.remove(trueLabels.size()-1);
		falseFlag = false;
		trueFlag = false;
		visitFalseFlag = false;
		visitTrueFlag = false;
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
	public R visit(final ORExpr astnode, final P param) {
		prolog(astnode);
		binexpr(astnode, param);
		epilog(astnode);
		return null;
	}
	
	public void getLabels(BinExpr astnode, CLABEL[] labels) {
		CLABEL label, label2;
		label = null;
		label2 = null;
		if(trueLabels.isEmpty()) {
			label = irfuncs.get(irfuncs.size()-1).getLabel();
			trueLabels.add(label);
			System.out.println("getLabels AND, new trueLabel"  + trueLabels.toString());
		}else {
			label = trueLabels.get(trueLabels.size()-1);
			System.out.println("getLabels vorhandenes trueLabel" + trueLabels.toString());
		}
		
		if(falseLabels.isEmpty()) {
			label2 = irfuncs.get(irfuncs.size()-1).getLabel();
			falseLabels.add(label2);
			System.out.println("getLabels AND, new falseLabel" + falseLabels.toString());
		}else {
			label2 = falseLabels.get(falseLabels.size()-1);
			System.out.println("getLabels vorhandenes falseLabel" + falseLabels.toString());
		}
		/*if(list.get(list.size()-2) instanceof ANDExpr){
			if(trueLabels.isEmpty()) {
				label = irfuncs.get(irfuncs.size()-1).getLabel();
				System.out.println("getLabels AND, new trueLabel" + label);
				trueLabels.add(label);
			}else {
				label = trueLabels.get(trueLabels.size()-1);
				System.out.println("in getLabels AND, isEmpty false, size= " + trueLabels.size()+ "inhalt: " + trueLabels.toString());
			}
			//System.out.println("list.size()-3: " + list.get(list.size()-3));
			//if(!(list.get(list.size()-3) instanceof IfStmt) && !(list.get(list.size()-3) instanceof WhileStmt)){
			if(visitTrueFlag) {	
				label2 = falseLabels.get(falseLabels.size()-1);
				System.out.println("in getLabels AND, kein if vorhandenes false holen, size= " + falseLabels.size()+ "inhalt: " + falseLabels.toString());
			}else {
				label2 = irfuncs.get(irfuncs.size()-1).getLabel();
				falseLabels.add(label2);
				System.out.println("in getLabels AND, if neues false holen, size= " + falseLabels.size()+ "inhalt: " + falseLabels.toString());
				visitTrueFlag = true;
			}	
		}
		
		if(list.get(list.size()-2) instanceof ORExpr){
			label2 = irfuncs.get(irfuncs.size()-1).getLabel();
			falseLabels.add(label2);
			System.out.println("getLabels OR, new falseLabel" + label2);
			System.out.println("list.size()-3: " + list.get(list.size()-3));
			if(visitFalseFlag) {
			//if(!(list.get(list.size()-3) instanceof IfStmt) && !(list.get(list.size()-3) instanceof WhileStmt)){
				label = trueLabels.get(trueLabels.size()-1);
				System.out.println("in getLabels OR, kein if vorhandenes true holen, size= " + trueLabels.size()+ "inhalt: " + trueLabels.toString());
			}else {
				label = irfuncs.get(irfuncs.size()-1).getLabel();
				trueLabels.add(label);
				System.out.println("in getLabels OR, if neues true holen, size= " + trueLabels.size() + "inhalt: " + trueLabels.toString());
				visitFalseFlag = true;
			}	
		}*/
		
		labels[0] = label;
		labels[1] = label2;
	}
	public void getLabels2(BinExpr astnode, CLABEL[] labels) {
		CLABEL label, label2;
		//get the right true label
		//in ORExpr
		if(list.get(list.size()-2) instanceof ORExpr){
			label = trueLabels.get(trueLabels.size()-1);
		//in ANDExpr
		}else{
			if(!(list.get(list.size()-2) instanceof IfStmt) && !(list.get(list.size()-2) instanceof WhileStmt)){
				if(((ANDExpr)list.get(list.size()-2)).getRight().equals(astnode)){
					trueFlag = true;
				}
			}
			//check whether an ORExpr is an ancestor of the current node
			boolean or = false;
			for(ASTNode ast : list){
				if(ast instanceof ORExpr){
					or = true;
					break;
				}
			}
			//create new label if no ORExpr was found or if current node is on the left side of AND
			if(!or || ((ANDExpr)list.get(list.size()-2)).getLeft().equals(astnode)){
				label = irfuncs.get(irfuncs.size()-1).getLabel();
				trueLabels.add(label);
			}else{
				label = trueLabels.get(trueLabels.size()-1);
			}
		}
		//get the right false label
		//in ANDExpr
		if(list.get(list.size()-2) instanceof ANDExpr){
			label2 = falseLabels.get(falseLabels.size()-1);
		//in ORExpr
		}else{
			if(!(list.get(list.size()-2) instanceof IfStmt) && !(list.get(list.size()-2) instanceof WhileStmt)){
				if(((ORExpr)list.get(list.size()-2)).getRight().equals(astnode)){
					falseFlag = true;
					System.out.println("falseFlag true");
				}
			}
			//check whether an ANDExpr is an ancestor of the current node
			boolean and = false;
			for(ASTNode ast : list){
				if(ast instanceof ANDExpr){
					and = true;
					break;
				}
			}
			//create new label if no ANDExpr was found or if current node is on the left side of OR
			if(!and || ((ORExpr)list.get(list.size()-2)).getLeft().equals(astnode)){
				label2 = irfuncs.get(irfuncs.size()-1).getLabel();
				falseLabels.add(label2);
			}else{
				label2 = falseLabels.get(falseLabels.size()-1);
			}
		}
		labels[0] = label;
		labels[1] = label2;
	}

	/**
	 * Visit this ASTNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final LEQExpr astnode, final P param) {
		prolog(astnode);
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);
		CLABEL labels[] = new CLABEL[2];
		getLabels(astnode, labels);
		CBLE lesseq = new CBLE(ops[0], ops[1], labels[0]);
		CBRA br = new CBRA(labels[1]);
		irfuncs.get(irfuncs.size()-1).add(lesseq);
		irfuncs.get(irfuncs.size()-1).add(br);
		epilog(astnode);
		return null;
	}

	/**
	 * Visit this ASopRight = new RegisterOperand(rightTmp);TNode (visitor pattern)
	 * 
	 * @param astnode
	 *            ASTNode to visit
	 */
	public R visit(final LTExpr astnode, final P param) {
		prolog(astnode);
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);
		CLABEL labels[] = new CLABEL[2];
		getLabels(astnode, labels);
		CBLT less = new CBLT(ops[0], ops[1], labels[0]);
		CBRA br = new CBRA(labels[1]);
		irfuncs.get(irfuncs.size()-1).add(less);
		irfuncs.get(irfuncs.size()-1).add(br);
		epilog(astnode);
		return null;
	}
	
	public R visit(final GEQExpr astnode, final P param) {
		prolog(astnode);
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);
		CLABEL labels[] = new CLABEL[2];
		getLabels(astnode, labels);
		CBGE greatereq = new CBGE(ops[0], ops[1], labels[0]);
		CBRA br = new CBRA(labels[1]);
		irfuncs.get(irfuncs.size()-1).add(greatereq);
		irfuncs.get(irfuncs.size()-1).add(br);
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
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);
		CLABEL labels[] = new CLABEL[2];
		getLabels(astnode, labels);
		CBGT greater = new CBGT(ops[0], ops[1], labels[0]);
		CBRA br = new CBRA(labels[1]);
		irfuncs.get(irfuncs.size()-1).add(greater);
		irfuncs.get(irfuncs.size()-1).add(br);
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
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);	
		CLABEL labels[] = new CLABEL[2];
		getLabels(astnode, labels);
		CBEQ eq = new CBEQ(ops[0], ops[1], labels[0]);
		CBRA br = new CBRA(labels[1]);
		irfuncs.get(irfuncs.size()-1).add(eq);
		irfuncs.get(irfuncs.size()-1).add(br);
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
		VirtualRegister[] tmp = new VirtualRegister[2];
		binexpr(astnode, param,tmp);
		Operand[] ops = new Operand[2];
		getOps(astnode, tmp, ops);	
		CLABEL labels[] = new CLABEL[2];
		getLabels(astnode, labels);
		CBNE neq = new CBNE(ops[0], ops[1], labels[0]);
		CBRA br = new CBRA(labels[1]);
		irfuncs.get(irfuncs.size()-1).add(neq);
		irfuncs.get(irfuncs.size()-1).add(br);
		epilog(astnode);
		return null;
	}
	
}