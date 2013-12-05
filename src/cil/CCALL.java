package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of a function call
 */
public class CCALL extends CTarget {

    private final String name;

    /**
     * Creates a new CCALL
     * 
     * @param name
     *            name of the function that will be called
     * @param target
     *            store the return value of that function to this operand
     */
    public CCALL(final String name, final Operand target) {
        super(target);
        assert (name != null) : this.hashCode();
        this.name = name;
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
     * Returns the name of the function to call
     * 
     * @return function name
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return getTargetOperand() + " = CALL [" + name + "]";
    }
}
