%{
#include <stdio.h>
int linenum = 1;
int indentNum = 0;
int symNum;
enum booltype{integer,boolean};

struct symbolEntry
{
	char *name;
	int type;
};
struct symbolEntry symbols[20];

union factorunion
{
	char	*ident;
	int bool;
	int num;
	struct expression *expr;
};

struct factor
{
	int type;
	union factorunion factor;
};

struct term
{
	int isUnary;
	struct factor *factor1;
	char	*operation;
	struct factor *factor2;
};
	
struct simpleExpression
{
	int isUnary;
	struct term *term1;
	char	*operation;
	struct term *term2;
};

struct expression
{
	int isUnary;
	struct simpleExpression *sExpr1;
	char	*operation;
	struct simpleExpression *sExpr2;
};

struct whileStatement
{
	struct expression *whileCondition;
	struct statements *doStmnts;
};
struct ifStatement
{
	struct expression *ifExpr;
	struct statements *doStmnts;
	struct statements *elseStmnts;
};
struct assignment
{
	char	*ident;
	int isWriteInt;
	struct expression *expr;
};

union statementTypes
{
	struct assignment *asgn;
	struct ifStatement *ifStmnt;
	struct whileStatement *whileStmnt;
	struct expression *writeInt; //writeInt only holds an expression, so I'll not make a separate struct
};

struct statement
{
	int type;
	union statementTypes typePntr;
};
struct statements
{
	struct statement *statement;
	struct statements *nextStatements;
};
struct declarations
{
	char	*ident;
	int type;
	struct declarations *nextDecl;
};

struct program
{
	struct declarations *decls;
	struct statements *statements;
};
%}

%union
{
	char	*sval;
	int		ival;
	struct declarations *decl;
	struct statements *statements;
	struct statement *statement;
	union statementTypes *statementTypes;
	struct assignment *assignment;
	struct ifStatement *ifStatement;
	struct whileStatement *whileStatement;
	struct expression *expression;
	struct simpleExpression *simpleExpression;
	struct term *term;
	struct factor *factor;
	struct program *program;
};
%token COMM		

%token WRITEINT	
%token READINT		


%token IF 			
%token THEN		
%token ELSE 		
%token BEGIN 		
%token END 		
%token WHILE 		
%token DO 			
%token PROGRAM 	
%token VAR 		
%token AS 			
%token INT 		
%token BOOL		
	
%token <ival> BOOLLIT	
%token <sval> IDENT	
%token <ival> NUM	
%token <sval> OP2			
%token <sval> OP3			
%token <sval> OP4			

%token LP 			
%token RP 			
%token ASGN 		
%token SC 			

%type <program> Program
%type <ival> type
%type <decl> declarations
%type <statements> statementSequence
%type <statement> statement
%type <expression> writeInt
%type <assignment> assignment
%type <ifStatement> ifStatement
%type <whileStatement> whileStatement
%type <expression> expression
%type <simpleExpression> simpleExpression
%type <assignment> assignment
%type <term> term
%type <factor> factor
%type <statements> elseClause
%start Program
%%

Program:
	PROGRAM declarations BEGIN statementSequence END
	{
		struct program *temp = calloc(1,sizeof(struct program));
		temp->decls = $2;
		temp->statements = $4;
		$$=temp;
		writeProgram($$);
	}
	;
	
declarations :
	VAR IDENT AS type SC declarations
	{
		struct declarations *newdecls = calloc(1,sizeof(struct declarations));
		addSymbol($2,$4);
		//printf("testdeclarations: %s as type# %i",$2,$4);
		newdecls->ident=$2;
		newdecls->nextDecl=$6;
		newdecls->type=$4;
		$$ = newdecls;
	}
	|
	/*empty*/
	{
	$$=NULL;
	}
	;

type:
	INT
	{$$=0;}
	|
	BOOL
	{$$=1;}
	;
	
statementSequence:
	statement SC statementSequence
	{
		struct statements *temp = calloc(1,sizeof(struct statements));
		temp->statement = $1;
		temp->nextStatements = $3;
		$$=temp;
		}
	|
	/*empty*/
	{
		$$=NULL;
		}
	;
	
statement:
	assignment
	{
		struct statement *temp = calloc(1,sizeof(struct statement));
		temp->type = 0; //assignment
		temp->typePntr.asgn = $1;
		$$=temp;
	}
	|
	ifStatement
	{
		struct statement *temp = calloc(1,sizeof(struct statement));
		temp->type = 1; //ifStatement
		temp->typePntr.ifStmnt = $1;
		$$=temp;
	}
	|
	whileStatement
	{
		struct statement *temp = calloc(1,sizeof(struct statement));
		temp->type = 2; //whileStatement
		temp->typePntr.whileStmnt = $1;
		$$=temp;
	}
	|
	writeInt
	{
		struct statement *temp = calloc(1,sizeof(struct statement));
		temp->type = 3; //writeInt
		temp->typePntr.writeInt = $1;
		$$=temp;
	}
	;
	
