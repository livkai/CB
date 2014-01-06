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
    	boolean regopFlag = false;
    	boolean hregFlag = false;
		if(((CUnary) icode).getOperand() instanceof RegisterOperand) {
    		System.out.println("blubRegOp");
    		if(((RegisterOperand) ((CUnary) icode).getOperand()).getRegister() instanceof VirtualRegister) {
    			System.out.println("blubVirtReg");
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) ((CUnary) icode).getOperand()).getRegister();
    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
    			CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
    			irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn);
    			System.out.println("targetoperand: " +((CTarget) icode).getTargetOperand());
    			CASSGN assgn2 = new CASSGN(((CTarget) icode).getTargetOperand(), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).addBefore(icode, assgn2);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			 irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
        	}
    	}
		if(icode.getTargetOperand() instanceof RegisterOperand) {
			if(((RegisterOperand) icode.getTargetOperand()).getRegister() instanceof VirtualRegister) {
    			if((icode.getOperand() instanceof RegisterOperand)) {
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
    			}
				
        	}
		}
		//regopFlag = false;
		//hregFlag = false;
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
	            irfuncs.get(irfuncs.size()-1).add(assgn);
	            assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).add(assgn2);
	            add = new CADD(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).add(add);
	            assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).add(assgn3);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr2.getName());
	            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	            
    		}
    	
	    	Iterator it =  irfuncs.get(irfuncs.size()-1).getIcodeIterator();
	 
	    	/*while(it.hasNext()) {
	    		assgnicode = (ICode) it.next();
	    		if(assgnicode instanceof CASSGN) {
	    			if(((CUnary) assgnicode).getOperand() instanceof RegisterOperand) {
	    	    		System.out.println("blubRegOp");
	    	    		if(((RegisterOperand) ((CUnary) assgnicode).getOperand()).getRegister() instanceof VirtualRegister) {
	    	    			System.out.println("blubVirtReg");
	    	    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) ((CUnary) assgnicode).getOperand()).getRegister();
	    	    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
	    	    			assgn4 = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
	    	    			irfuncs.get(irfuncs.size()-1).add(assgn4);
	    	    			System.out.println("targetoperand: " +((CTarget) icode).getTargetOperand());
	    	    			assgn5 = new CASSGN(((CTarget) assgnicode).getTargetOperand(), new RegisterOperand(hr));
	    		            irfuncs.get(irfuncs.size()-1).add(assgn5);
	    		            irfuncs.get(irfuncs.size()-1).removeIcode(assgnicode);
	    	    			 irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
	    	        	}
	    	    	}
	    		}
	    	}*/
    	}
    /*	 irfuncs.get(irfuncs.size()-1).add(assgn);
    	 irfuncs.get(irfuncs.size()-1).add(assgn2);
    	 irfuncs.get(irfuncs.size()-1).add(add);
    	 irfuncs.get(irfuncs.size()-1).add(assgn3);
    	 irfuncs.get(irfuncs.size()-1).add(assgn4);
    	 irfuncs.get(irfuncs.size()-1).add(assgn5);
    	 irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    	 irfuncs.get(irfuncs.size()-1).removeIcode(assgnicode);
      */  process(icode);
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
	            irfuncs.get(irfuncs.size()-1).add(assgn);
	            CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).add(assgn2);
	            CSUB sub = new CSUB(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).add(sub);
	            CASSGN assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).add(assgn3);
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
	            irfuncs.get(irfuncs.size()-1).add(assgn);
	            CASSGN assgn2 = new CASSGN(new RegisterOperand(hr2), icode.getRightOperand());
	            irfuncs.get(irfuncs.size()-1).add(assgn2);
	            CMUL mul = new CMUL(new RegisterOperand(hr), new RegisterOperand(hr), new RegisterOperand(hr2));
	            irfuncs.get(irfuncs.size()-1).add(mul);
	            CASSGN assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(hr));
	            irfuncs.get(irfuncs.size()-1).add(assgn3);
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
	        	if(irfuncs.get(irfuncs.size()-1).freeHardregs.contains("eax") && irfuncs.get(irfuncs.size()-1).freeHardregs.contains("edx")) {
		    		HardwareRegister eax = irfuncs.get(irfuncs.size()-1).getHardReg("%eax", icode.getLeftOperand().getType());
		        	HardwareRegister edx = irfuncs.get(irfuncs.size()-1).getHardReg("%edx", icode.getLeftOperand().getType());
		        	HardwareRegister hrDivisor = irfuncs.get(irfuncs.size()-1).getHardReg(icode.getRightOperand().getType());
		            CASSGN assgn = new CASSGN(new RegisterOperand(eax), icode.getLeftOperand());
		            irfuncs.get(irfuncs.size()-1).add(assgn);
		            ConstOperand cons = new ConstOperand("0", Type.getIntType());
		            CASSGN assgn1 = new CASSGN(new RegisterOperand(edx), cons);
		            irfuncs.get(irfuncs.size()-1).add(assgn1);
		            CASSGN assgn2 = new CASSGN(new RegisterOperand(hrDivisor), icode.getRightOperand());
		            irfuncs.get(irfuncs.size()-1).add(assgn2);
		            //eigentl. muesste man eax:edx als operand uebergeben...
		            CDIV div = new CDIV(new RegisterOperand(eax), new RegisterOperand(eax), new RegisterOperand(hrDivisor));
		            irfuncs.get(irfuncs.size()-1).add(div);
		            CASSGN assgn3 = new CASSGN(new RegisterOperand(vr), new RegisterOperand(eax));
		            irfuncs.get(irfuncs.size()-1).add(assgn3);
		            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
		            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hrDivisor.getName());
		            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, edx.getName());
		            irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, eax.getName());
	        	}
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
    	if(icode.getValueOperand() instanceof RegisterOperand) {
    		System.out.println("blubCSTORE");
    		if(((RegisterOperand) icode.getValueOperand()).getRegister() instanceof VirtualRegister) {
    			System.out.println("blubVirtReg");
    			VirtualRegister vr = (VirtualRegister) ((RegisterOperand) icode.getValueOperand()).getRegister();
    			HardwareRegister hr = irfuncs.get(irfuncs.size()-1).getHardReg(vr.getType());
    			CASSGN assgn = new CASSGN(new RegisterOperand(hr), new RegisterOperand(vr));
    			irfuncs.get(irfuncs.size()-1).addBefore(icode,assgn);
    			//CASSGN assgn2 = new CASSGN(((CTarget) icode).getTargetOperand(), new RegisterOperand(hr));
    			CSTORE store = new CSTORE(icode.getBaseOperand(), icode.getOffsetOperand(), new RegisterOperand(hr));
    			irfuncs.get(irfuncs.size()-1).addBefore(icode,store);
	            irfuncs.get(irfuncs.size()-1).removeIcode(icode);
    			irfuncs.get(irfuncs.size()-1).freeHardregs.add(0, hr.getName());
        	}
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
