package cil.visitors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cil.*;

public class AssemblerCILVisitor extends CILVisitorAdapter{
	private FileWriter fw;
	private BufferedWriter writer;
	private File file;
	
	public AssemblerCILVisitor(String name) {
		super(name);
		fw = null;
		writer = null;
		file = null;
	/*	try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer = new BufferedWriter(fw);
		*/
	}
	

    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CADD icode) {
    	try {
			writer.append("\t add " + icode.getLeftOperand() + ", " + icode.getRightOperand() + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    	
    	try {
    		writer.append(" \t movl %edx, %ebx \n");
    		writer.append(" \t movl %eax, %edx \n");
    		writer.append(" \t sarl $31, %edx \n");
    		writer.append(" \t idivl %ebx \n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    	try {
			writer.append("\t mul " + icode.getLeftOperand() + ", " + icode.getRightOperand() + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    	try {
			writer.append("\t sub " + icode.getLeftOperand() + ", " + icode.getRightOperand() + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    	
    	
		name = name.replace(".e",".s");
		file = new File("./" + name);
		try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer = new BufferedWriter(fw);

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
    	try {
			writer.append(icodefunc.getName() + ": \n");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

