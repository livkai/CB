import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import cil.IRProgram;
import cil.visitors.DumpCILVisitor;

import com.martiansoftware.jsap.AbstractParameter;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.StringStringParser;

import common.InternalCompilerErrorRuntimeException;
import frontend.ast.Program;
import frontend.lexer.Lexer;
import frontend.parser.Parser;
import frontend.visitors.ASTVisitor;
import frontend.visitors.CILGeneratorASTVisitor;
import frontend.visitors.DumpASTVisitor;
import frontend.visitors.ReduceASTVisitor;
import frontend.visitors.SymbolTableASTVisitor;
import frontend.visitors.TypeCheckASTVisitor;


/**
 * Main class for the mcc compiler
 */
public final class Compiler {

	private static String commandPrefix = "";

	/**
	 * starting point of the compiler
	 * 
	 * @param args
	 *            commandline arguments to control the compiler behavior
	 */
	public final static void main(final String args[]) {
		Compiler f = new Compiler();
		f.parseCommandLine(args);
		f.process();
	}

	private static String getBackend() {
		String arch = System.getProperties().getProperty("os.arch");
		if (arch.equals("amd64")) {
			arch = "i386";
		}
		return arch;
	}

	private static boolean isAMD64Host() {
		String arch = System.getProperties().getProperty("os.arch");
		return arch.equals("amd64");
	}

	private boolean assembly = false;

	private String backend = getBackend();

	private boolean amd64Host = isAMD64Host();

	private String basename = "";

	private boolean dumpAST = false;

	private boolean dumpCIL = false;

	private String inputFile = null;

	private boolean keep = false;

	private boolean link = true;

	private String outputFile = null;

	/**
	 * Register all know commandline parameters in a ArrayList
	 * 
	 * @return ArryList with all commandline parameters registered
	 */
	private final ArrayList<AbstractParameter> getCommandLineParameters() {
		ArrayList<AbstractParameter> param = new ArrayList<AbstractParameter>();

		// commandline parameters
		FlaggedOption fo;
		UnflaggedOption ufo;
		Switch sw;

		ufo = new UnflaggedOption("inputFile");
		ufo.setStringParser(new StringStringParser());
		ufo.setRequired(true);
		ufo.setHelp("Input file to use.");
		param.add(ufo);

		fo = new FlaggedOption("outputFile");
		fo.setStringParser(new StringStringParser());
		fo.setDefault(outputFile);
		fo.setRequired(false);
		fo.setShortFlag('o');
		fo.setLongFlag(JSAP.NO_LONGFLAG);
		fo.setHelp("Write output to file <outputFile> (default = " + outputFile + ").");
		param.add(fo);

		sw = new Switch("assembly");
		sw.setShortFlag('S');
		sw.setLongFlag(JSAP.NO_LONGFLAG);
		sw.setHelp("Write assembler code to file (default = " + assembly + ").");
		param.add(sw);

		sw = new Switch("link");
		sw.setShortFlag('c');
		sw.setLongFlag(JSAP.NO_LONGFLAG);
		sw.setHelp("Don't call the linker (default = " + link + ").");
		param.add(sw);

		sw = new Switch("keep");
		sw.setShortFlag(JSAP.NO_SHORTFLAG);
		sw.setLongFlag("keep");
		sw.setHelp("Do not delete temporary files.");
		param.add(sw);

		sw = new Switch("dumpAST");
		sw.setShortFlag(JSAP.NO_SHORTFLAG);
		sw.setLongFlag("dump-ast");
		sw.setHelp("Write AST(s) to file(s) (default = " + dumpAST + ").");
		param.add(sw);

		sw = new Switch("dumpCIL");
		sw.setShortFlag(JSAP.NO_SHORTFLAG);
		sw.setLongFlag("dump-cil");
		sw.setHelp("Write CIL(s) to file(s) (default = " + dumpCIL + ").");
		param.add(sw);

		sw = new Switch("dumpALL");
		sw.setShortFlag(JSAP.NO_SHORTFLAG);
		sw.setLongFlag("dump-all");
		sw.setHelp("Activate all dumps.");
		param.add(sw);

		sw = new Switch("help");
		sw.setShortFlag('h');
		sw.setLongFlag("help");
		sw.setHelp("Print this help text.");
		param.add(sw);

		fo = new FlaggedOption("backend");
		fo.setStringParser(new StringStringParser());
		fo.setDefault(backend);
		fo.setRequired(true);
		fo.setShortFlag('m');
		fo.setLongFlag(JSAP.NO_LONGFLAG);
		fo.setHelp("Choose one of the supported backends (i386 | ppc | arm | jvm). " + "Default = " + backend);
		param.add(fo);

		return param;
	}

