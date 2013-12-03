package cil;

/**
 * Abstract class that represents a single CIL statement with a target operand
 */
public abstract class CTarget extends ICode {

    private Operand target;

    /**
     * Creates a new CTarget
     * 
     * @param target
     *            set the target of the target icode
     */
    public CTarget(final Operand target) {
        setTargetOperand(target);
    }

    /**
     * Returns the target
     * 
     * @return target operand or null if there is no target
     */
    public final Operand getTargetOperand() {
        return target;
    }

    /**
     * Sets the target
     * 
     * @param target
     *            set the target to this
     */
    public final void setTargetOperand(final Operand target) {
        assert (target != null) : this.hashCode();
        this.target = target;
        return;
    }
}