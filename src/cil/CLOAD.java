package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of an array load
 */
public final class CLOAD extends CTarget {
    private Operand base;
    private Operand offset;

    /**
     * Creates a new CLOAD
     * 
     * @param target
     *            target operand to store the value of the array to
     * @param base
     *            base address of the array
     * @param off
     *            offset in the array
     */
    public CLOAD(final Operand target, final Operand base, final Operand off) {
        super(target);
	this.base = base;
	offset = off;
    }

    /**
     * Register an CILVisitor at this ICode (visitor pattern)
     * 
     * @param visitor
     *            CILVisitor to register at this ICode
     */
    public final void accept(final CILVisitor visitor) {
        visitor.visit(this);
        return;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public String toString() {
        return getTargetOperand() + " = " + base + "[" + offset + "]";
    }

    /**
     * Returns the base address.
     */
    public Operand getBaseOperand() {
        return base;
    }

    /**
     * Set the base address.
     */
    public void setBaseOperand(Operand b) {
        base = b;
    }

    /**
     * Returns the offset.
     */
    public Operand getOffsetOperand() {
        return offset;
    }

    /**
     * Set the offset.
     */
    public void setOffsetOperand(Operand o) {
        offset = o;
    }
}
