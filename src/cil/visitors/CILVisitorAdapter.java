package cil.visitors;

import cil.*;

/**
 * Adapter class that visits all ICodes and ICodeFunctions and performs NO
 * action on them.
 */
public class CILVisitorAdapter implements CILVisitor {

    /** variable hold number of errors that occured during the Visitor run */
    protected int errors;
    /** name of the visitor */
    protected String name;
    /** reference to the whole program */ 
    protected IRProgram root;
    /** reference to the current function */
    protected IRFunction currentFunction;

    /** reference to the current icode being visited */
    protected ICode currentICode;

    /** The next instruction to visit. */
    protected ICode nextICode;
    
    /**
     * Creates a new CILVisitorAdapter
     * 
     * @param name
     *            set the name to this
     */
    public CILVisitorAdapter(final String name) {
        errors = 0;
        this.name = name;
    }

    /**
     * Returns the number of errors that occurred during processing
     * 
     * @return number of errors
     */
    public final int getErrors() {
        return errors;
    }

    /**
     * Returns the name of the CILVisitor
     * 
     * @return name
     */
    public final String getName() {
        return name;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CADD icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CASSGN icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBEQ icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBGE icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBGT icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBLE icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBLT icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBNE icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBRA icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CCALL icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CDIV icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CI2R icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CLABEL icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CLOAD icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CMUL icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CPUSH icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CR2I icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CRET icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CSTORE icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CSUB icode) {
        process(icode);
        return;
    }

    /**
     * Visit this ICodeProgram (visitor pattern)
     * 
     * @param icodeprogram
     *            ICodeProgram to visit
     */
    public void visit(final IRProgram icodeprogram) {
    	root = icodeprogram;

    	for(IRFunction ff : icodeprogram.functions()) {
    		currentFunction = ff;
    		currentFunction.accept(this);
    	}
    	return;
    }

    /**
     * Visit this ICodeFunction (visitor pattern)
     * 
     * @param icodefunc
     *            ICodeFunction to visit
     */
    public void visit(final IRFunction icodefunc) {
    	// Beware, if the accept/visit(ICode) method below adds a new
    	// instruction directly after icode,
    	// the icode = next construct causes the new insn to not
    	// be visited.
    	// If your accept/visit method deletes the insn directly following icode,
    	// you need to set nextICode properly in the accept/visit method to
    	// make sure we don't visit a deleted instruction.
    	nextICode = null;
    	for (ICode icode = icodefunc.getFirstInsn(); icode != null; icode = nextICode) {
    		nextICode = icode.next;
    		currentICode = icode;
    		icode.accept(this);
    	}

    	return;
    }

    /**
     * What to do with an ICode
     * 
     * @param icode
     *            ICode to process
     */
    protected void process(final ICode icode) {
        // default: do nothing
        return;
    }
}
