package cil;

import common.Variable;

/**
 * CIL representation of a variable
 */
public final class VariableOperand extends Operand {

    private final Variable variable;

    /**
     * Creates a new VariableOperand
     * 
     * @param variable
     *            variable that is encapsulated by this VariableOperand
     */
    public VariableOperand(final Variable variable) {
        super(variable.getType());
        assert (variable != null) : this.hashCode();
        this.variable = variable;
    }

    /**
     * @see cil.MutableOperand#getName()
     */
    public String getName() {
        return variable.getName();
    }

    /**
     * Returns the variable
     * 
     * @return variable
     */
    public final Variable getVariable() {
        return variable;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return variable.getName();
    }
}
