package cil;

/**
 * Abstract class that represents a single CIL statement with an operand and a
 * target operand
 */
public abstract class CUnary extends CTarget {

    private Operand operand;

    /**
     * Creates a new CUnary
     * 
     * @param target
     *            set the target of the unary icode opertation
     * @param operand
     *            set the operand of the unary icode opertation
     */
    public CUnary(final Operand target, final Operand operand) {
        super(target);
        setOperand(operand);
    }

    /**
     * Returns the operand
     * 
     * @return operand or null if there is no operand
     */
    public final Operand getOperand() {
        return operand;
    }

    /**
     * Sets the operand
     * 
     * @param operand
     *            set the operand to this
     */
    public final void setOperand(final Operand operand) {
        assert (operand != null) : this.hashCode();
        this.operand = operand;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public String toString() {
        return getTargetOperand() + " = " + getClassName() + " " + getOperand();
    }
}
