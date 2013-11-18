//### This file created by BYACC 1.8(/Java extension  1.1)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 19 "Parser.y"
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
//#line 28 "Parser.java"




/**
 * Encapsulates yacc() parser functionality in a Java
 *        class for quick code development
 */
public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[],stateptr;           //state stack
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
void state_push(int state)
{
  if (stateptr>=YYSTACKSIZE)         //overflowed?
    return;
  statestk[++stateptr]=state;
  if (stateptr>statemax)
    {
    statemax=state;
    stateptrmax=stateptr;
    }
}
int state_pop()
{
  if (stateptr<0)                    //underflowed?
    return -1;
  return statestk[stateptr--];
}
void state_drop(int cnt)
{
int ptr;
  ptr=stateptr-cnt;
  if (ptr<0)
    return;
  stateptr = ptr;
}
int state_peek(int relative)
{
int ptr;
  ptr=stateptr-relative;
  if (ptr<0)
    return -1;
  return statestk[ptr];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
boolean init_stacks()
{
  statestk = new int[YYSTACKSIZE];
  stateptr = -1;
  statemax = -1;
  stateptrmax = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:Object
String   yytext;//user variable to return contextual strings
Object yyval; //used to return semantic vals from action routines
Object yylval;//the 'lval' (result) I got from yylex()
Object valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new Object[YYSTACKSIZE];
  yyval=new Object();
  yylval=new Object();
  valptr=-1;
}
void val_push(Object val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
Object val_pop()
{
  if (valptr<0)
    return null;
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
Object val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return null;
  return valstk[ptr];
}
//#### end semantic value section ####
public final static short TKNUMBER=257;
public final static short TKIDENTIFIER=258;
public final static short TKREAL=259;
public final static short TKINT=260;
public final static short TKLEQ=261;
public final static short TKGEQ=262;
public final static short TKNEQ=263;
public final static short TKIF=264;
public final static short TKELSE=265;
public final static short TKWHILE=266;
public final static short TKRETURN=267;
public final static short TKASSIGN=268;
public final static short TKAND=269;
public final static short TKOR=270;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    0,    3,    3,    1,    2,    2,    4,
    4,    4,    6,    6,    7,    7,    7,    7,    8,    8,
    8,   11,   11,   11,   12,   12,   12,   12,   12,   12,
   12,   10,   10,   10,   13,   13,   13,   13,   13,   16,
   16,   15,   15,    5,   14,   14,    9,    9,   17,   17,
   17,   17,   17,   18,   18,   19,   20,   21,
};
final static short yylen[] = {                            2,
    3,    2,    1,    2,    3,    2,    2,    6,    5,    1,
    1,    4,    3,    1,    4,    3,    3,    2,    3,    3,
    1,    3,    3,    1,    3,    3,    3,    3,    3,    3,
    3,    3,    3,    1,    3,    1,    4,    3,    1,    4,
    4,    1,    1,    1,    3,    1,    2,    1,    1,    1,
    1,    1,    1,    7,    5,    5,    4,    3,
};
final static short yydefred[] = {                         0,
   10,   11,    0,    0,    3,    0,    0,    2,    4,   44,
    0,    0,    1,   36,    0,    0,    0,    0,   34,   39,
    0,    0,    0,    0,    0,   12,    0,    0,    0,    0,
    0,    0,   14,    0,    0,   35,   38,    0,    0,    0,
    0,    0,   32,   33,    0,    0,    9,    7,    0,    0,
   37,    0,   41,   40,    0,    0,    0,   18,    0,    0,
    0,   53,    0,    0,   48,   49,   50,   51,   52,    8,
   13,    0,    0,    0,    0,    6,   17,    0,    0,   16,
   47,    0,    0,    0,    0,   24,    0,   58,    5,   15,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   57,   31,    0,    0,    0,    0,    0,
    0,   22,   23,    0,   56,    0,   54,
};
final static short yydgoto[] = {                          3,
    4,    5,   60,   34,   16,   35,   62,   84,   63,   18,
   85,   86,   19,   39,   20,   21,   65,   66,   67,   68,
   69,
};
final static short yysindex[] = {                      -116,
    0,    0, -116,  -26,    0,  -76,   13,    0,    0,    0,
  -17,   54,    0,    0,  -17,   40,   34,   42,    0,    0,
   43,   -7,   96,  -32,  -17,    0,  -17,  -17,  -17,  -17,
  -17,  -14,    0,  -76,   89,    0,    0,   76,  104,   62,
   42,   42,    0,    0,   65,  -79,    0,    0,  -14, -116,
    0,  -17,    0,    0,   64,   85,  -17,    0,   91,  -65,
   86,    0,  -55, -111,    0,    0,    0,    0,    0,    0,
    0,   76,   11,   11,   70,    0,    0,  102,   17,    0,
    0,  -17,   11,   38,  -36,    0,  -34,    0,    0,    0,
   79,   30,  -24,  -17,  -17,  -17,  -17,  -17,  -17,   11,
   11,  -14,  -14,    0,    0,   76,   76,   76,   76,   76,
   76,    0,    0, -102,    0,  -14,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  119,    0,    0,    0,  -31,    0,  -19,    0,    0,
  -41,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  108,    0,    0,
   -6,    4,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  -85,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  115,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   -9,    9,   16,   18,   20,
   26,    0,    0,   39,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   66,  181,    0,  111,   72,    0,   44,   71,  126,  144,
  -47,   73,  146,    0,   57,    0,   22,    0,    0,    0,
    0,
};
final static int YYTABLESIZE=306;
final static short yytable[] = {                         42,
   42,   42,   42,   42,  102,   42,  103,   15,   37,   43,
   43,   43,   43,   43,   11,   43,  105,   42,   42,   42,
   42,   21,   15,   21,   21,   21,   87,   43,   43,   43,
   43,   29,    9,   32,   19,   93,   19,   19,   19,   21,
   21,   21,   21,   46,   20,   58,   20,   20,   20,   30,
   83,   42,   19,   19,   19,   19,   26,   46,   25,   77,
   27,   43,   20,   20,   20,   20,   28,   46,    7,   80,
   36,   13,   27,   21,   28,   47,   27,   12,   28,   24,
   27,   17,   28,   29,   81,   23,   19,   33,   30,   98,
   97,   99,   70,   22,   38,   40,   20,   98,   97,   99,
   81,   45,   64,   73,   27,   48,   28,   27,   46,   28,
    6,   59,   27,    6,   28,   71,   64,   61,   27,   64,
   28,   27,   72,   28,   74,   78,   26,   75,   88,   49,
   25,   61,   50,   31,   61,   64,   36,  104,   27,   46,
   28,   90,    1,    2,   51,  114,  115,   52,   46,   76,
   61,   46,   91,   92,   53,   45,   82,   54,   45,  117,
   89,   55,  116,   55,  106,  107,  108,  109,  110,  111,
   41,   42,  112,  113,   43,   44,   25,    7,   10,    1,
    2,   10,   43,    8,   55,   79,   56,   57,    0,    0,
    0,    0,   10,    1,    2,    0,    0,    0,   55,    0,
   56,   57,   10,    0,    0,    0,    0,    0,   55,    0,
   56,   57,    0,    0,    0,    0,    0,    0,    0,   42,
   42,   42,    0,    0,   14,   10,   42,   42,   42,   43,
   43,   43,  100,  101,  100,  101,    0,   43,   43,   14,
   10,   21,   21,   21,  100,  101,    0,    0,    0,   21,
   21,    1,    2,    0,   19,   19,   19,    0,    0,   29,
   29,    0,   19,   19,   20,   20,   20,   14,   10,    0,
    0,    0,   20,   20,   10,    0,    0,   30,   30,    0,
   55,    0,   56,   57,   26,   26,   25,   25,   27,   27,
   94,   95,   96,    0,   28,   28,   55,    0,   94,   95,
   96,    0,   55,    0,   55,   55,
};
final static short yycheck[] = {                         41,
   42,   43,   44,   45,   41,   47,   41,   40,   41,   41,
   42,   43,   44,   45,   91,   47,   41,   59,   60,   61,
   62,   41,   40,   43,   44,   45,   74,   59,   60,   61,
   62,   41,   59,   41,   41,   83,   43,   44,   45,   59,
   60,   61,   62,  123,   41,  125,   43,   44,   45,   41,
   40,   93,   59,   60,   61,   62,   41,  123,   41,  125,
   41,   93,   59,   60,   61,   62,   41,  123,    3,  125,
   41,   59,   43,   93,   45,   32,   43,    6,   45,   40,
   43,   11,   45,   42,   63,   15,   93,   22,   47,   60,
   61,   62,   49,   40,   24,   25,   93,   60,   61,   62,
   79,   31,   46,   40,   43,   34,   45,   43,  123,   45,
    0,   46,   43,    3,   45,   50,   60,   46,   43,   63,
   45,   43,   52,   45,   40,   60,   93,   57,   59,   41,
   91,   60,   44,   91,   63,   79,   41,   59,   43,  123,
   45,  125,  259,  260,   41,  102,  103,   44,   41,   59,
   79,   44,   82,   83,   93,   41,  268,   93,   44,  116,
   59,  123,  265,  125,   94,   95,   96,   97,   98,   99,
   27,   28,  100,  101,   29,   30,   91,   59,  258,  259,
  260,  258,  268,    3,  264,   60,  266,  267,   -1,   -1,
   -1,   -1,  258,  259,  260,   -1,   -1,   -1,  264,   -1,
  266,  267,  258,   -1,   -1,   -1,   -1,   -1,  264,   -1,
  266,  267,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  261,
  262,  263,   -1,   -1,  257,  258,  268,  269,  270,  261,
  262,  263,  269,  270,  269,  270,   -1,  269,  270,  257,
  258,  261,  262,  263,  269,  270,   -1,   -1,   -1,  269,
  270,  259,  260,   -1,  261,  262,  263,   -1,   -1,  269,
  270,   -1,  269,  270,  261,  262,  263,  257,  258,   -1,
   -1,   -1,  269,  270,  258,   -1,   -1,  269,  270,   -1,
  264,   -1,  266,  267,  269,  270,  269,  270,  269,  270,
  261,  262,  263,   -1,  269,  270,  258,   -1,  261,  262,
  263,   -1,  264,   -1,  266,  267,
};
final static short YYFINAL=3;
final static short YYMAXTOKEN=270;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"TKNUMBER","TKIDENTIFIER","TKREAL",
"TKINT","TKLEQ","TKGEQ","TKNEQ","TKIF","TKELSE","TKWHILE","TKRETURN","TKASSIGN",
"TKAND","TKOR",
};
final static String yyrule[] = {
"$accept : program",
"program : program var_decl ';'",
"program : program func_decl",
"program : func_decl",
"program : var_decl ';'",
"var_decl_list : var_decl_list var_decl ';'",
"var_decl_list : var_decl ';'",
"var_decl : type identifier",
"func_decl : type identifier '(' par_list ')' block",
"func_decl : type identifier '(' ')' block",
"type : TKREAL",
"type : TKINT",
"type : type '[' arith_expr ']'",
"par_list : par_list ',' var_decl",
"par_list : var_decl",
"block : '{' var_decl_list stmt_list '}'",
"block : '{' stmt_list '}'",
"block : '{' var_decl_list '}'",
"block : '{' '}'",
"arith_expr : arith_expr '+' term",
"arith_expr : arith_expr '-' term",
"arith_expr : term",
"cond_expr : cond_expr TKAND ao_expr",
"cond_expr : cond_expr TKOR ao_expr",
"cond_expr : ao_expr",
"ao_expr : arith_expr '=' arith_expr",
"ao_expr : arith_expr TKNEQ arith_expr",
"ao_expr : arith_expr '<' arith_expr",
"ao_expr : arith_expr '>' arith_expr",
"ao_expr : arith_expr TKLEQ arith_expr",
"ao_expr : arith_expr TKGEQ arith_expr",
"ao_expr : '(' cond_expr ')'",
"term : term '*' factor",
"term : term '/' factor",
"term : factor",
"factor : '(' arith_expr ')'",
"factor : TKNUMBER",
"factor : identifier '(' arg_list ')'",
"factor : identifier '(' ')'",
"factor : lvalue",
"array_access : array_access '[' arith_expr ']'",
"array_access : identifier '[' arith_expr ']'",
"lvalue : array_access",
"lvalue : identifier",
"identifier : TKIDENTIFIER",
"arg_list : arg_list ',' arith_expr",
"arg_list : arith_expr",
"stmt_list : stmt_list stmt",
"stmt_list : stmt",
"stmt : if_stmt",
"stmt : while_stmt",
"stmt : assgn_stmt",
"stmt : return_stmt",
"stmt : block",
"if_stmt : TKIF '(' cond_expr ')' block TKELSE block",
"if_stmt : TKIF '(' cond_expr ')' block",
"while_stmt : TKWHILE '(' cond_expr ')' block",
"assgn_stmt : lvalue TKASSIGN arith_expr ';'",
"return_stmt : TKRETURN arith_expr ';'",
};

