package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of an assignment
 */
public final class CASSGN extends CUnary {

    /**
     * Creates a new CASSGN
     * 
     * @param operand
     *            Operand that will be assigned to the target operand
     * @param target
     *            Operand to which the operand will be assigned
     */
    public CASSGN(final Operand target, final Operand operand) {
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

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return getTargetOperand() + " = " + getOperand();
    }
}
