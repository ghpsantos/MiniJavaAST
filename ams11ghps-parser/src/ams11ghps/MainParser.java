package ams11ghps;



import symboltable.SymbolTable;
import visitor.BuildSymbolTableVisitor;
import visitor.PrettyPrintVisitor;
import visitor.TypeCheckVisitor;
import ast.Program;


public class MainParser {
	public static void main(String[] args) throws Exception {
		Parser parser = new Parser();
		
		Program prog = (Program)parser.parse().value;
		
		BuildSymbolTableVisitor stVis = new BuildSymbolTableVisitor();
		//programa na forma de AST

		prog.accept(stVis); 
		//fazendo a checagem de tipos
		prog.accept(new TypeCheckVisitor(stVis.getSymbolTable())); 

	}
}
