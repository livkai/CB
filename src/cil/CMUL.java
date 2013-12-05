package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of a multiplication
 */
public final class CMUL extends CBinary {

    /**
     * Creates a new CMUL
     * 
     * @param left
     *            left operand of the multiplication
     * @param right
     *            right operand of the multiplication
     * @param target
     *            store the result of the multiplication to this operand
     */
    public CMUL(final Operand target, final Operand left, final Operand right) {
        super(target, left, right, "*");
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
