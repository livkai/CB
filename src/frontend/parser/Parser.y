%token TKNUMBER
%token TKIDENTIFIER
%token TKREAL
%token TKINT
%token TKLEQ
%token TKGEQ
%token TKNEQ
%token TKIF
%token TKELSE
%token TKWHILE
%token TKRETURN
%token TKASSIGN
%token TKAND
%token TKOR

%start program
	
%{
package frontend.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ArrayList;

import frontend.lexer.*;
import frontend.ast.*;
import common.InternalCompilerErrorRuntimeException;
import common.Type;
%}

%%

program: 
	program var_decl ';'
		{
			Program	p = (Program) $1;
			VarDecl d = (VarDecl) $2;
			p.addDecl(d);
			$$ = p;
		}
	| program func_decl
		{
			Program p  = (Program) $1;
			FuncDecl f = (FuncDecl) $2;
			p.addDecl(f);
			$$ = p;
		}
	| func_decl
		{
			FuncDecl f = (FuncDecl) $1;
			Program x = new Program(f, file, line);
			root = x;
			$$ = x;
		}
	| var_decl ';'
		{
			VarDecl d = (VarDecl) $1;
			Program x = new Program(d, file, line);
			root = x;
			$$ = x;
		}
	;

var_decl_list:
	var_decl_list var_decl ';'
		{
			VarDeclList	v = (VarDeclList) $1;
			VarDecl		d = (VarDecl) $2;
			v.addVarDecl(d);
			$$ = v;
		}
	| var_decl ';'
		{ 
			VarDecl		v = (VarDecl) $1;
			VarDeclList x = new VarDeclList(v, file, line);
			$$ = x;
		}
	;

var_decl:
	type identifier
		{ 	
			Type t = (Type) $1;
			Identifier	i = (Identifier) $2;
			VarDecl		x = new VarDecl(t, i, file, line);
			$$ = x;
		}
	;

func_decl:
	type identifier '(' par_list ')' block
		{
			Type t = (Type) $1;
			Identifier	i = (Identifier) $2;
			ParList		p = (ParList) $4;
			Block		b = (Block) $6;
			FuncDecl	x = new FuncDecl(t, i, p, b, file, line);
			$$ = x;
		}
	| type identifier '(' ')' block
		{ 	
			Type t = (Type) $1;
			Identifier	i = (Identifier) $2;
			Block		b = (Block) $5;
			FuncDecl	x = new FuncDecl(t, i, b, file, line);
			$$ = x;
		}
	;

