package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of a label
 */
public final class CLABEL extends ICode {

    private final int labelNumber;

    /**
     * Creates a new
     * 
     * @param labelNumber
     *            unique number of this label
     */
    public CLABEL(final int labelNumber) {
        assert (labelNumber >= 0) : this.hashCode();
        this.labelNumber = labelNumber;
    }

    /**
     * Returns the number of this label
     * 
     * @return number of this label
     */
    public final int getLabelNumber() {
        return labelNumber;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return "L" + getLabelNumber();
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
}
