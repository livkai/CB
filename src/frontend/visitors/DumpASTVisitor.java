package frontend.visitors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import frontend.ast.*;

/**
 * class that visits all ASTNodes and writes to a .dot-file in order to dump the AST
 * <b>Note:</b> The visit methods always return <code>null</code>.
 *
 * @param <P>
 *          The type of the parameter used by each visit method
 * @param <R>
 *          The type of the value returned by each visit method
 */
public class DumpASTVisitor<P, R> extends ASTVisitorAdapter<P, R> implements ASTVisitor<P, R> {

	/** variable hold number of errors that occured during the Visitor run */
	protected int errors;
	/** name of the visitor */
	protected  String name;
	protected ArrayList<String> list;
	protected boolean end = false;
	protected int i= 0;
	protected int index = -1;


	/**
	 * prolog. Writes the visited nodes into the .dot-file
	 * @see frontend.visitors.ASTVisitor#prolog ASTVisitor.prolog
	 */
	public void prolog(ASTNode n) {
		if(n instanceof VarDeclList || n instanceof StmtList){
			return;
		}
		//create a unique id for each node
		String unique = "\"" + n.toString() +i+"\"";
		try {
			File file = null;
			name = name.replace(".e","");
			if(index != -1) {
				file = new File("./" + name + index + ".dot");
			}else {
				file = new File("./" + name + ".dot");
			}
			FileWriter fw = null; 
			BufferedWriter bw = null;
			boolean exists = false;
			
			//file already exists and we are at the beginning -> overwrites the existing file
			if(file.exists() && n.getClassName().compareTo("Program")==0) {
				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				String begin = "digraph ast { \n";
				bw.append(begin);
				bw.flush();
			}
			// if file doesn't exist, then create it
			if (!(file.exists())) {
				exists = true;
				file.createNewFile();
				fw = new FileWriter(file.getAbsoluteFile(),true);
				bw = new BufferedWriter(fw);
				String begin = "digraph ast { \n";
				bw.append(begin);
				bw.flush();
			}
			if(!exists){
				fw = new FileWriter(file.getAbsoluteFile(),true);
				bw = new BufferedWriter(fw);
			}
			bw.append(unique + "[label=\""+n.toString()+"\"]; \n");
			if(n.getClassName().compareTo("Program")!=0){
			int size = list.size();
			bw.append(list.get(size-1)+"->"+unique+"; \n");
			}
			bw.flush();
			list.add(unique);
			i++;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * epilog. 
	 * @see frontend.visitors.ASTVisitor#epilog ASTVisitor.epilog
	 */
	public void epilog(ASTNode n) {
		File file = null;
		if(n instanceof VarDeclList || n instanceof StmtList){
			return;
		}
		if(n.getClassName().compareTo("Program")==0) {
			if(index != -1) {
				file = new File("./" + name + index + ".dot");
			}else {
				file = new File("./" + name + ".dot");
			}
			FileWriter fw;
			//completes the .dot-file with "}"
			try {
				fw = new FileWriter(file.getAbsoluteFile(),true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.append("}");
				bw.flush();
				list.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		//removes the last inserted element
		else{
			list.remove(list.size()-1);
		}
	}

	/**
	 * Creates a new DumpASTVisitor
	 * 
	 * @param name
	 *            set the name to this
	 */
	public DumpASTVisitor(final String name) {
		super(name);
		list = new ArrayList<String>();
		errors = 0;
		this.name = name;
	}
	
	public DumpASTVisitor(final String name,int ind){
		super(name);
		list = new ArrayList<String>();
		errors = 0;
		this.name = name;
		index = ind;
		
	}
	
}