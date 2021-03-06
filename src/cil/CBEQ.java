package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of an equal comparison
 */
public final class CBEQ extends CCondBranch {

    /**
     * Creates a new CBEQ
     * 
     * @param left
     *            left operand of the condition
     * @param right
     *            right operand of the condition
     * @param label
     *            branch to this label if the left operand is equal to the right
     *            operand
     */
    public CBEQ(final Operand left, final Operand right, final CLABEL label) {
        super(left, right, label);
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
