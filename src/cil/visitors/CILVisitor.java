package cil.visitors;

import cil.CADD;
import cil.CASSGN;
import cil.CBEQ;
import cil.CBGE;
import cil.CBGT;
import cil.CBLE;
import cil.CBLT;
import cil.CBNE;
import cil.CBRA;
import cil.CCALL;
import cil.CDIV;
import cil.CI2R;
import cil.CLABEL;
import cil.CLOAD;
import cil.CMUL;
import cil.CPUSH;
import cil.CR2I;
import cil.CRET;
import cil.CSTORE;
import cil.CSUB;
import cil.IRProgram;
import cil.IRFunction;

/**
 * Interface that provides methods to visit all ICodes
 */
public interface CILVisitor {

    /**
     * Returns the number of errors that occurred during processing
     * 
     * @return number of errors
     */
    public abstract int getErrors();

    /**
     * Returns the name of the CILVisitor
     * 
     * @return name
     */
    public abstract String getName();

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CASSGN icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CBEQ icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CBGE icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CBGT icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CBLE icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CBLT icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CBNE icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CBRA icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CCALL icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CDIV icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CI2R icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CLABEL icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CLOAD icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CSUB icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CADD icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CPUSH icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CR2I icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CRET icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CSTORE icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icode
     *            ICode to visit
     */
    public abstract void visit(final CMUL icode);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icodeprogram
     *            ICodeProgram to visit
     */
    public abstract void visit(final IRProgram icodeprogram);

    /**
     * Abstract method for the visitor pattern
     * 
     * @param icodefunc
     *            ICodeFunction to visit
     */
    public abstract void visit(final IRFunction icodefunc);
}
