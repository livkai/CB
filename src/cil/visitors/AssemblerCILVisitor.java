package cil.visitors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import common.Variable;

import cil.*;

public class AssemblerCILVisitor extends CILVisitorAdapter{
	private FileWriter fw;
	private BufferedWriter writer;
	private File file;
	private int paramCount = 0;
	
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
    		writer.append("\t addl " + icode.getRightOperand() + ", " + icode.getLeftOperand() + "\n");
    		writer.flush();
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
    	try {
    		writer.append("\t movl " + getOpCode(icode.getOperand()) + " , " + getOpCode(icode.getTargetOperand())+"\n");
    		writer.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        process(icode);
        return;
    }

   
    
    private String getOpCode(Operand operand){
		if(operand instanceof RegisterOperand && ((RegisterOperand)operand).getRegister() instanceof HardwareRegister) {
			return operand.toString();
		}else if(operand instanceof ConstOperand) {
			return "$" +  operand;
		}else if(operand instanceof RegisterOperand && ((RegisterOperand)operand).getRegister() instanceof VirtualRegister) {
			return ((VirtualRegister)((RegisterOperand) operand).getRegister()).getOffset() + "(%ebp)";
		}else{
			if(((VariableOperand) operand).getVariable().getOffset() == -1){
				return ((VariableOperand) operand).getVariable().getName();
			}
			return ((VariableOperand) operand).getVariable().getOffset() + "(%ebp)";
		}
    }
    
