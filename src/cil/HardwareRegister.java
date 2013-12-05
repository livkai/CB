package cil;

import common.Type;

/**
 * Represents a real register such as %eax on x86
 */
public final class HardwareRegister extends Register {

    /**
     * Creates a new HardwareRegister
     * 
     * @param name
     *            set the name to this
     * @param type
     *            set the type to this
     */
    public HardwareRegister(final String name, final Type type) {
        super(name, type);
    }
}
