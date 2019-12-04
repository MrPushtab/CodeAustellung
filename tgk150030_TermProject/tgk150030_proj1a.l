%{
#include <stdio.h>
#include "y.tab.h"
#define NEEDSTEXT 1
#define NEEDSNUM 2


int linenum;
%}


COMM		%.*

WRITEINT	"writeInt"
READINT		"readInt"


IF 			"if"
THEN		"then"
ELSE 		"else"
BEGIN 		"begin"
END 		"end"
WHILE 		"while"
DO 			"do"
PROGRAM 	"program"
VAR 		"var"
AS 			"as"
INT 		"int"
BOOL		"bool"

BOOLLIT		"true"|"false"			
IDENT		[A-Z][A-Z0-9]*
NUM			[1-9][0-9]*|0
OP2			[*]|"div"|"mod"
OP3			"+"|"-"
OP4			"<="|">="|"="|"!="|"<"|">"

LP 			"("
RP 			")"
ASGN 		":="
SC 			";"

%%
{COMM}		{}

{WRITEINT}	return(WRITEINT);
{READINT}	return(READINT);

{IF} 		return(IF);
{THEN}		return(THEN);
{ELSE}		return(ELSE);
{BEGIN}		return(BEGIN);
{END}		return(END);
{WHILE}		return(WHILE);
{DO}		return(DO);
{PROGRAM}	return(PROGRAM);
{VAR}		return(VAR);
{AS}		return(AS);
{INT}		return(INT);
{BOOL}		return(BOOL);

{LP}		return(LP);
{RP}		return(RP);
{ASGN}		return(ASGN);
{SC}		return(SC);

{NUM}		{
				//printf("NUM = ");
				storeNUMVal();
				return(NUM);
			}
			
{OP2}		{
				//printf("OP2 = ");
				yylval.sval =  malloc(sizeof(yytext));
				strncpy(yylval.sval,yytext,yyleng);
				return(OP2);
			}
			
{OP3}		{
				//printf("OP3 = ");
				yylval.sval =  malloc(sizeof(yytext));
				strncpy(yylval.sval,yytext,yyleng);
				return(OP3);
			}

{OP4}		{
				//printf("OP4 = ");
				yylval.sval =  malloc(sizeof(yytext));
				strncpy(yylval.sval,yytext,yyleng);
				return(OP4);
			}
			
{IDENT}		{
				//printf("IDENT = ");
				yylval.sval =  malloc(sizeof(yytext));
				strncpy(yylval.sval,yytext,yyleng);
				return(IDENT);
			}

				
[ \t]+ 		/*ignore blanks*/
[\n]		{linenum++;}
%%

void printTxt()
{
	printf(yytext);
}
void printVal()
{
	printf("%i",yylval);
}

void storeNUMVal()
{
	yylval.ival = (int)atol(yytext);
}

/*
int main()
{
	int ret = 0;
	while(1==1)
	{
			ret = yylex();
			if(ret == NEEDSTEXT)
			{
				printTxt();
				printf("\n");
			}
			if(ret == NEEDSNUM)
			{
				printVal();
			}
	}
}

{COMM}		{}

{WRITEINT}	//printf("WRITEINT\n");
{READINT}	//printf("READINT\n");

{IF} 		//printf("IF\n");
{THEN}		//printf("THEN\n");
{ELSE}		//printf("ELSE\n");
{BEGIN}		//printf("BEGIN\n");
{END}		//printf("END\n");
{WHILE}		//printf("WHILE\n");
{DO}		//printf("DO\n");
{PROGRAM}	//printf("PROGRAM\n");
{VAR}		//printf("VAR\n");
{AS}		//printf("AS\n");
{INT}		//printf("INT\n");
{BOOL}		//printf("BOOL\n");

{LP}		//printf("LP\n");
{RP}		//printf("RP\n");
{ASGN}		//printf("ASGN\n");
{SC}		//printf("SC\n");

{NUM}		{
				//printf("NUM = ");
				storeNUMVal();
				printf("yyval.ival");
				return(NUM);
			}
			
{OP2}		{
				//printf("OP2 = ");
				//return(NEEDSTEXT);
			}
			
{OP3}		{
				//printf("OP3 = ");
				//return(NEEDSTEXT);
			}

{OP4}		{
				//printf("OP4 = ");
				//return(NEEDSTEXT);
			}
			
{IDENT}		{
				//printf("IDENT = ");
				//return(NEEDSTEXT);
			}
				


*/
			