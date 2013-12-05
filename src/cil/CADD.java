package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of an addition
 */
public final class CADD extends CBinary {

    /**
     * Creates a new CADD
     * 
     * @param target
     *            store the result of the addition to this operand
     * @param left
     *            left operand of the additon
     * @param right
     *            right operand of the additon
     */
    public CADD(final Operand target, final Operand left, final Operand right) {
        super(target, left, right, "+");
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
