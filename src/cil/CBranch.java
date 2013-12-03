package cil;

/**
 * Abstract class that represents a single CIL statement with a label
 */
public abstract class CBranch extends ICode {

    private CLABEL label;

    /**
     * Creates a new CBranch
     * 
     * @param label
     *            branch to this label
     */
    public CBranch(final CLABEL label) {
        setLabel(label);
    }

    /**
     * Returns the label
     * 
     * @return label null if there is no label
     */
    public final CLABEL getLabel() {
        return label;
    }

    /**
     * Sets the label
     * 
     * @param label
     *            set the label to this
     */
    public final void setLabel(final CLABEL label) {
        assert (label != null) : this.hashCode();
        this.label = label;
        return;
    }
}