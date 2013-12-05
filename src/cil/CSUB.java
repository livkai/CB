package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of a subtraction
 */
public final class CSUB extends CBinary {

    /**
     * Creates a new CSUB
     * 
     * @param left
     *            left operand of this subtraction
     * @param right
     *            right operand of this subtraction
     * @param target
     *            target operand of this subtraction
     */
    public CSUB(final Operand target, final Operand left, final Operand right) {
        super(target, left, right, "-");
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
