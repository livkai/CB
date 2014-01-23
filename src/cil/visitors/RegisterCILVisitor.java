package cil.visitors;

import java.util.ArrayList;
import java.util.Iterator;

import common.Type;

import cil.*;

public class RegisterCILVisitor extends CILVisitorAdapter {

	//list with all IRFunctions
	protected ArrayList<IRFunction> irfuncs;
		
	public RegisterCILVisitor(String name) {
		super(name);
		irfuncs = new ArrayList<IRFunction>();
		// TODO Auto-generated constructor stub
	}
	
	
	 /**
     * Visit this ICodeFunction (visitor pattern)
     * 
     * @param icodefunc
     *            ICodeFunction to visit
     */
    public void visit(final IRFunction icodefunc) {
	//add current function in local list
    	irfuncs.add(icodefunc);
    
    	// Beware, if the accept/visit(ICode) method below adds a new
    	// instruction directly after icode,
    	// the icode = next construct causes the new insn to not
    	// be visited.
    	// If your accept/visit method deletes the insn directly following icode,
    	// you need to set nextICode properly in the accept/visit method to
    	// make sure we don't visit a deleted instruction.
    	nextICode = null;
    	for (ICode icode = icodefunc.getFirstInsn(); icode != null; icode = nextICode) {
    		nextICode = icode.next;
    		currentICode = icode;
    		icode.accept(this);
    	}

    	return;
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CASSGN icode) {
        process(icode);
       
	//check whether the right side of the assignment is a Register
        if(((CUnary) icode).getOperand() instanceof RegisterOperand) {
        	//check if the register is a virtual one and left side is a variable (const and register makes no sense)
    		if(((RegisterOperand) ((CUnary) icode).getOperand()).getRegister() instanceof VirtualRegister && (icode.getTargetOperand() instanceof VariableOperand)) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) ((CUnary) icode).getOperand()).getRegister();
    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
    			//assignment from virtReg to hardReg
    			CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
    			//assignment from hardReg to variable
    			CASSGN assgn2 = new CASSGN(((CTarget) icode).getTargetOperand(), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn2);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			 irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
        	}
    	}
        return;
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CADD icode) {
    	CASSGN assgn = null;
    	CASSGN assgn2 = null;
    	CASSGN assgn3 = null;
    	CADD add = null;
    		//check whether target is a virtReg
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
	    		VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
	    		HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getLeftOperand().getType());
	        	HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getRightOperand().getType());
	        	//copy left/right operands in hardRegs
	            assgn = new CASSGN(new RegisterOperand(hr), icode.getLeftOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
	            assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            add = new CADD(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, add);
	            //copy result of addition from hardReg to virtReg
	            assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	            
    	}
    	process(icode);
        return;
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CSUB icode) {
    		//check whether target is a VirtReg
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
	    		VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
	        	HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getLeftOperand().getType());
	        	HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getRightOperand().getType());
	            //copy left/right operands in hardRegs
	        	CASSGN assgn = new CASSGN(new RegisterOperand(hr), icode.getLeftOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn);
	            CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            CSUB sub = new CSUB(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, sub);
	            //copy result of subtraction from hardReg in virtReg
	            CASSGN assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	            
    		}
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CMUL icode) {
    		//check whether target is a VirtReg
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
	    		VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
	        	HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg("%eax",icode.getLeftOperand().getType());
	        	HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg("%edx",icode.getRightOperand().getType());
	            //copy left/right operands in hardRegs
	        	CASSGN assgn = new CASSGN(new RegisterOperand(hr), icode.getLeftOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
	            CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            CMUL mul = new CMUL(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, mul);
	            //copy result of mult from hardReg in virtReg
	            CASSGN assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	            
    		}
    	
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CDIV icode) {
    		//check whether target is a virt Reg
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
	    		VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
	        	HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg("%eax", icode.getLeftOperand().getType());
	        	HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg("%edx", icode.getRightOperand().getType());
	            //copy left/right operands in hardRegs
	        	CASSGN assgn = new CASSGN(new RegisterOperand(hr), icode.getLeftOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
	            CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            CDIV div = new CDIV(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, div);
	            //copy result of div from hardReg to virtReg
	            CASSGN assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	            
    		}
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CSTORE icode) {
    	CSTORE store = null;
    	//check whether the value is virtReg
    	if(icode.getValueOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getValueOperand()).getRegister() instanceof VirtualRegister) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getValueOperand()).getRegister();
    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
    			//copy value from virtReg to hardReg
    			CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
    			//check whether the offset is stored as virtReg
    			if(icode.getOffsetOperand() instanceof RegisterOperand) {
    	    		if(((RegisterOperand) icode.getOffsetOperand()).getRegister() instanceof VirtualRegister) {
    	    			VirtualRegister vr2 = (VirtualRegister) ((RegisterOperand) icode.getOffsetOperand()).getRegister();
    	    			HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
    	    			//copy offset in hardReg
    	    			CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
    	    			store = new CSTORE(icode.getBaseOperand(), new RegisterOperand(hr2), new RegisterOperand(hr));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, store);
    	    			//copy offset back from hardReg to virtReg
    	    			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
    	    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	    			
    	    		}
    			}else {
    				//offset is not a virtReg
    				//store hardReg in array
    				store = new CSTORE(icode.getBaseOperand(), icode.getOffsetOperand(), new RegisterOperand(hr));
    				irfuncs.get(irfuncs.size()-1).addBefore(icode, store);
    			}
    			//copy value back from hardReg to virtReg
    			CASSGN assgn4 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn4);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
        	}
    	}
        process(icode);
        return;
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CLOAD icode) {
    	CLOAD load = null;
    	//check wether target is a virtReg
    	if(icode.getTargetOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
    			//check whether the offset is stored as virtReg
    			if(icode.getOffsetOperand() instanceof RegisterOperand) {
    	    		if(((RegisterOperand) icode.getOffsetOperand()).getRegister() instanceof VirtualRegister) {
    	    			VirtualRegister vr2 = (VirtualRegister) ((RegisterOperand) icode.getOffsetOperand()).getRegister();
    	    			HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
    	    			//copy offset from virtReg to hardReg
    	    			CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
    	    			load = new CLOAD(new RegisterOperand(hr), icode.getBaseOperand(), new RegisterOperand(hr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, load);
    	    			//copy offset back from hardReg to virtReg
    	    			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
    	    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	    			
    	    		}
    			}else {
    				//offset is not a virtReg
    				//load array content to hardReg
    				load = new CLOAD(new RegisterOperand(hr), icode.getBaseOperand(), icode.getOffsetOperand());
    				irfuncs.get(irfuncs.size()-1).addBefore(icode, load);
        			
    			}
    			//copy array content from hardReg to virtReg 
    			CASSGN assgn4 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn4);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
        	}
    	}
        process(icode);
        return;
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CRET icode) {
    	//check wether virtReg should be returned
    	if(icode.getOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getOperand()).getRegister() instanceof VirtualRegister) {
		    	VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getOperand()).getRegister();
				HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
				//copy operand from virtReg to hardReg
				CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				CRET ret = new CRET(new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, ret);
				//copy operand back from hardReg to virtReg
	//			CASSGN assgn2 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
			//	irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
    		}
    	}
	    process(icode);
        return;
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CCALL icode) {
    	//check wether the result should be stored in a virtREg
    	if(icode.getTargetOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
		    	VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
				HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
				//set targetOperand of CCALL to hardReg
				CCALL call = new CCALL(icode.getName(), new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, call);
				//copy result from hardReg to virtReg
				CASSGN assgn = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
    		}
    	}
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBEQ icode) {
    	boolean leftOp = false;
    	boolean rightOp = false;
    	HardwareRegister hr1 = null;
    	HardwareRegister hr2 = null;
    	VirtualRegister vr1 = null;
    	VirtualRegister vr2 = null;
    	//check whether leftOp is a virtReg
    	if(icode.getLeftOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getLeftOperand()).getRegister() instanceof VirtualRegister) {
    			vr1 = (VirtualRegister) ((RegisterOperand) icode.getLeftOperand()).getRegister();
				hr1 = irfuncs.get(irfuncs.size()-1).getHardReg(vr1.getType());
				//copy leftOperand from virtReg to hardReg
				CASSGN assgn = new CASSGN(new RegisterOperand(hr1), new RegisterOperand(vr1));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				leftOp = true;
    		}
    	}
    	//check whether rightOp is a virtReg
    	if(icode.getRightOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getRightOperand()).getRegister() instanceof VirtualRegister) {
    			vr2 = (VirtualRegister) ((RegisterOperand) icode.getRightOperand()).getRegister();
				hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
				//copy rightOperand from virtReg to hardReg
				CASSGN assgn = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
				irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn);
				rightOp = true;
    		}
    	}
    	if(leftOp && rightOp) {
    		CBEQ eq = new CBEQ(new RegisterOperand(hr1), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,eq);
    		//copy left/right operand bck from hardRegs to virtRegs
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    		
    	}
    	else if(leftOp) {
    		CBEQ eq = new CBEQ(new RegisterOperand(hr1), icode.getRightOperand(), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,eq);
    		//copy back left operand from hardReg to virtReg
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    	}else if(rightOp){
    		CBEQ eq = new CBEQ(icode.getLeftOperand(), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode, eq);
    		//copy back right operand from hardReg to virtReg
    		CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	}
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBGE icode) {
    	boolean leftOp = false;
    	boolean rightOp = false;
    	HardwareRegister hr1 = null;
    	HardwareRegister hr2 = null;
    	VirtualRegister vr1 = null;
    	VirtualRegister vr2 = null;
    
    	if(icode.getLeftOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getLeftOperand()).getRegister() instanceof VirtualRegister) {
    			vr1 = (VirtualRegister) ((RegisterOperand) icode.getLeftOperand()).getRegister();
				hr1 = irfuncs.get(irfuncs.size()-1).getHardReg(vr1.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr1), new RegisterOperand(vr1));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				leftOp = true;
    		}
    	}
    	if(icode.getRightOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getRightOperand()).getRegister() instanceof VirtualRegister) {
    			vr2 = (VirtualRegister) ((RegisterOperand) icode.getRightOperand()).getRegister();
				hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
				irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn);
				rightOp = true;
    		}
    	}
    	if(leftOp && rightOp) {
    		CBGE geq = new CBGE(new RegisterOperand(hr1), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,geq);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    		
    	}
    	else if(leftOp) {
    		CBGE geq = new CBGE(new RegisterOperand(hr1), icode.getRightOperand(), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,geq);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    	}else if(rightOp){
    		CBGE geq = new CBGE(icode.getLeftOperand(), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode, geq);
    		CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	}
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBGT icode) {
    	boolean leftOp = false;
    	boolean rightOp = false;
    	HardwareRegister hr1 = null;
    	HardwareRegister hr2 = null;
    	VirtualRegister vr1 = null;
    	VirtualRegister vr2 = null;
    
    	if(icode.getLeftOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getLeftOperand()).getRegister() instanceof VirtualRegister) {
    			vr1 = (VirtualRegister) ((RegisterOperand) icode.getLeftOperand()).getRegister();
				hr1 = irfuncs.get(irfuncs.size()-1).getHardReg(vr1.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr1), new RegisterOperand(vr1));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				leftOp = true;
    		}
    	}
    	if(icode.getRightOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getRightOperand()).getRegister() instanceof VirtualRegister) {
    			vr2 = (VirtualRegister) ((RegisterOperand) icode.getRightOperand()).getRegister();
				hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
				irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn);
				rightOp = true;
    		}
    	}
    	if(leftOp && rightOp) {
    		CBGT gt = new CBGT(new RegisterOperand(hr1), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,gt);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    		
    	}
    	else if(leftOp) {
    		CBGT gt = new CBGT(new RegisterOperand(hr1), icode.getRightOperand(), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,gt);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    	}else if(rightOp){
    		CBGT gt = new CBGT(icode.getLeftOperand(), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode, gt);
    		CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	}
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBLE icode) {
    	boolean leftOp = false;
    	boolean rightOp = false;
    	HardwareRegister hr1 = null;
    	HardwareRegister hr2 = null;
    	VirtualRegister vr1 = null;
    	VirtualRegister vr2 = null;
    
    	if(icode.getLeftOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getLeftOperand()).getRegister() instanceof VirtualRegister) {
    			vr1 = (VirtualRegister) ((RegisterOperand) icode.getLeftOperand()).getRegister();
				hr1 = irfuncs.get(irfuncs.size()-1).getHardReg(vr1.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr1), new RegisterOperand(vr1));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				leftOp = true;
    		}
    	}
    	if(icode.getRightOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getRightOperand()).getRegister() instanceof VirtualRegister) {
    			vr2 = (VirtualRegister) ((RegisterOperand) icode.getRightOperand()).getRegister();
				hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
				irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn);
				rightOp = true;
    		}
    	}
    	if(leftOp && rightOp) {
    		CBLE le = new CBLE(new RegisterOperand(hr1), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,le);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    		
    	}
    	else if(leftOp) {
    		CBLE le = new CBLE(new RegisterOperand(hr1), icode.getRightOperand(), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,le);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    	}else if(rightOp){
    		CBLE le = new CBLE(icode.getLeftOperand(), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode, le);
    		CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	}
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBLT icode) {
    	boolean leftOp = false;
    	boolean rightOp = false;
    	HardwareRegister hr1 = null;
    	HardwareRegister hr2 = null;
    	VirtualRegister vr1 = null;
    	VirtualRegister vr2 = null;
    
    	if(icode.getLeftOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getLeftOperand()).getRegister() instanceof VirtualRegister) {
    			vr1 = (VirtualRegister) ((RegisterOperand) icode.getLeftOperand()).getRegister();
				hr1 = irfuncs.get(irfuncs.size()-1).getHardReg(vr1.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr1), new RegisterOperand(vr1));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				leftOp = true;
    		}
    	}
    	if(icode.getRightOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getRightOperand()).getRegister() instanceof VirtualRegister) {
    			vr2 = (VirtualRegister) ((RegisterOperand) icode.getRightOperand()).getRegister();
				hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
				irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn);
				rightOp = true;
    		}
    	}
    	if(leftOp && rightOp) {
    		CBLT lt = new CBLT(new RegisterOperand(hr1), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,lt);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    		
    	}
    	else if(leftOp) {
    		CBLT lt = new CBLT(new RegisterOperand(hr1), icode.getRightOperand(), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,lt);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    	}else if(rightOp){
    		CBLT lt = new CBLT(icode.getLeftOperand(), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode, lt);
    		CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	}
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBNE icode) {
    	boolean leftOp = false;
    	boolean rightOp = false;
    	HardwareRegister hr1 = null;
    	HardwareRegister hr2 = null;
    	VirtualRegister vr1 = null;
    	VirtualRegister vr2 = null;
    
    	if(icode.getLeftOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getLeftOperand()).getRegister() instanceof VirtualRegister) {
    			vr1 = (VirtualRegister) ((RegisterOperand) icode.getLeftOperand()).getRegister();
				hr1 = irfuncs.get(irfuncs.size()-1).getHardReg(vr1.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr1), new RegisterOperand(vr1));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				leftOp = true;
    		}
    	}
    	if(icode.getRightOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getRightOperand()).getRegister() instanceof VirtualRegister) {
    			vr2 = (VirtualRegister) ((RegisterOperand) icode.getRightOperand()).getRegister();
				hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
				irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn);
				rightOp = true;
    		}
    	}
    	if(leftOp && rightOp) {
    		CBNE neq = new CBNE(new RegisterOperand(hr1), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,neq);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    		
    	}
    	else if(leftOp) {
    		CBNE neq = new CBNE(new RegisterOperand(hr1), icode.getRightOperand(), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,neq);
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    	}else if(rightOp){
    		CBNE neq = new CBNE(icode.getLeftOperand(), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode, neq);
    		CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	}
        process(icode);
        return;
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CPUSH icode) {
    	//check whether the operand is a virtReg
    	if(icode.getOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getOperand()).getRegister() instanceof VirtualRegister) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getOperand()).getRegister();
				HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
				//copy operand from virtReg to hardReg
				CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				CPUSH push = new CPUSH(new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, push);
				//copy back operand from hardReg to virtReg
				CASSGN assgn2 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
				
    		}
    	}
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CR2I icode) {
    	//check whether the operand is virtReg
    	if(icode.getOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getOperand()).getRegister() instanceof VirtualRegister) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getOperand()).getRegister();
				HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
				HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(Type.getIntType());
				//copy virtReg to hardReg
				CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				//set target from cast to hardReg
				CR2I r2i = new CR2I(new RegisterOperand(hr2),new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, r2i);
				//copy result of cast from hardReg to virtReg
				CASSGN assgn1 = new CASSGN(icode.getTargetOperand(), new RegisterOperand(hr2));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn1);
				//copy back operand from hardReg to virtReg
				CASSGN assgn2 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
				
    		}
    	}else{
    		//operand is not a virtReg
    		HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(Type.getIntType());
    		//set target of cast to hardReg
			CR2I r2i = new CR2I(new RegisterOperand(hr),icode.getOperand());
			irfuncs.get(irfuncs.size()-1).addBefore(icode, r2i);
			//copy result of cast from hardReg to virtReg
			CASSGN assgn1 = new CASSGN(icode.getTargetOperand(), new RegisterOperand(hr));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn1);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
    	}
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CI2R icode) {
    	if(icode.getOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getOperand()).getRegister() instanceof VirtualRegister) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getOperand()).getRegister();
				HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
				HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(Type.getRealType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				CR2I r2i = new CR2I(new RegisterOperand(hr2),new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, r2i);
				CASSGN assgn1 = new CASSGN(icode.getTargetOperand(), new RegisterOperand(hr2));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn1);
				CASSGN assgn2 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
				
    		}
    	}else{
    		HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(Type.getRealType());
			CR2I r2i = new CR2I(new RegisterOperand(hr),icode.getOperand());
			irfuncs.get(irfuncs.size()-1).addBefore(icode, r2i);
			CASSGN assgn1 = new CASSGN(icode.getTargetOperand(), new RegisterOperand(hr));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn1);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
    	}
        process(icode);
        return;
    }
    
    
  
    
    /**
     * What to do with an ICode
     * 
     * @param icode
     *            ICode to process
     */
    protected void process(final ICode icode) {
        return;
    }


}
