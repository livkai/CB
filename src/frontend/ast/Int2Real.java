package frontend.ast;

import frontend.visitors.ASTVisitor;

public class Int2Real extends Expr {
	
	private double doubleValue;
	private Expr intExpr;
	
	
	public Int2Real(String file, int line, Expr value) {
		super(file, line);
		intExpr = value;
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
		return "INT -> REAL";
	}
	
	public Expr getExpr(){
		return intExpr;
	}
}