    /**
     * Visit this ICode (visitor pattern)
     * 
     * @param icode
     *            ICode to visit
     */
    public void visit(final CBEQ icode) {
    	try {
    		writer.append("\t cmpl " + getOpCode(icode.getRightOperand()) + " , " + getOpCode(icode.getLeftOperand())+"\n");
    		writer.append("\t je " + icode.getLabel()  + "\n");
    		writer.flush();

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
    public void visit(final CBGE icode) {
    	try {
    		writer.append("\t cmpl " + getOpCode(icode.getRightOperand()) + " , " + getOpCode(icode.getLeftOperand())+"\n");
    		writer.append("\t jge " + icode.getLabel()  + "\n");
    		writer.flush();

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
    public void visit(final CBGT icode) {
    	try {
    		writer.append("\t cmpl " + getOpCode(icode.getRightOperand()) + " , " + getOpCode(icode.getLeftOperand())+"\n");
    		writer.append("\t jg " + icode.getLabel()  + "\n");
    		writer.flush();

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
    public void visit(final CBLE icode) {
    	try {
    		writer.append("\t cmpl " + getOpCode(icode.getRightOperand()) + " , " + getOpCode(icode.getLeftOperand())+"\n");
    		writer.append("\t jle " + icode.getLabel()  + "\n");
    		writer.flush();

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
    public void visit(final CBLT icode) {
    	try {
    		writer.append("\t cmpl " + getOpCode(icode.getRightOperand()) + " , " + getOpCode(icode.getLeftOperand())+"\n");
    		writer.append("\t jl " + icode.getLabel()  + "\n");
    		writer.flush();

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
    public void visit(final CBNE icode) {
    	try {
    		writer.append("\t cmpl " + getOpCode(icode.getRightOperand()) + " , " + getOpCode(icode.getLeftOperand())+"\n");
    		writer.append("\t jne " + icode.getLabel()  + "\n");
    		writer.flush();

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
    public void visit(final CBRA icode) {
    	try {
    		writer.append("\t jmp " + icode.getLabel()  + "\n");
    		writer.flush();

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
    public void visit(final CCALL icode) {
    	
    	try {
			writer.append("\t call " + icode.getName() + "\n");
			writer.append("\t addl $"+paramCount*4 + ",%esp \n");
			writer.append("\t movl %eax,"+getOpCode(icode.getTargetOperand())+"\n");
			writer.flush();
	    	paramCount = 0;
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
    public void visit(final CDIV icode) {
    	
    	try {
    		writer.append(" \t movl %edx, %ebx \n");
    		writer.append(" \t movl %eax, %edx \n");
    		writer.append(" \t sarl $31, %edx \n");
    		writer.append(" \t idivl %ebx \n");
    		writer.flush();
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
    	try {
    		writer.append(icode.toString()+ ": \n");
    		writer.flush();

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
    //kein plan, soll erstmal so laufen..^^
    public void visit(final CLOAD icode) {
    	if(((VariableOperand)icode.getBaseOperand()).getVariable().getOffset() == -1){
    		try {
				writer.append("\t mul $4,"+ getOpCode(icode.getOffsetOperand()));
				writer.append("\t movl " + ((VariableOperand)icode.getBaseOperand()).getVariable().getName() + "+"+getOpCode(icode.getOffsetOperand()) + ","+icode.getTargetOperand());
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}else{
    		try {
				writer.append("\t mul $-4,"+ getOpCode(icode.getOffsetOperand()));
				writer.append("\t addl $"+((VariableOperand)icode.getBaseOperand()).getVariable().getOffset()+","+getOpCode(icode.getOffsetOperand()));
			//	writer.append("\t movl " + getOpCode(icode.getOffsetOperand())"+"+getOpCode(icode.getOffsetOperand()) + ","+icode.getTargetOperand());
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
    public void visit(final CMUL icode) {
    	try {
    		writer.append("\t mul " + icode.getRightOperand() + ", " + icode.getLeftOperand() + "\n");
    		writer.flush();
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
    	try {
    		writer.append("\t pushl " + getOpCode(icode.getOperand()) + "\n");
   		 	writer.flush();
			paramCount++;
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
    	try {
    		writer.append("\t movl %ebp, %esp \n");
    		writer.append("\t popl %ebp \n");
    		writer.append("\t movl " + getOpCode(icode.getOperand()) + ", %eax \n");
    		writer.append("\t ret \n");
    		writer.flush();

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
    		 writer.append("\t subl " + icode.getRightOperand() + ", " + icode.getLeftOperand() + "\n");
    		 writer.flush();
			
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
		try {
			writer.append("\t .global _start\n");
	    	for(IRFunction ff : icodeprogram.functions()) {
	    		writer.append("\t .global " +ff.getName() +"\n");
	    	}
	    	writer.append("\n");
			writer.append("_start:\n");
			writer.append("\t call main\n");
			writer.append("\t movl	%eax, %ebx \n");
			writer.append("\t movl	$1, %eax	# Syscall-Code f. exit \n");
			writer.append("\t int	$0x80 \n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	for(IRFunction ff : icodeprogram.functions()) {
    		currentFunction = ff;
    		currentFunction.accept(this);
    	}
		for(Variable global : icodeprogram.globalVariables()){
			if(global.getType().isRealType()){
				System.err.println("Real type is not supported!");
				System.exit(1);
			}else if(global.getType().isArrayType()){
    			if(global.getType().getArrayElementType().isRealType()){
	    			System.err.println("Real type is not supported!");
	    			System.exit(1);
    			}
    			try {
    				int size=4;
    				for(int i=0;i<global.getType().getNumDimensions();i++){
    					size *= global.getType().getDimSize(i);
    				}
					writer.append("\t .lcomm "+global.getName()+","+size+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				
			}
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
			writer.append("\t pushl %ebp \n");
			writer.append("\t movl %esp, %ebp \n");
	    	for(Variable local : icodefunc.getLocals()){
	    		if(local.getType().isArrayType()){
	    			if(local.getType().getArrayElementType().isRealType()){
		    			System.err.println("Real type is not supported!");
		    			System.exit(1);
	    			}
	    			int arraysize = 1;
	    			for(int i=0; i< local.getType().getNumDimensions();i++){
	    				arraysize *= local.getType().getDimSize(i);
	    			}
	    			writer.append("\t subl $" + arraysize + ", %esp \n");
	    		}
	    		else if(local.getType().isRealType()){
	    			System.err.println("Real type is not supported!");
	    			System.exit(1);
	    		}
	    		else{
	    			writer.append("\t subl $4, %esp \n");
	    		}
	    	}
	    	
	    	for(VirtualRegister virtreg : icodefunc.getVirtRegs()){
	    		if(virtreg.getType().isRealType()){
	    			System.err.println("Real type is not supported!");
	    			System.exit(1);
	    		}
	    		writer.append("\t subl $4, %esp \n");
	    	}
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

