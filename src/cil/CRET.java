package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of a return-statement
 */
public final class CRET extends CSingle {

    /**
     * Creates a new CRET
     * 
     * @param operand
     *            operand that will be returned by this CRET
     */
    public CRET(final Operand operand) {
        super(operand);
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
