package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of a branch
 */
public final class CBRA extends CBranch {

    /**
     * Creates a new CBRA
     * 
     * @param label
     *            label to branch to
     */
    public CBRA(final CLABEL label) {
        super(label);
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
    public final String toString() {
        return getClassName() + " " + getLabel();
    }
}