type:
	TKREAL
		{ 
			Type x = Type.getRealType();
			$$ = x;
		}
	| TKINT
		{ 
			Type x = Type.getIntType();
			$$ = x;
		}
	| type '[' arith_expr ']'
		{ 
			Type t = (Type) $1;
			Expr a = (Expr) $3;
			Type x = Type.getArrayType(t,a);
			$$ = x;		
			//$$ = null;	/* TODO for exercise 1 */

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if ($$ == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
	;

par_list:
	par_list ',' var_decl
		{ 
			ParList	p = (ParList) $1;
			VarDecl	v = (VarDecl) $3;
			p.addParam(v);

			$$ = p;
		}
	| var_decl
		{ 
			VarDecl	v = (VarDecl) $1;
			ParList x = new ParList(v, file, line);
			$$ = x;
		}
	;

block:
	'{' var_decl_list stmt_list '}'
		{
			VarDeclList	v = (VarDeclList) $2;
			StmtList	s = (StmtList) $3;
			Block		x = new Block(v, s, file, line);
			$$ = x;
		}
	| '{' stmt_list '}'
		{ 	
			StmtList	s = (StmtList) $2;
			Block		x = new Block(s, file, line);
			$$ = x;
		}
	| '{' var_decl_list '}'
		{
			VarDeclList v = (VarDeclList) $2;
			Block		x = new Block(v, file, line);
			$$ = x;
		}
	| '{' '}'
		{	
			$$ = new Block(file, line);

		}
	;

arith_expr:
	arith_expr '+' term
		{ 
			Expr	a = (Expr) $1;
			Expr		t = (Expr) $3;
			ADDExpr	x = new ADDExpr(a, t, file, line);
			$$ = x;
		}
	| arith_expr '-' term
		{ 
			Expr	a = (Expr) $1;
			Expr		t = (Expr) $3;
			SUBExpr	x = new SUBExpr(a, t, file, line);
			$$ = x;
		}
	| term
		{ 
			$$ = $1;
		}
	;

cond_expr:
	cond_expr TKAND ao_expr
		{ 
			Expr	c = (Expr) $1;
			Expr		a = (Expr) $3;
			ANDExpr		x = new ANDExpr(c, a, file, line);
			$$ = x;
		}
	| cond_expr TKOR  ao_expr
		{
			Expr	c = (Expr) $1;
			Expr		a = (Expr) $3;
			ORExpr		x = new ORExpr(c, a, file, line);
			$$ = x;
		}
	| ao_expr
		{ 
			$$ = $1;
		}
	;

ao_expr:
	arith_expr '='   arith_expr
		{ 
			Expr	a1 = (Expr) $1;
			Expr	a2 = (Expr) $3;
			EQExpr		x  = new EQExpr(a1, a2, file, line);
			$$ = x;
		}
	| arith_expr TKNEQ arith_expr
		{ 
			Expr	a1 = (Expr) $1;
			Expr	a2 = (Expr) $3;
			NEQExpr		x  = new NEQExpr(a1, a2, file, line);
			$$ = x;
		}
	| arith_expr '<'   arith_expr
		{ 
			Expr	a1 = (Expr) $1;
			Expr	a2 = (Expr) $3;
			LTExpr		x  = new LTExpr(a1, a2, file, line);
			$$ = x;
		}
	| arith_expr '>'   arith_expr
		{ 	
			Expr	a1 = (Expr) $1;
			Expr	a2 = (Expr) $3;
			GTExpr		x  = new GTExpr(a1, a2, file, line);
			$$ = x;
		}
	| arith_expr TKLEQ arith_expr
		{ 
			Expr	a1 = (Expr) $1;
			Expr	a2 = (Expr) $3;
			LEQExpr		x  = new LEQExpr(a1, a2, file, line);
			$$ = x;
		}
	| arith_expr TKGEQ arith_expr
		{ 
			Expr	a1 = (Expr) $1;
			Expr	a2 = (Expr) $3;
			GEQExpr		x  = new GEQExpr(a1, a2, file, line);
			$$ = x;
		}
	| '(' cond_expr ')'
		{
			$$ = $2;
		}
	;

term:
	term '*' factor
		{ 
			Expr		t = (Expr) $1;
			Expr		f = (Expr) $3;
			MULTerm	x = new MULTerm(t, f, file, line);
			$$ = x;
		}		 
	| term '/' factor
		{ 
			Expr		t = (Expr) $1;
			Expr		f = (Expr) $3;
			DIVTerm	x = new DIVTerm(t, f, file, line);
			$$ = x;
		}
	| factor
		{
			$$ = $1;
		}
	;

factor:
	'(' arith_expr ')'
		{
			$$ = $2;
		}
	| TKNUMBER
		{ 
			Const	x = new Const(lxtext, file, line);
			$$ = x;
		}
	| identifier '(' arg_list ')'
		{ 
			Identifier i = (Identifier) $1;
			ArgList l = (ArgList) $3;
			FuncCall f = new FuncCall(i,l,file,line);	
			$$ = f;	/* TODO for exercise 1 */

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if ($$ == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
	| identifier '(' ')'
		{ 
			Identifier i = (Identifier) $1;
			FuncCall f = new FuncCall(i,file,line);	
			$$ = f;	
			/* TODO for exercise 1 */

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if ($$ == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
	| lvalue
		{
			$$ = $1;
		}
	;

array_access:
	array_access '[' arith_expr ']'
		{
			ArrayAccess base = (ArrayAccess) $1;
			Expr a = (Expr) $3;
			base.addIndex(a);
			$$ = base;
		}
	| identifier '[' arith_expr ']'
		{
			Identifier i = (Identifier) $1;
			Expr a = (Expr) $3;
			ArrayAccess aa = new ArrayAccess(i, a, file, line);
			$$ = aa;
		}
	;

lvalue:
	array_access
		{
			$$ = $1;
		}
	| identifier
		{
			$$ = $1;
		}
	;

identifier:
	TKIDENTIFIER
		{ 
			Identifier	x = new Identifier(lxtext, file, line);
			$$ = x;
		}
	;

arg_list:
	arg_list ',' arith_expr
		{ 
			ArgList	l = (ArgList) $1;
			Expr a = (Expr) $3;
			l.addParam(a);
			$$ = l;
			//$$ = null;	/* TODO for exercise 1 */

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if ($$ == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
	| arith_expr
		{
			Expr e = (Expr) $1;
			ArgList l = new ArgList(e,file,line);
			$$ = l;

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if ($$ == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
	;

stmt_list:
	stmt_list stmt
		{ 
			StmtList	l = (StmtList) $1;
			Stmt		s = (Stmt) $2;
			l.addStatement(s);
			$$ = l;
		}
	| stmt
		{
			Stmt		s = (Stmt) $1;
			StmtList	x = new StmtList(s, file, line);
			$$ = x;
		}
	;

stmt:
	if_stmt
		{ 
			$$ = $1;
		}
	| while_stmt
		{
			$$ = $1;
		}
	| assgn_stmt
		{
			$$ = $1;
		}
	| return_stmt
		{
			$$ = $1;
		}
	| block
		{
			$$ = $1;
		}
	;

if_stmt:
	TKIF '(' cond_expr ')' block TKELSE block
		{ 	
			Expr	c  = (Expr) $3;
			Block		b1 = (Block) $5;
			Block		b2 = (Block) $7;
			IfStmt		x  = new IfStmt(c, b1, b2, file, line);
			$$ = x;	
		}
	| TKIF '(' cond_expr ')' block
		{ 
			Expr	c = (Expr) $3;
			Block		b = (Block) $5;
			IfStmt		x = new IfStmt(c, b, file, line);
			$$ = x;
		}
	;

while_stmt:
	TKWHILE '(' cond_expr ')' block
		{ 
			Expr c = (Expr) $3;
			Block b = (Block) $5;
			WhileStmt x = new WhileStmt(c, b, file, line);
			$$ = x;
			//$$ = null;	/* TODO for exercise 1 */

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if ($$ == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
	;

assgn_stmt:
	lvalue TKASSIGN arith_expr ';'
		{ 
			Expr	l = (Expr) $1;
			Expr	a = (Expr) $3;
			AssgnStmt	x = new AssgnStmt(l, a, file, line);
			$$ = x;
		}
	;

return_stmt:
	TKRETURN arith_expr ';'
		{ 
			Expr	a = (Expr) $2;
			ReturnStmt 	x = new ReturnStmt(a, file, line);
			$$ = x;
		}
	;

%%
private String lxtext	= "";
private Lexer lx		= null;
private ASTNode root	= null;
private int line 		= 1;
private String file;

public Parser(final Lexer lexer) {
	lx = lexer;
}

public final ASTNode getRoot() {
	return root;
}

public final void start(String filename) {
	file = filename;
	yyparse();
	return;
}

private final void yyerror(final String s) {
	System.err.println(file + ":" + line + " :error: parsing error caused by token " + lxtext);
}

public final int yylex() {
	try{
		yylval=lx.yylex();
		lxtext=((Yytoken) yylval).text();
		line = ((Yytoken) yylval).line();
		return ((Yytoken) yylval).type();
	} catch (Error e) {
		System.err.println(file + ":" + line + " :error: parsing error occured *AFTER* token " + lxtext);
		throw new RuntimeException();
	} catch(Exception e) {
		return -1;
	}
}