//#line 532 "Parser.y"
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
//#line 390 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 37 "Parser.y"
{
			Program	p = (Program) val_peek(2);
			VarDecl d = (VarDecl) val_peek(1);
			p.addDecl(d);
			yyval = p;
		}
break;
case 2:
//#line 44 "Parser.y"
{
			Program p  = (Program) val_peek(1);
			FuncDecl f = (FuncDecl) val_peek(0);
			p.addDecl(f);
			yyval = p;
		}
break;
case 3:
//#line 51 "Parser.y"
{
			FuncDecl f = (FuncDecl) val_peek(0);
			Program x = new Program(f, file, line);
			root = x;
			yyval = x;
		}
break;
case 4:
//#line 58 "Parser.y"
{
			VarDecl d = (VarDecl) val_peek(1);
			Program x = new Program(d, file, line);
			root = x;
			yyval = x;
		}
break;
case 5:
//#line 68 "Parser.y"
{
			VarDeclList	v = (VarDeclList) val_peek(2);
			VarDecl		d = (VarDecl) val_peek(1);
			v.addVarDecl(d);
			yyval = v;
		}
break;
case 6:
//#line 75 "Parser.y"
{ 
			VarDecl		v = (VarDecl) val_peek(1);
			VarDeclList x = new VarDeclList(v, file, line);
			yyval = x;
		}
