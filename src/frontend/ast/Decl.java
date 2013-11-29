package frontend.ast;

import common.Type;

/**
 * AST representation off a declaration
 */
public abstract class Decl extends ASTNode {

    private final Type type;
    private final Identifier identifier;

    /**
     * Creates a new Decl
     * 
     * @param type
     *            set the type to this
     * @param identifier
     *            set the identifier to this
     * @param file
     *            set the file number
     * @param line
     *            set the line number to this
     */
    public Decl(final Type type, final Identifier identifier, final String file, final int line) {
        super(file, line);
        assert (type != null) : this.hashCode();
        assert (identifier != null) : this.hashCode();
        this.type = type;
        this.identifier = identifier;
    }

    /**
     * Returns the name of identifier
     * 
     * @return name of the identifier
     */
    public final String getName() {
        return identifier.getName();
    }

    /**
     * Returns the identifier that is connected with this declaration
     * 
     * @return identifier of the declaration
     */
    public final Identifier getIdentifier() {
        return identifier;
    }

    public final Type getType() {
	    return type;
    }
}
