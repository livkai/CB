package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of an integer to real cast
 */
public final class CI2R extends CUnary {

    /**
     * Creates a new CI2R
     * 
     * @param target
     *            target operand of this INT to REAL cast
     * @param operand
     *            operand that shall be casted from INT to REAL
     */
    public CI2R(final Operand target, final Operand operand) {
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
