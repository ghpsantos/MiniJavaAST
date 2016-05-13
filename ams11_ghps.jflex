package ams11ghps;

import java_cup.runtime.*;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;
import java.lang.*;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

%%

%public
%class AMS11GHPS
%line
%column
%unicode
%cup
%implements sym
%char


//lembrar de colocar %implements sym


%{	
    public AMS11GHPS(ComplexSymbolFactory sf, java.io.InputStream is)throws UnsupportedEncodingException	{
		this(new InputStreamReader (is, "Cp1252"));
        symbolFactory = sf;
    }
	public AMS11GHPS(ComplexSymbolFactory sf, java.io.Reader reader){
		this(reader);
        symbolFactory = sf;
    }
    
    /*{
    	public Lexer(ComplexSymbolFactory sf, java.io.InputStream is){
		this(is);
        symbolFactory = sf;
    }
    */

    private StringBuffer sb;
    private ComplexSymbolFactory symbolFactory;
    private int csline,cscolumn;

    public Symbol symbol(String name, int code){
		return symbolFactory.newSymbol(name, code,
						new Location(yyline+1,yycolumn+1, yychar), // -yylength()
						new Location(yyline+1,yycolumn+yylength(), yychar+yylength())
				);
    }
    public Symbol symbol(String name, int code, String lexem){
	return symbolFactory.newSymbol(name, code, 
						new Location(yyline+1, yycolumn +1, yychar), 
						new Location(yyline+1,yycolumn+yylength(), yychar+yylength()), lexem);
    }
    
    protected void emit_warning(String message){
    	System.out.println("scanner warning: " + message + " at : 2 "+ 
    			(yyline+1) + " " + (yycolumn+1) + " " + yychar);
    }
    
    protected void emit_error(String message){
    	System.out.println("scanner error: " + message + " at : 2" + 
    			(yyline+1) + " " + (yycolumn+1) + " " + yychar);
    }
%}


%{
	int qtdToken = 0;
	int qtdID = 0;
%}

%eofval{
	    return symbolFactory.newSymbol("EOF",EOF);
%eofval}


//padroes
whitespace = [ \n\t\r\f]
iniDigit = [1-9]
digit = [0-9]
underline = _
integer = 0|{iniDigit}({digit})*
letter = [A-Za-z]
alphanumeric = {letter}|{digit}
id = ({letter}|{underline})({alphanumeric}|{underline})*
comment1 = \/\/.*
comment2 = "/*"~"*/"
comment = {comment1}|{comment2}

//regras
%%
boolean      {return symbolFactory.newSymbol(yytext(), BOOLEAN);}
class        {return symbolFactory.newSymbol(yytext(), CLASS);}
public       {return symbolFactory.newSymbol(yytext(), PUBLIC);}
extends      {return symbolFactory.newSymbol(yytext(), EXTENDS);}
static       {return symbolFactory.newSymbol(yytext(), STATIC);}
void         {return symbolFactory.newSymbol(yytext(), VOID);}
main         {return symbolFactory.newSymbol(yytext(), MAIN);}
String       {return symbolFactory.newSymbol(yytext(), STRING);}
int          {return symbolFactory.newSymbol(yytext(), INT);}
while        {return symbolFactory.newSymbol(yytext(), WHILE);}
if           {return symbolFactory.newSymbol(yytext(), IF);}
else         {return symbolFactory.newSymbol(yytext(), ELSE);}
return       {return symbolFactory.newSymbol(yytext(), RETURN);}
length       {return symbolFactory.newSymbol(yytext(), LENGTH);}
true         {return symbolFactory.newSymbol(yytext(), TRUE);}
false        {return symbolFactory.newSymbol(yytext(), FALSE);}
this         {return symbolFactory.newSymbol(yytext(), THIS);}
new          {return symbolFactory.newSymbol(yytext(), NEW);}
System.out.println       {return symbolFactory.newSymbol(yytext(), PRINT);}
String\[\] 				{return symbolFactory.newSymbol(yytext(), STRARG);}

{id}         {return symbolFactory.newSymbol(yytext(), IDENTIFIER, yytext());}
{comment}  	 {}
{integer}    {return symbolFactory.newSymbol(yytext(), INT, Integer.parseInt(yytext()));}
{whitespace}   {}

"&&"         {return symbolFactory.newSymbol(yytext(), AND);}
"<"          {return symbolFactory.newSymbol(yytext(), LESSTHAN);}
"=="         {return symbolFactory.newSymbol(yytext(), EQUAL);}
"!="         {return symbolFactory.newSymbol(yytext(), DIFFERENT);}
"+"          {return symbolFactory.newSymbol(yytext(), PLUS);}
"-"          {return symbolFactory.newSymbol(yytext(), MINUS);}
"*"          {return symbolFactory.newSymbol(yytext(), TIMES);}
"!"          {return symbolFactory.newSymbol(yytext(), NOT);}

"="          {return symbolFactory.newSymbol(yytext(), ASSIGN);}
";"          {return symbolFactory.newSymbol(yytext(), SEMI);}
"."          {return symbolFactory.newSymbol(yytext(), DOT);}
","          {return symbolFactory.newSymbol(yytext(), COMMA);}
"("          {return symbolFactory.newSymbol(yytext(), LPAREN);}
")"          {return symbolFactory.newSymbol(yytext(), RPAREN);}
"{"          {return symbolFactory.newSymbol(yytext(), LKEY);}
"}"          {return symbolFactory.newSymbol(yytext(), RKEY);}
"["          {return symbolFactory.newSymbol(yytext(), LBRACK);}
"]"          {return symbolFactory.newSymbol(yytext(), RBRACK);}
\"           {return symbolFactory.newSymbol(yytext(), INVERTBAR);}
. 			 { throw new RuntimeException("Caractere ilegal! '" + yytext() + 
				"' na linha: " + yyline + ", coluna: " + yycolumn); }