	// dirty hack to avoid a annoying warning by javac
	@SuppressWarnings("unchecked")
	private Iterator<Object> errorIterator(JSAPResult result) {
		return result.getErrorMessageIterator();
	}

	/**
	 * parse the command line options and set the member variable approriate
	 * 
	 * @param args
	 *            command line arguments
	 */
	public final void parseCommandLine(final String args[]) {
		JSAP clParser = new JSAP();

		// register commen frontend parameters
		Iterator<AbstractParameter> i = getCommandLineParameters().iterator();
		while (i.hasNext()) {
			try {
				clParser.registerParameter(i.next());
			} catch (final JSAPException jsape) {
				throw new InternalCompilerErrorRuntimeException("can not register parameters");
			}
		}

		// parse the commandline parameters
		JSAPResult result = clParser.parse(args);
		if (!result.success() || result.getBoolean("help")) {
			if (!result.getBoolean("help")) {
				System.err.println();
				Iterator<Object> errs = errorIterator(result);
				while (errs.hasNext()) {
					System.err.println("ERROR: " + errs.next());
				}
				System.err.println();
			}
			System.err.println("Usage: java " + this.getClass().getName());
			System.err.println();
			System.err.println(clParser.getHelp());
			System.exit(1);
		}

		// set options according to the commandline parameters
		backend = result.getString("backend");
		inputFile = result.getString("inputFile");
		outputFile = result.getString("outputFile");

		basename = inputFile.substring(0, inputFile.lastIndexOf('.')); // strip file extension
		basename = inputFile.substring(basename.lastIndexOf('/') + 1, basename.length()); // strip all directories

		assembly = !result.getBoolean("assembly");
		dumpAST = result.getBoolean("dumpAST");
		dumpCIL = result.getBoolean("dumpCIL");

		if (result.getBoolean("dumpALL")) {
			dumpAST = true;
			dumpCIL = true;
		}
		link = !result.getBoolean("link");
		keep = result.getBoolean("keep");

		if (backend.equals("ppc")) {
			commandPrefix = "powerpc-eabi-";
		} else if (backend.equals("arm")) {
			commandPrefix = "arm-";
		}

		// turn of assembler run and linking pass for JVM backend
		if (backend.equals("jvm")) {
			assembly = false;
			link = false;
		}
	}

	/**
	 * Start the lexing -> parsing -> transforming -> optimizing -> compiling -> assembling -> linking
	 * process of the given source file
	 *
	 * If the input source file is foo.e then
	 *
	 * ./bin/mcc foo.e should generate an executable binary a.out
	 * ./bin/mcc foo.e -o foo should generate an executable binary foo
	 * ./bin/mcc foo.e -S should only output the assembly code in foo.s
	 * ./bin/mcc foo.e -o bar.s should only output the assembly code in bar.s
	 * ./bin/mcc foo.e -c should build an object file foo.o, but not generate an executable
	 * ./bin/mcc foo.e -c -o bar.o should build an object file bar.o, but not generate an executable
	 */
	public final void process() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(inputFile));
		} catch (final FileNotFoundException fnfe) {
			System.err.println(inputFile + ": error: no such input-file");
			System.exit(1);
		}

		Parser parser = new Parser(new Lexer(in));
		try {
			parser.start(inputFile);
		} catch (Throwable t) {
			System.err.println(inputFile + ": error: could not parse file");
			System.exit(1);
		}
		
		IRProgram newRoot = null;
		Program root = (Program) parser.getRoot();
		if (root == null) {
			System.err.println(inputFile + ": error: could not parse file");
			System.exit(1);
		}

		ArrayList<ASTVisitor<?,?>> astvisitors = new ArrayList<ASTVisitor<?,?>>();
		ReduceASTVisitor reduceVisitor = new ReduceASTVisitor(inputFile);
		astvisitors.add(reduceVisitor);

		SymbolTableASTVisitor stVisitor = new SymbolTableASTVisitor(inputFile);
		astvisitors.add(stVisitor);
		TypeCheckASTVisitor typeVisitor = new TypeCheckASTVisitor(inputFile);
		astvisitors.add(typeVisitor);
		CILGeneratorASTVisitor<String,IRProgram> cilvisitor = new CILGeneratorASTVisitor<String,IRProgram>(inputFile);
		astvisitors.add(cilvisitor);
		/*
		 * TODO for exercise 2 and later: Add visitors to traverse the AST here.
		 */

		if (dumpAST) {
			DumpASTVisitor visitor = new DumpASTVisitor(inputFile);
			visitor.visit(root, null);
			/*
			 * TODO for exercise 1: Dump AST created by parser before the other
			 * visitors are run.
			 */
		}

		/*
		 * Call the AST visitors.
		 */
		int index = 0;
		for(ASTVisitor<?,?> av : astvisitors) {
			newRoot = (IRProgram)av.visit(root, null);
			if (av.getErrors() != 0) {
				System.exit(1); // errors occured
			}
			if (dumpAST) {
				DumpASTVisitor visitor = new DumpASTVisitor(inputFile,index);
				visitor.visit(root, null);
				/*
				 * TODO for exercise 1: Dump AST again after an
				 * AST visitor has run. Be sure to write the output to
				 * another .dot file.
				 */
			}
			index++;
		}

		if (dumpCIL) {
			DumpCILVisitor visitor = new DumpCILVisitor(inputFile + "CIL");
			visitor.visit(newRoot);
			/* TODO for execerice 4: Dump CIL code before the other CIL visitors are run */
		}

		/*
		 * Build the file paths where the outputs should go.
		 * aso: assembly file generated by the compiler
		 * obj: the object generated by the assembler from the
		 *      assembly file
		 * binary: the name of the executable binary generated by the
		 *         linker from the object file
		 */
		String aso = (!assembly && outputFile != null ? outputFile : basename + ".s");
		String obj = (!link && outputFile != null ? outputFile : basename + ".o");
		String binary = (outputFile != null ? outputFile : "a.out");

		/*
		 * TODO for exercise 6 and later: Similar to the AST visitors,
		 * add and call visitors that should traverse the intermediate
		 * code here.
		 */

		/*
		 * Generate the object file/executable now.
		 * TODO for exercise 7: Enable this code once your compiler
		 * generates assembly code.
		 */
