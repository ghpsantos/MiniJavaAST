package ams11ghps;

import visitor.PrettyPrintVisitor;
import ast.Program;


public class MainParser {
	public static void main(String[] args) throws Exception {
		Parser parser = new Parser();
		parser.parse();
		
		Program prog = (Program)parser.parse().value;
	//	chama o visitor de pretty print
		prog.accept(new PrettyPrintVisitor()); 

	}
}
