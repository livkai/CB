package cil;

/**
 * Abstract class that represents a single CIL statement with a left operand, a
 * right operand and a label
 */
public abstract class CCondBranch extends CBranch {

    private Operand left;

    private Operand right;

    /**
     * Creates a new CCondBranche
     * 
     * @param left
     *           left operand of the condition
     * @param right
     *           right operand of the condition
     * @param label
     *            jump to this label if the condition is met
     */
    public CCondBranch(final Operand left, final Operand right,
            final CLABEL label) {
        super(label);
        setLeftOperand(left);
        setRightOperand(right);
    }

    /**
     * Return the left operand
     * 
     * @return left operand or null if there is no left operand
     */
    public final Operand getLeftOperand() {
        return left;
    }

    /**
     * Returns the right operand
     * 
     * @return right operand or null if there is no right operand
     */
    public final Operand getRightOperand() {
        return right;
    }

    /**
     * Sets the left operand
     * 
     * @param left
     *            set the left operand to this
     */
    public final void setLeftOperand(final Operand left) {
        assert (left != null) : this.hashCode();
        this.left = left;
        return;
    }

    /**
     * Sets the right operand
     * 
     * @param right
     *            set the right operand to this
     */
    public final void setRightOperand(final Operand right) {
        assert (right != null) : this.hashCode();
        this.right = right;
        return;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return getClassName() + " " + getLeftOperand() + ", "
                + getRightOperand() + ", " + getLabel();
    }
}