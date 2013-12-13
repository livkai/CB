package cil;

import common.Type;

/**
 * Superclass that represents either virtual or a hardware register
 */
public abstract class Register {

    private final String name;

    private final Type type;

    /**
     * Creates a new Register
     * 
     * @param name
     *            set the name to this
     * @param type
     *            set the type to this
     */
    public Register(final String name, final Type type) {
        assert (type != null) : this.hashCode();
        this.type = type;

        assert (name != null) : this.hashCode();
        this.name = name;
    }

    /**
     * Returns the name of the register
     * 
     * @return name of the register
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the type of the register
     * 
     * @return type of the register
     */
    public final Type getType() {
        return type;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return getName();
    }
    
  
}
