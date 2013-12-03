package cil;

/**
 * Abstract class that represents a single CIL statement with a single operand
 */
public abstract class CSingle extends ICode {

    private Operand operand;

    /**
     * Creates a new CSingle
     * 
     * @param operand
     *            operand of this single icode
     */
    protected CSingle(final Operand operand) {
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
     *            set operand to this
     */
    public final void setOperand(final Operand operand) {
        assert (operand != null) : this.hashCode();
        this.operand = operand;
        return;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public String toString() {
        return getClassName() + " " + getOperand();
    }
}