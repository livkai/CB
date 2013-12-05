package cil;

import java.util.ArrayList;

import java.util.List;

import common.Variable;
import cil.visitors.CILVisitor;

/**
 * Contains all global variables and functions of a program
 */
public class IRProgram {

    private final ArrayList<Variable> globals;

    private final ArrayList<IRFunction> funcs;

    /**
     * Creates a new ICodeProgram
     *  
     */
    public IRProgram() {
	globals = new ArrayList<Variable>();
	funcs = new ArrayList<IRFunction>();
    }

    /**
     * Register an CILVisitor at this ICodeProgram (visitor pattern)
     * 
     * @param visitor
     *            CILVisitor to register at this ICodeProgram
     */
    public void accept(final CILVisitor visitor) {
        visitor.visit(this);
        return;
    }

    /**
     * Add a global variable to the program
     *
     * @param var
     *            The variable to add
     */
    public void addGlobal(Variable var) {
        globals.add(var);
    }

    /**
     * Add a bunch of functions to the program.
     *
     * @param func
     *             The function to add.
     */
    public final void addFunc(final IRFunction func) {
        funcs.add(func);
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

    /**
     * Returns an iterator over the global variables in this list in proper sequence.
     * 
     * @return an iterator over the global variables in this list in proper sequence
     */
    public final List<Variable> globalVariables() {
        return java.util.Collections.unmodifiableList(globals);
    }

    /**
     * Returns an iterator over the functions in this list in proper sequence.
     * 
     * @return an iterator over the functions in this list in proper sequence
     */
    public final List<IRFunction> functions() {
        return java.util.Collections.unmodifiableList(funcs);
    }
}

