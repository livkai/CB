package frontend.ast;

import frontend.visitors.ASTVisitor;

public class Real2Int extends Expr {
	
	private int intValue;
	private Expr doubleExpr;
	
	
	public Real2Int(String file, int line, Expr value) {
		super(file, line);
		doubleExpr= value;
		// TODO Auto-generated constructor stub
	}

	@Override
	public <P, R> R accept(ASTVisitor<P, R> visitor, P param) {
		// TODO Auto-generated method stub
		return visitor.visit(this, param);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "REAL -> INT";
	}
	
	public Expr getExpr(){
		return doubleExpr;
	}
	
	public int getIntValue(){
		return intValue;
	}

}
