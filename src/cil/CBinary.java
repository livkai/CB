package cil;

/**
 * Abstract class that represents a single CIL statement with a left and a right
 * operand
 */
public abstract class CBinary extends CTarget {

    private Operand left;

    private Operand right;

    private final String op;

    /**
     * Creates a new CBinary
     * 
     * @param target
     *            store the result to this operand
     * @param left
     *            left operand
     * @param right
     *            right operand
     * @param op
     * 		  the operator (+, -, etc.)
     */
    public CBinary(final Operand target, final Operand left, final Operand right, final String op) {
        super(target);
	this.op = op;
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
     * @return right operand or null if there is no left operand
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
    public String toString() {
        return getTargetOperand() + " = " + left + " " + op + " " + right;
    }
}