/*
		if (assembly) {
			runAssembler(aso, obj);

			if (link) {
				runLinker(obj, binary);
			}
		}
*/
		return;
	}

	/**
	 * Run the assembler on the assembly file generated by the compiler and
	 * generate the object file. The compiler terminates on an error.
	 * If the run of the assembler was successful, the input assembly file is
	 * deleted if the --keep flag was not specified when the compiler is
	 * run.
	 */
	private void runAssembler(String input, String output) {
		String machopt = " ";
		if (amd64Host && backend.equals("i386")) {
			machopt = "--32 ";
		} else if (backend.equals("arm")) {
			machopt = "-mfpu=vfp ";
		}

		String asc = commandPrefix + "as " + machopt + "-o " + output + " " + input;
		execStuff(asc, new String[] {input, output});

		if (!keep) {
			deleteFile(input);
		}
	}

	/**
	 * Run the linker on the object file generated by the compiler
	 * and generate the executable. The compiler terminates on an error.
	 * If the run of the linker was successful, the input object file is
	 * deleted if the --keep flag was not specified when the compiler is
	 * run.
	 */
	private void runLinker(String input, String output) {
		/*
		 * Search for the runtime library that contains
		 * readInt, writeInt etc. and the startup code
		 * that calls main.
		 */
		String path = System.getProperty("MCCLIB");
		if (path == null) {
			System.err.println("ld: [error] MCCLIB is not set, cannot find runtime library");
		}
		String lib = path + "/libruntime_" + backend + ".a";
		String start = path + "/" + backend + "/startup.o";

		String binfmt = " ";
		if (amd64Host && backend.equals("i386")) {
			binfmt = "-melf_i386 ";
		}

		String ldc = commandPrefix + "ld " + binfmt + "-o " + output + " " + input + " " + lib + " " + start;
		execStuff(ldc, new String[] {output, input});
		if (!keep) {
			deleteFile(input);
		}
	}
	
	/**
	 * Deletes the file denoted by filename.
	 * 
	 * @param filename The name of the file to delete
	 */
	public void deleteFile(String filename) {
		new File(filename).delete();
	}
	
	
	/**
	 * Renames a file.
	 * 
	 * @param oldfilename The old name of the file
	 * @param newfilename The new name of the file
	 */
	public void renameFile(String oldfilename, String newfilename) {
		new File(oldfilename).renameTo(new File(newfilename));
	}
	
	
	/**
	 * Executes cmd. If Execution finishes with an error status != 0 or if any other exception occurs,
	 * deletes all files mentioned in filesToCleanOnFailure.
	 * 
	 * @param cmd The Command to execute
	 * @param filesToCleanOnFailure The files to delete if execution of cmd fails.  
	 */
	public void execStuff(String cmd, String[] filesToCleanOnFailure) {
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			proc.getInputStream().close();
			proc.getErrorStream().close();
			proc.waitFor();
			if (proc.exitValue() != 0) {
				System.err.println("DEBUG: exitValue(): " + proc.exitValue());
				throw new Exception();
			}
		} catch (Exception e) {
			System.err.println("execStuff: [error] (" + cmd + ") " + e.getMessage());
			if (!keep) {
				for(String s : filesToCleanOnFailure)
					deleteFile(s); // clean up the mess
			}
			System.exit(1);
		}
	}

}
