package cil;

/**
 * CIL representation of a register
 */
public final class RegisterOperand extends Operand {

    private final Register register;

    /**
     * Creates a new RegisterOperand
     * 
     * @param register
     *            register that is encapsualted by this RegisterOperand
     */
    public RegisterOperand(final Register register) {
        super(register.getType());
        assert (register != null) : this.hashCode();
        this.register = register;
    }
    
    /**
     * @see cil.MutableOperand#getName()
     */
    public String getName() {
        return getRegister().getName();
    }

    /**
     * Returns the register
     * 
     * @return register
     */
    public final Register getRegister() {
        return register;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return getRegister().toString();
    }
}
