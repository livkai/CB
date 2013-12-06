package cil;

import java.util.ArrayList;

import cil.visitors.CILVisitor;
import common.Type;
import common.Variable;

/**
 * Contains all ICodes of a function
 */
public final class IRFunction {
    private final String name;
    private ArrayList<Variable> params; /** The parameters this function uses */
    private ArrayList<Variable> locals; /** The local variables this function uses */
    private ArrayList<VirtualRegister> virtregs; /** The virtual registers this function uses */
    private ICodeList icodes;

    private static int nextvregid = 0;
    private static int nextlabelid = 0;

    /**
     * Creates a new  ICodeFunction
     * 
     * @param name
     *            set the name of the function
     */
    public IRFunction(final String name) {
        super();
        assert (name != null) : this.hashCode();
        this.name = name;
	params = new ArrayList<Variable>();
	locals = new ArrayList<Variable>();
	virtregs = new ArrayList<VirtualRegister>();
	icodes = new ICodeList();
    }

    /**
     * Register an CILVisitor at this ICodeFunction (visitor pattern)
     * 
     * @param visitor
     *            CILVisitor to register at this ICodeFunction
     */
    public final void accept(final CILVisitor visitor) {
        visitor.visit(this);
        return;
    }

    /**
     * Returns the name of the ICodeFunction
     * 
     * @return name
     */
    public final String getName() {
        return name;
    }

    /**
     * Create a new virtual register
     *
     * @param t
     * 	The type the register should have
     */
    public VirtualRegister getVirtReg(Type t) {
	    VirtualRegister vr = new VirtualRegister("%tmp" + nextvregid, t);
	    nextvregid++;
	    virtregs.add(vr);
	    return vr;
    }
    
    public VirtualRegister getVirtReg(int i) {
    	return virtregs.get(i);
    }

    public CLABEL getLabel() {
	return new CLABEL(nextlabelid++);
    }
	
    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return getName();
    }

    public final ICode getFirstInsn() {
	    return icodes.getHead();
    }

    public void appendInsnList(ICodeList list) {
	    icodes.concat(list);
    }

    /**
     * Add toPrepend after icode.
     */
    public void addBefore(ICode icode, ICode toPrepend) {
	    icodes.addBefore(icode, toPrepend);
    }

    /**
     * Add toAppend after icode.
     */
    public void addAfter(ICode icode, ICode toAppend) {
	    icodes.addAfter(icode, toAppend);
    }

    public void add(ICode icode) {
	    icodes.add(icode);
    }
    
    public void addParam(Variable var) {
    	params.add(var);
    }
    public void addLocals(Variable var) {
    	locals.add(var);
    }
    
    

    /**
     * Returns the name of the class
     * 
     * @return name of the class
     */
    public final String getClassName() {
        String cn = this.getClass().getName();
        return cn.substring(cn.lastIndexOf('.') + 1, cn.length());
    }
}