assignment:
	IDENT ASGN expression
	{
		struct assignment *temp = calloc(1,sizeof(struct assignment));
		if(isSymbolInTable($1)==0)
				{
					yyerror("undefined identifier; %s has not been previously declared\n");
				}
		temp->ident = $1;
		temp->expr = $3;
		temp->isWriteInt = 0;//false
		$$=temp;
	}
	|
	IDENT ASGN READINT
	{
		if(isSymbolInTable($1)==0)
				{
					yyerror("undefined identifier; %s has not been previously declared\n");
				}
		struct assignment *temp = calloc(1,sizeof(struct assignment));
		temp->ident = $1;
		temp->isWriteInt = 1;//true
		$$=temp;
	}
	;
	
ifStatement:
	IF expression THEN statementSequence elseClause END
	{
		struct ifStatement *temp = calloc(1,sizeof(struct ifStatement));
		temp->ifExpr = $2;
		temp->doStmnts = $4;
		temp->elseStmnts = $5;
		if($2->isUnary==1)
		{
			yyerror("An if statement's conditional is not a boolean expression");
		}
		$$=temp;
	}
	;
	
elseClause:
	ELSE statementSequence
	{
		$$=$2;
	}
	|
	/*empty*/
	;
	
whileStatement:
	WHILE expression DO statementSequence END
	{
		struct whileStatement *temp = calloc(1,sizeof(struct whileStatement));
		temp->whileCondition = $2;
		temp->doStmnts = $4;
		if($2->isUnary==1)
		{
			yyerror("A while statement's conditional is not a boolean expression");
		}
		$$=temp;
	}
	;

writeInt:
	WRITEINT expression
	{
		$$ = $2;
	}
	;
	
expression:
	simpleExpression OP4 simpleExpression
	{
		struct expression *temp = calloc(1,sizeof(struct expression));
		temp->sExpr1 = $1;
		temp->isUnary = 0; //false
		temp->operation = $2;
		temp->sExpr2 = $3;
		//printf("whatsappenin:%s",temp->sExpr2->term1->factor1->factor.num);
		$$=temp;
		//writeExpression($$);
	}
	|
	simpleExpression
	{
		struct expression *temp = calloc(1,sizeof(struct expression));
		temp->sExpr1 = $1;
		temp->isUnary = 1; //true
		$$=temp;
		//writeExpression($$);
	}
	;
	
simpleExpression:
	term OP3 term
	{
		struct simpleExpression *temp = calloc(1,sizeof(struct simpleExpression));
		temp->isUnary = 0;//false
		temp->term1=$1;
		temp->operation=$2;
		temp->term2=$3;
		$$=temp;
	}
	|
	term
	{
		struct simpleExpression *temp = calloc(1,sizeof(struct simpleExpression));
		temp->isUnary = 1;//true
		temp->term1=$1;
		$$=temp;
		//printf("whatsappenin:%i",$1->factor1->factor.num);
	}
	;
	
term:
	factor OP2 factor
	{
		struct term *temp = calloc(1,sizeof(struct term));
		temp->isUnary = 1;//true
		temp->factor1=$1;
		temp->operation=$2;
		temp->factor2=$3;
		$$=temp;
	}
	|
	factor
	{
		struct term *temp = calloc(1,sizeof(struct term));
		temp->isUnary = 1;//true
		temp->factor1=$1;
		$$=temp;
		//printf("whatsappenin:%i",$1->factor.num);
	}
	;
	
factor:
	IDENT
	{
		struct factor *temp = calloc(1,sizeof(struct factor));
		temp->type = 0;//IDENT
		if(isSymbolInTable($1)==0)
				{
					yyerror("undefined identifier; %s has not been previously declared\n");
				}
		temp->factor.ident=$1;
		$$=temp;
	}
	|
	NUM
	{
		struct factor *temp = calloc(1,sizeof(struct factor));
		temp->type = 1;//NUM
		temp->factor.num=$1;
		//printf("whatsappenin:%i",$1);
		$$=temp;
	}
	|
	BOOLLIT
	{
		struct factor *temp = calloc(1,sizeof(struct factor));
		temp->type = 2;//bool
		temp->factor.bool=$1;
		$$=temp;
	}
	|
	LP expression RP
	{
		struct factor *temp = calloc(1,sizeof(struct factor));
		temp->type = 3;//parentheses
		temp->factor.expr=$2;
		$$=temp;
	}
	;

%%


void addSymbol(char* name, int type)
{
	int temp = symNum;
	symbols[temp].name = name;
	symbols[temp].type = type;
	symNum++;
}

int isSymbolInTable(char* name)
{
int i = 0;
	for(i; i < symNum; i++)
	{
		if(strcmp(name,symbols[i].name) == 0)
		{
			//printf("%s is in the table\n",name);
			return 1;
		}
	}
	return 0;
}

