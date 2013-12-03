package cil;

import java.math.BigDecimal;

import common.Type;

/**
 * CIL representation of a constant
 */
public final class ConstOperand extends Operand {

    private final String constant;

    /**
     * Creates a new ConstOperand
     * 
     * @param constant
     *            constant that is encapsulated by this ConstOperand
     * @param type
     *            type of this operand
     */
    public ConstOperand(final String constant, final Type type) {
        super(type);
        assert (constant != null) : this.hashCode();
        this.constant = constant;
        assert (type != null) : this.hashCode();
    }

    /**
     * Returns the constant
     * 
     * @return constant
     */
    public final String getConst() {
        return constant;
    }

    /**
     * Returns the int representation of the constant
     * 
     * @return constant as an integer
     */
    public final int toInt() {
        return new BigDecimal(constant).intValue();
    }

    /**
     * Returns the real representation of the constant
     * 
     * @return constant as a real
     */
    public final double toReal() {
        return new BigDecimal(constant).doubleValue();
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return constant;
    }
}
