package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of a real to int cast
 */
public final class CR2I extends CUnary {

    /**
     * Creates a new CR2I
     * 
     * @param target
     *            target operand of the REAL to INT cast
     * @param operand
     *            operand that shall be casted from REAL to INT
     */
    public CR2I(final Operand target, final Operand operand) {
        super(target, operand);
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
