package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of a division
 */
public final class CDIV extends CBinary {

    /**
     * Creates a new CDIV
     * 
     * @param left
     *            divident of the divison
     * @param right
     *            divisor of the divison
     * @param target
     *            store the result of the division to this operand
     */
    public CDIV(final Operand target, final Operand left, final Operand right) {
        super(target, left, right, "/");
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
