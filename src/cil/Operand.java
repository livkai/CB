package cil;

import common.Type;

/**
 * Abstract superclass for all operands
 */
public abstract class Operand {
    /** The type of the operand */
    private final Type type;

    /**
     * Creates a new Operand
     *  
     */
    public Operand(final Type ty) {
	    type = ty;
    }

    /**
     * Returns the name of the class
     * 
     * @return name of the class
     */
    public final String getClassName() {
        String cn = this.getClass().getName();
        return cn.substring(cn.lastIndexOf('.') + 1, cn.length());
    }

    /**
     * Returns the type of this Operand
     * 
     * @return type
     */
    public final Type getType() {
        return type;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public abstract String toString();
}
