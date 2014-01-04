package cil.visitors;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cil.CLABEL;
import cil.ICode;
import cil.IRProgram;
import cil.IRFunction;

import common.Variable;

/**
 * Dumps the Common-Intermediate-Language to a file
 */
public final class DumpCILVisitor extends CILVisitorAdapter {

    private BufferedWriter bw;

    private final String filename;
 

    /**
     * Creates a new DumpCILVisitor
     * 
     * @param filename
     *            set the filename to this
     */
    public DumpCILVisitor(final String filename) {
        super("dump");
        this.filename = filename;
        try {
            bw = new BufferedWriter(new FileWriter(filename));
        }
        catch (final IOException ioe) {
            System.err.println("mcc: [error] can't open file " + filename + "["
                    + ioe.getMessage() + "]");
            errors++;
        }
    }
    
 
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public final void visit(final CLABEL icode) {
        write("#" + icode.toString());
        return;
    }

    /**
     * Visit this ICodeProgram (visitor pattern)
     * 
     * @param icodeprogram
     *            ICodeProgram to visit
     */
    public final void visit(final IRProgram icodeprogram) {
	for(Variable v : icodeprogram.globalVariables()) {
		write("#" + v.toString());
	}

        super.visit(icodeprogram);

        try {
            bw.close();
        }
        catch (final IOException ioe) {
            System.err.println("mcc: [error] can't close file " + filename
                    + "[" + ioe.getMessage() + "]");
            errors++;
        }
        return;
    }

    /**
     * Visit this ICodeFunction(visitor pattern)
     * 
     * @param icodefunc
     *            ICodeFunction to visit
     */
    public final void visit(final IRFunction icodefunc) {
        write("#" + "--- [ BEGIN ] [" + icodefunc + "] ---");
        super.visit(icodefunc);
        write("#" +  "--- [  END  ] [" + icodefunc + "] ---");
        write("#" + "");
        write("#" + "");
        return;
    }

    /**
     * What to do with an ICode
     * 
     * @param icode
     *            ICode to process
     */
    protected final void process(final ICode icode) {
        write("#" + "\t" + icode);
        return;
    }

    /**
     * Write the given string to the BufferedWriter
     * 
     * @param line
     *            line to write
     */
    private final void write(final String line) {
        try {
            bw.write(line);
            bw.newLine();
        }
        catch (final IOException ioe) {
            System.err.println("mcc: [error] can't write to file " + filename
                    + "[" + ioe.getMessage() + "]");
            errors++;
        }
        return;
    }
}
