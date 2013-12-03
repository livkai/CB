package cil;

import common.Type;

/**
 * Represtents a virtual register
 */
public final class VirtualRegister extends Register {

    /**
     * Creates a new VirtualRegister
     * 
     * @param name
     *            set the name to this
     * @param type
     *            set the type to this
     */
    public VirtualRegister(final String name, final Type type) {
        super(name, type);
    }
}
