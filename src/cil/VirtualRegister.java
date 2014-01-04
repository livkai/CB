package cil;

import common.Type;

/**
 * Represents a virtual register
 */
public final class VirtualRegister extends Register {

	private int offset;
    /**
     * Creates a new VirtualRegister
     * 
     * @param name
     *            set the name to this
     * @param type
     *            set the type to this
     */
    public VirtualRegister(final String name, final Type type) {
        super(name, type);
        offset = -1;
    }
    
    public int getOffset(){
    	return offset;
    }
    
    public void setOffset(int off){
    	offset = off;
    }
    
}