void printTable()
{
printf("contents of the symboltable:\n");
int i = 0;
	for(i; i < symNum; i++)
	{
		
			if(symbols[i].type == integer)
			{
				printf("int ");
			}
			else
			{
				printf("bool ");
			}
			printf("%s\n",symbols[i].name);
		
	}
}
int main(void) {
	int temp = 0;
	//while(1==1)
	//{
		temp =  yyparse();
		//printf("compiled if no errors");
		//printTable();
	//}
}

yyerror(s)
char *s;
{
  fprintf(stderr, "%s at line %i\n",s,linenum);
  exit(-1);
}

yywrap()
{
  return(1);
}

void indent()
{
	int i = 0;
	while(i<indentNum)
	{
		printf("\t");
		i++;
	}
}

void writeProgram(struct program *prg)
{
	linenum = 1;
	printf("#include<stdio.h>\n");
	linenum++;
	printf("#include<stdlib.h>\n");
	linenum++;
	printf("#include<stdbool.h>\n");
	linenum++;
	printf("int main()\n{\n");
	linenum++;
	linenum++;
	indentNum++;
	writeDeclarations(prg->decls);
	writeStatements(prg->statements);
	indentNum--;
	printf("return 0;}");
}

writeDeclarations(struct declarations *temp)
{
	if(temp==NULL) return;
	if(temp->type==integer)
		{
			indent();
			printf("int %s;\n",temp->ident);
			linenum++;
		}
		else
		{
			indent();
			printf("bool %s;\n",temp->ident);
			linenum++;
		}
		writeDeclarations(temp->nextDecl);
}

void writeStatements(struct statements *temp)
{
	//printf("still livin");
	if(temp==NULL)
	{
		return;
	}
	writeStatement(temp->statement);
	writeStatements(temp->nextStatements);
}

void writeStatement(struct statement *temp)
{
	switch (temp->type){
		case 0: writeAssignment(temp->typePntr.asgn); return;
		case 1: writeIf(temp->typePntr.ifStmnt); return;
		case 2: writeWhile(temp->typePntr.whileStmnt); return;
		case 3: writeWriteInt(temp->typePntr.writeInt);
	}
}

void writeIf(struct ifStatement *temp)
{
	indent();
	printf("if(");
	writeExpression(temp->ifExpr);
	printf(")\n");
	linenum++;
	indent();
	//printf("wtf");
	indentNum++;
	printf("{\n");
	linenum++;
	writeStatements(temp->doStmnts);
	indentNum--;
	indent();
	printf("}\n");
	linenum++;
	writeElse(temp->elseStmnts);
}
	
void writeElse(struct statements *temp)
{
	indent();
	printf("else\n");
	linenum++;
	indent();
	printf("{\n");
	linenum++;
	indentNum++;
	writeStatements(temp);
	indentNum--;
	indent();
	printf("}\n");
	linenum++;
}

void writeWhile(struct whileStatement *temp)
{
	indent();
	printf("while(");
	writeExpression(temp->whileCondition);
	printf(")\n");
	linenum++;
	indent();
	printf("{\n");
	linenum++;
	indentNum++;
	writeStatements(temp->doStmnts);
	indentNum--;
	indent();
	printf("}\n");
	linenum++;
}

void writeWriteInt(struct expression *temp)
{	
	indent();
	printf("printf(\"%%i\",");
	writeExpression(temp);
	printf(");\n");
	linenum++;
}

void writeAssignment(struct assignment *temp)
{
	if(temp->isWriteInt == 0)
	{
		indent();
		printf("%s = ",temp->ident);
		writeExpression(temp->expr);
		printf(";\n");
		linenum++;
	}
	else
	{
		indent();
		printf("scanf(\"%%d\",&%s);\n",temp->ident);
		linenum++;
	}
}

void writeExpression(struct expression *temp)
{
	writeSimpleExpression(temp->sExpr1);
	if(temp->isUnary==0)
	{
		printf("%s",temp->operation);
		writeSimpleExpression(temp->sExpr2);
	}
}

void writeSimpleExpression(struct simpleExpression *temp)
{
	writeTerm(temp->term1);
	if(temp->isUnary==0)
	{
		printf("%s",temp->operation);
		writeTerm(temp->term2);
	}
}
void writeTerm(struct term *temp)
{
	writeFactor(temp->factor1);
	if(temp->isUnary==0)
	{
		printf("%s",temp->operation);
		writeFactor(temp->factor2);
	}
}
void writeFactor(struct factor *temp)
{
	switch (temp->type){
		case 0: {printf("%s",temp->factor.ident);
				return;}
		case 1: printf("%i",temp->factor.num); return;
		case 2: printf("%i",temp->factor.bool); return;
		case 3: 
			{
				printf("(");
				writeExpression(temp->factor.expr);
				printf(")");
			}
	}

}
