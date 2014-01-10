package cil.visitors;

import common.Variable;

import cil.ICode;
import cil.IRFunction;
import cil.VirtualRegister;

public class FPOffsetCILVisitor extends CILVisitorAdapter {

	public FPOffsetCILVisitor(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
    public void visit(final IRFunction icodefunc) {
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
    	int offset = 0;
    	for(Variable param : icodefunc.getParams()){
    		param.setOffset(offset);
    		offset +=4;
    	}
    	
    	for(Variable local : icodefunc.getLocals()){
    		local.setOffset(offset);
    		if(local.getType().isArrayType()){
    			int arraysize = 1;
    			for(int i=0; i< local.getType().getNumDimensions();i++){
    				arraysize *= local.getType().getDimSize(i);
    			}
    			offset += arraysize*4;
    		}
    		else{
    			offset +=4;
    		}
    	}
    	
    	for(VirtualRegister virtreg : icodefunc.getVirtRegs()){
    		virtreg.setOffset(offset);
    		offset +=4;
    	}
    	
    	return;
    }

}
