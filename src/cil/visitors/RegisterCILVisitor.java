package cil.visitors;

import java.util.ArrayList;
import java.util.Iterator;

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
    	
    	System.out.println("targetop: " + icode.getTargetOperand().toString());
    	System.out.println("op: " + icode.getOperand().toString());
    	
        process(icode);
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
    	CASSGN assgn4 = null;
    	CASSGN assgn5 = null;
    	CADD add = null;
    	ICode assgnicode = null;
    	if(!irfuncs.get(irfuncs.size()-1).freeHardregs.isEmpty()) {
    		if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
	    		VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getTargetOperand()).getRegister();
	        	HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getLeftOperand().getType());
	        	HardwareRegister hr2 = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getRightOperand().getType());
	            assgn = new CASSGN(new RegisterOperand(hr), icode.getLeftOperand());
	            //irfuncs.get(irfuncs.size()-1).add(assgn);
	            assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            //irfuncs.get(irfuncs.size()-1).add(assgn2);
	            add = new CADD(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            //irfuncs.get(irfuncs.size()-1).add(add);
	            assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	           // irfuncs.get(irfuncs.size()-1).add(assgn3);
	            //irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	            
    		}
    	
	    	Iterator it =  irfuncs.get(irfuncs.size()-1).getIcodeIterator();
	 
	    	while(it.hasNext()) {
	    		assgnicode = (ICode) it.next();
	    		if(assgnicode instanceof CASSGN) {
	    			if(((CUnary) assgnicode).getOperand() instanceof RegisterOperand) {
	    	    		System.out.println("blubRegOp");
	    	    		if(((RegisterOperand) ((CUnary) assgnicode).getOperand()).getRegister() instanceof VirtualRegister) {
	    	    			System.out.println("blubVirtReg");
	    	    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) ((CUnary) assgnicode).getOperand()).getRegister();
	    	    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
	    	    			assgn4 = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
	    	    			//irfuncs.get(irfuncs.size()-1).add(assgn4);
	    	    			System.out.println("targetoperand: " +((CTarget) icode).getTargetOperand());
	    	    			assgn5 = new CASSGN(((CTarget) assgnicode).getTargetOperand(), new RegisterOperand(hr));
	    		            //irfuncs.get(irfuncs.size()-1).add(assgn5);
	    		            //irfuncs.get(irfuncs.size()-1).removeIcode(assicode);
	    	    			 irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	    	        	}
	    	    	}
	    		}
	    	}
    	}
    	 irfuncs.get(irfuncs.size()-1).add(assgn);
    	 irfuncs.get(irfuncs.size()-1).add(assgn2);
    	 irfuncs.get(irfuncs.size()-1).add(add);
    	 irfuncs.get(irfuncs.size()-1).add(assgn3);
    	 irfuncs.get(irfuncs.size()-1).add(assgn4);
    	 irfuncs.get(irfuncs.size()-1).add(assgn5);
    	 irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    	 irfuncs.get(irfuncs.size()-1).removeIcode(assgnicode);
        process(icode);
        return;
    }

}
