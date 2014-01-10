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
    	
    	for(int i = 0; i < icodefunc.vrcounter; i++) {
    		//System.out.println("inhalt virtreg: " + icodefunc.getVirtReg(i));
    	}
    	irfuncs.add(icodefunc);
    	
    	Iterator it = icodefunc.getIcodeIterator();
    	while(it.hasNext()) {
    		System.out.println("icodes: " + it.next());
    	}
    
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
    		System.out.println("icodesschleife: " + nextICode);
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
    	
    	System.out.println("targetop: " + icode.getTargetOperand().toString());
    	System.out.println("op: " + icode.getOperand().toString());
    	boolean regopFlag = false;
    	boolean hregFlag = false;
		
		/*if(icode.getTargetOperand() instanceof RegisterOperand) {
			if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister && (icode.getOperand() instanceof VariableOperand)) {
    			/*if((icode.getOperand() instanceof RegisterOperand)) {
    				regopFlag = true;
    				if(((RegisterOperand) icode.getOperand()).getRegister() instanceof HardwareRegister) {
    					hregFlag = true;
    				}
    			}
    			if(!regopFlag || (regopFlag && !hregFlag)) {
    				System.out.println("blubVirtReg");
        			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
        			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
        			//CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
        			//irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
        			System.out.println("targetoperand: " +icode.getTargetOperand());
        			CASSGN assgn2 = new CASSGN(new RegisterOperand(hr), icode.getOperand());
    	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
    	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
        			 irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
    			//}
				
        	}
		}
		//regopFlag = false;
		//hregFlag = false;*/
        process(icode);
       
        if(((CUnary) icode).getOperand() instanceof RegisterOperand) {
    		System.out.println("blubRegOp");
    		if(((RegisterOperand) ((CUnary) icode).getOperand()).getRegister() instanceof VirtualRegister && (icode.getTargetOperand() instanceof VariableOperand)) {
    			System.out.println("blubVirtReg");
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) ((CUnary) icode).getOperand()).getRegister();
    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
    			CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
    			System.out.println("icodeassgn: " + icode);
    			System.out.println("targetoperand: " +((CTarget) icode).getTargetOperand());
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
    	if(!irfuncs.get(irfuncs.size()-1).freeHardregs.isEmpty()) {
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
	    		VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
	        	HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getLeftOperand().getType());
	        	HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getRightOperand().getType());
	            assgn = new CASSGN(new RegisterOperand(hr), icode.getLeftOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
	            assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            add = new CADD(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, add);
	            assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
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
    public void visit(final CSUB icode) {
    	if(!irfuncs.get(irfuncs.size()-1).freeHardregs.isEmpty()) {
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
	    		VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
	        	HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getLeftOperand().getType());
	        	HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getRightOperand().getType());
	            CASSGN assgn = new CASSGN(new RegisterOperand(hr), icode.getLeftOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn);
	            CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            CSUB sub = new CSUB(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, sub);
	            CASSGN assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	            
    		}
    	}
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CMUL icode) {
    	if(!irfuncs.get(irfuncs.size()-1).freeHardregs.isEmpty()) {
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
	    		VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
	        	HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getLeftOperand().getType());
	        	HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getRightOperand().getType());
	            CASSGN assgn = new CASSGN(new RegisterOperand(hr), icode.getLeftOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
	            CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            CMUL mul = new CMUL(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, mul);
	            CASSGN assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	            
    		}
    	}
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CDIV icode) {

    	if(!irfuncs.get(irfuncs.size()-1).freeHardregs.isEmpty()) {
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
	    		VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
	        	HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getLeftOperand().getType());
	        	HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getRightOperand().getType());
	            CASSGN assgn = new CASSGN(new RegisterOperand(hr), icode.getLeftOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
	            CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            CDIV div = new CDIV(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, div);
	            CASSGN assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	            
    		}
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
    	if(icode.getValueOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getValueOperand()).getRegister() instanceof VirtualRegister) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getValueOperand()).getRegister();
    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
    			CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
    			if(icode.getOffsetOperand() instanceof RegisterOperand) {
    	    		if(((RegisterOperand) icode.getOffsetOperand()).getRegister() instanceof VirtualRegister) {
    	    			VirtualRegister vr2 = (VirtualRegister) ((RegisterOperand) icode.getOffsetOperand()).getRegister();
    	    			HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
    	    			CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
    	    			store = new CSTORE(icode.getBaseOperand(), new RegisterOperand(hr2), new RegisterOperand(hr));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, store);
    	    			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
    	    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	    			
    	    		}
    			}else {
    				store = new CSTORE(icode.getBaseOperand(), icode.getOffsetOperand(), new RegisterOperand(hr));
    				irfuncs.get(irfuncs.size()-1).addBefore(icode, store);
    			}
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
    	if(icode.getTargetOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
    			//CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
    		//irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
    			if(icode.getOffsetOperand() instanceof RegisterOperand) {
    	    		if(((RegisterOperand) icode.getOffsetOperand()).getRegister() instanceof VirtualRegister) {
    	    			VirtualRegister vr2 = (VirtualRegister) ((RegisterOperand) icode.getOffsetOperand()).getRegister();
    	    			HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(vr2.getType());
    	    			CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), new RegisterOperand(vr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
    	    			load = new CLOAD(new RegisterOperand(hr), icode.getBaseOperand(), new RegisterOperand(hr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, load);
    	    			CASSGN assgn3 = new CASSGN(new RegisterOperand(vr2), new RegisterOperand(hr2));
    	    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn3);
    	    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
    	    			
    	    		}
    			}else {
    				load = new CLOAD(new RegisterOperand(hr), icode.getBaseOperand(), icode.getOffsetOperand());
    				irfuncs.get(irfuncs.size()-1).addBefore(icode, load);
        			
    			}
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
    	if(icode.getOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getOperand()).getRegister() instanceof VirtualRegister) {
		    	VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getOperand()).getRegister();
				HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				CRET ret = new CRET(new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, ret);
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
    public void visit(final CCALL icode) {
    	if(icode.getTargetOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
		    	VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
				HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
				CCALL call = new CCALL(icode.getName(), new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, call);
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
    		CBEQ eq = new CBEQ(new RegisterOperand(hr1), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode,eq);
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
    		CASSGN assgn2 = new CASSGN(new RegisterOperand(vr1), new RegisterOperand(hr1));
			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    		irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr1.getName());
    	}else if(rightOp){
    		CBEQ eq = new CBEQ(icode.getLeftOperand(), new RegisterOperand(hr2), icode.getLabel());
    		irfuncs.get(irfuncs.size()-1).addBefore(icode, eq);
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
    	if(icode.getOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getOperand()).getRegister() instanceof VirtualRegister) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getOperand()).getRegister();
				HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
				CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
				CPUSH push = new CPUSH(new RegisterOperand(hr));
				irfuncs.get(irfuncs.size()-1).addBefore(icode, push);
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
    	if(icode.getOperand() instanceof RegisterOperand) {
    		if(((RegisterOperand) icode.getOperand()).getRegister() instanceof VirtualRegister) {
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getOperand()).getRegister();
				HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
				HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(Type.getIntType());
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
    		HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(Type.getIntType());
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
