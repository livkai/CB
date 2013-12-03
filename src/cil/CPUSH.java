package cil;

import cil.visitors.CILVisitor;

/**
 * Class the represents a primitive datatype that will get pushed to the stack
 * as paramter for the next function that gets called
 */
public final class CPUSH extends CSingle {

    /**
     * Creates a new CPUSH
     * 
     * @param operand
     *            operand that will be pusched onto the stack
     */
    public CPUSH(final Operand operand) {
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