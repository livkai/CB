package frontend.lexer;
import frontend.parser.*;

%%

%line

WHITE_SPACE=[\n\ \t\b\012]
DIGIT=[0-9]
ALPHA=[a-zA-Z_]
ALNUM=({DIGIT}|{ALPHA})

%state COMMENT

%%
<YYINITIAL>{WHITE_SPACE}                             {/* ingore */}

<YYINITIAL>"//".*\n                                  {/* ignore */}
<YYINITIAL>^{WHITE_SPACE}*"/*"                       {yybegin(COMMENT); /* goto COMMENT state */}
<YYINITIAL>^{WHITE_SPACE}*"/*".*"*/"{WHITE_SPACE}*\n {/* ignore */}
<COMMENT>"*/"{WHITE_SPACE}*\n                        {yybegin(YYINITIAL); /* go back to initial state */}
<COMMENT>"*/"                                        {yybegin(YYINITIAL); /* go back to initial state */}
<COMMENT>\n                                          {/* ignore */}
<COMMENT>.                                           {/* ignore */}

<YYINITIAL>"!="                {return new Yytoken(Parser.TKNEQ,yytext(), (yyline+1));}
<YYINITIAL>"<="                {return new Yytoken(Parser.TKLEQ,yytext(), (yyline+1));}
<YYINITIAL>">="                {return new Yytoken(Parser.TKGEQ,yytext(), (yyline+1));}
<YYINITIAL>"&&"                {return new Yytoken(Parser.TKAND,yytext(), (yyline+1));}
<YYINITIAL>"||"                {return new Yytoken(Parser.TKOR,yytext(), (yyline+1));}
<YYINITIAL>":="                {return new Yytoken(Parser.TKASSIGN,yytext(), (yyline+1));}
<YYINITIAL>["()[],;+-*/<>{}"=] {return new Yytoken(yytext().charAt(0),yytext(), (yyline+1));}

<YYINITIAL>{DIGIT}+("."{DIGIT}*)?           {return new Yytoken(Parser.TKNUMBER,yytext(), (yyline+1)); }

<YYINITIAL>"int"		{return new Yytoken(Parser.TKINT, yytext(), (yyline+1));}
<YYINITIAL>"real"		{return new Yytoken(Parser.TKREAL, yytext(), (yyline+1));}
<YYINITIAL>"if"			{return new Yytoken(Parser.TKIF, yytext(), (yyline+1));}
<YYINITIAL>"else"		{return new Yytoken(Parser.TKELSE, yytext(), (yyline+1));}
<YYINITIAL>"while"		{return new Yytoken(Parser.TKWHILE, yytext(), (yyline+1));}
<YYINITIAL>"return"		{return new Yytoken(Parser.TKRETURN, yytext(), (yyline+1));}

<YYINITIAL>{ALPHA}{ALNUM}*	{return new Yytoken(Parser.TKIDENTIFIER, yytext(), (yyline+1));}