break;
case 7:
//#line 84 "Parser.y"
{ 	
			Type t = (Type) val_peek(1);
			Identifier	i = (Identifier) val_peek(0);
			VarDecl		x = new VarDecl(t, i, file, line);
			yyval = x;
		}
break;
case 8:
//#line 94 "Parser.y"
{
			Type t = (Type) val_peek(5);
			Identifier	i = (Identifier) val_peek(4);
			ParList		p = (ParList) val_peek(2);
			Block		b = (Block) val_peek(0);
			FuncDecl	x = new FuncDecl(t, i, p, b, file, line);
			yyval = x;
		}
break;
case 9:
//#line 103 "Parser.y"
{ 	
			Type t = (Type) val_peek(4);
			Identifier	i = (Identifier) val_peek(3);
			Block		b = (Block) val_peek(0);
			FuncDecl	x = new FuncDecl(t, i, b, file, line);
			yyval = x;
		}
break;
case 10:
//#line 114 "Parser.y"
{ 
			Type x = Type.getRealType();
			yyval = x;
		}
break;
case 11:
//#line 119 "Parser.y"
{ 
			Type x = Type.getIntType();
			yyval = x;
		}
break;
case 12:
//#line 124 "Parser.y"
{ 
			Type t = (Type) val_peek(3);
			Expr a = (Expr) val_peek(1);
			Type x = Type.getArrayType(t,a);
			yyval = x;		
			/*$$ = null;	/* TODO for exercise 1 * /*/

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if (yyval == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
break;
case 13:
//#line 147 "Parser.y"
{ 
			ParList	p = (ParList) val_peek(2);
			VarDecl	v = (VarDecl) val_peek(0);
			p.addParam(v);

			yyval = p;
		}
break;
case 14:
//#line 155 "Parser.y"
{ 
			VarDecl	v = (VarDecl) val_peek(0);
			ParList x = new ParList(v, file, line);
			yyval = x;
		}
break;
case 15:
//#line 164 "Parser.y"
{
			VarDeclList	v = (VarDeclList) val_peek(2);
			StmtList	s = (StmtList) val_peek(1);
			Block		x = new Block(v, s, file, line);
			yyval = x;
		}
break;
case 16:
//#line 171 "Parser.y"
{ 	
			StmtList	s = (StmtList) val_peek(1);
			Block		x = new Block(s, file, line);
			yyval = x;
		}
break;
case 17:
//#line 177 "Parser.y"
{
			VarDeclList v = (VarDeclList) val_peek(1);
			Block		x = new Block(v, file, line);
			yyval = x;
		}
break;
case 18:
//#line 183 "Parser.y"
{	
			yyval = new Block(file, line);

		}
break;
case 19:
//#line 191 "Parser.y"
{ 
			Expr	a = (Expr) val_peek(2);
			Expr		t = (Expr) val_peek(0);
			ADDExpr	x = new ADDExpr(a, t, file, line);
			yyval = x;
		}
break;
case 20:
//#line 198 "Parser.y"
{ 
			Expr	a = (Expr) val_peek(2);
			Expr		t = (Expr) val_peek(0);
			SUBExpr	x = new SUBExpr(a, t, file, line);
			yyval = x;
		}
break;
case 21:
//#line 205 "Parser.y"
{ 
			yyval = val_peek(0);
		}
break;
case 22:
//#line 212 "Parser.y"
{ 
			Expr	c = (Expr) val_peek(2);
			Expr		a = (Expr) val_peek(0);
			ANDExpr		x = new ANDExpr(c, a, file, line);
			yyval = x;
		}
break;
case 23:
//#line 219 "Parser.y"
{
			Expr	c = (Expr) val_peek(2);
			Expr		a = (Expr) val_peek(0);
			ORExpr		x = new ORExpr(c, a, file, line);
			yyval = x;
		}
break;
case 24:
//#line 226 "Parser.y"
{ 
			yyval = val_peek(0);
		}
break;
case 25:
//#line 233 "Parser.y"
{ 
			Expr	a1 = (Expr) val_peek(2);
			Expr	a2 = (Expr) val_peek(0);
			EQExpr		x  = new EQExpr(a1, a2, file, line);
			yyval = x;
		}
break;
case 26:
//#line 240 "Parser.y"
{ 
			Expr	a1 = (Expr) val_peek(2);
			Expr	a2 = (Expr) val_peek(0);
			NEQExpr		x  = new NEQExpr(a1, a2, file, line);
			yyval = x;
		}
break;
case 27:
//#line 247 "Parser.y"
{ 
			Expr	a1 = (Expr) val_peek(2);
			Expr	a2 = (Expr) val_peek(0);
			LTExpr		x  = new LTExpr(a1, a2, file, line);
			yyval = x;
		}
break;
case 28:
//#line 254 "Parser.y"
{ 	
			Expr	a1 = (Expr) val_peek(2);
			Expr	a2 = (Expr) val_peek(0);
			GTExpr		x  = new GTExpr(a1, a2, file, line);
			yyval = x;
		}
break;
case 29:
//#line 261 "Parser.y"
{ 
			Expr	a1 = (Expr) val_peek(2);
			Expr	a2 = (Expr) val_peek(0);
			LEQExpr		x  = new LEQExpr(a1, a2, file, line);
			yyval = x;
		}
break;
case 30:
//#line 268 "Parser.y"
{ 
			Expr	a1 = (Expr) val_peek(2);
			Expr	a2 = (Expr) val_peek(0);
			GEQExpr		x  = new GEQExpr(a1, a2, file, line);
			yyval = x;
		}
break;
case 31:
//#line 275 "Parser.y"
{
			yyval = val_peek(1);
		}
break;
case 32:
//#line 282 "Parser.y"
{ 
			Expr		t = (Expr) val_peek(2);
			Expr		f = (Expr) val_peek(0);
			MULTerm	x = new MULTerm(t, f, file, line);
			yyval = x;
		}
break;
case 33:
//#line 289 "Parser.y"
{ 
			Expr		t = (Expr) val_peek(2);
			Expr		f = (Expr) val_peek(0);
			DIVTerm	x = new DIVTerm(t, f, file, line);
			yyval = x;
		}
break;
case 34:
//#line 296 "Parser.y"
{
			yyval = val_peek(0);
		}
break;
case 35:
//#line 303 "Parser.y"
{
			yyval = val_peek(1);
		}
break;
case 36:
//#line 307 "Parser.y"
{ 
			Const	x = new Const(lxtext, file, line);
			yyval = x;
		}
break;
case 37:
//#line 312 "Parser.y"
{ 
			Identifier i = (Identifier) val_peek(3);
			ArgList l = (ArgList) val_peek(1);
			FuncCall f = new FuncCall(i,l,file,line);	
			yyval = f;	/* TODO for exercise 1 */

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if (yyval == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
break;
case 38:
//#line 331 "Parser.y"
{ 
			Identifier i = (Identifier) val_peek(2);
			FuncCall f = new FuncCall(i,file,line);	
			yyval = f;	
			/* TODO for exercise 1 */

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if (yyval == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
break;
case 39:
//#line 350 "Parser.y"
{
			yyval = val_peek(0);
		}
break;
case 40:
//#line 357 "Parser.y"
{
			ArrayAccess base = (ArrayAccess) val_peek(3);
			Expr a = (Expr) val_peek(1);
			base.addIndex(a);
			yyval = base;
		}
break;
case 41:
//#line 364 "Parser.y"
{
			Identifier i = (Identifier) val_peek(3);
			Expr a = (Expr) val_peek(1);
			ArrayAccess aa = new ArrayAccess(i, a, file, line);
			yyval = aa;
		}
break;
case 42:
//#line 374 "Parser.y"
{
			yyval = val_peek(0);
		}
break;
case 43:
//#line 378 "Parser.y"
{
			yyval = val_peek(0);
		}
break;
case 44:
//#line 385 "Parser.y"
{ 
			Identifier	x = new Identifier(lxtext, file, line);
			yyval = x;
		}
break;
case 45:
//#line 393 "Parser.y"
{ 
			ArgList	l = (ArgList) val_peek(2);
			Expr a = (Expr) val_peek(0);
			l.addParam(a);
			yyval = l;
			/*$$ = null;	/* TODO for exercise 1 * /*/

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if (yyval == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
break;
case 46:
//#line 413 "Parser.y"
{
			Expr e = (Expr) val_peek(0);
			ArgList l = new ArgList(e,file,line);
			yyval = l;

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if (yyval == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
break;
case 47:
//#line 434 "Parser.y"
{ 
			StmtList	l = (StmtList) val_peek(1);
			Stmt		s = (Stmt) val_peek(0);
			l.addStatement(s);
			yyval = l;
		}
break;
case 48:
//#line 441 "Parser.y"
{
			Stmt		s = (Stmt) val_peek(0);
			StmtList	x = new StmtList(s, file, line);
			yyval = x;
		}
break;
case 49:
//#line 450 "Parser.y"
{ 
			yyval = val_peek(0);
		}
break;
case 50:
//#line 454 "Parser.y"
{
			yyval = val_peek(0);
		}
break;
case 51:
//#line 458 "Parser.y"
{
			yyval = val_peek(0);
		}
break;
case 52:
//#line 462 "Parser.y"
{
			yyval = val_peek(0);
		}
break;
case 53:
//#line 466 "Parser.y"
{
			yyval = val_peek(0);
		}
break;
case 54:
//#line 473 "Parser.y"
{ 	
			Expr	c  = (Expr) val_peek(4);
			Block		b1 = (Block) val_peek(2);
			Block		b2 = (Block) val_peek(0);
			IfStmt		x  = new IfStmt(c, b1, b2, file, line);
			yyval = x;	
		}
break;
case 55:
//#line 481 "Parser.y"
{ 
			Expr	c = (Expr) val_peek(2);
			Block		b = (Block) val_peek(0);
			IfStmt		x = new IfStmt(c, b, file, line);
			yyval = x;
		}
break;
case 56:
//#line 491 "Parser.y"
{ 
			Expr c = (Expr) val_peek(2);
			Block b = (Block) val_peek(0);
			WhileStmt x = new WhileStmt(c, b, file, line);
			yyval = x;
			/*$$ = null;	/* TODO for exercise 1 * /*/

			/*
                         * You can remove this comment and the following three
			 * lines as soon as you build an
			 * AST for this production.
			 * We need the ($$ == null) check because otherwise
			 * javac complains about unreachable code which is
			 * added by the parser generator after our code.
			 */
			if (yyval == null) {
				throw new InternalCompilerErrorRuntimeException("unimplemented");
			}
		}
break;
case 57:
//#line 514 "Parser.y"
{ 
			Expr	l = (Expr) val_peek(3);
			Expr	a = (Expr) val_peek(1);
			AssgnStmt	x = new AssgnStmt(l, a, file, line);
			yyval = x;
		}
break;
case 58:
//#line 524 "Parser.y"
{ 
			Expr	a = (Expr) val_peek(1);
			ReturnStmt 	x = new ReturnStmt(a, file, line);
			yyval = x;
		}
break;
//#line 1083 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
