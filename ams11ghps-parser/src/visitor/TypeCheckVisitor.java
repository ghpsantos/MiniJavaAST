package visitor;

import symboltable.Method;
import symboltable.SymbolTable;
import symboltable.Variable;
import ast.And;
import ast.ArrayAssign;
import ast.ArrayLength;
import ast.ArrayLookup;
import ast.Assign;
import ast.Block;
import ast.BooleanType;
import ast.Call;
import ast.ClassDeclExtends;
import ast.ClassDeclSimple;
import ast.False;
import ast.Formal;
import ast.Identifier;
import ast.IdentifierExp;
import ast.IdentifierType;
import ast.If;
import ast.IntArrayType;
import ast.IntegerLiteral;
import ast.IntegerType;
import ast.LessThan;
import ast.MainClass;
import ast.MethodDecl;
import ast.Minus;
import ast.NewArray;
import ast.NewObject;
import ast.Not;
import ast.Plus;
import ast.Print;
import ast.Program;
import ast.This;
import ast.Times;
import ast.True;
import ast.Type;
import ast.VarDecl;
import ast.While;
import symboltable.Class;

public class TypeCheckVisitor implements TypeVisitor {

	private SymbolTable symbolTable;
	private Class currClass;
	private Method currMethod;
	
	public TypeCheckVisitor(SymbolTable st) {
		symbolTable = st;
	}

	// MainClass m;
	// ClassDeclList cl;
	public Type visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i1,i2;
	// Statement s;
	public Type visit(MainClass n) {
		this.currClass = this.symbolTable.getClass(n.i1.toString());
		n.i1.accept(this);
		n.i2.accept(this);
		n.s.accept(this);
		return null;
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclSimple n) {
		this.currClass = symbolTable.getClass(n.i.toString());
		n.i.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		
		
		this.currClass = null;
		return null;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclExtends n) {
		this.currClass = symbolTable.getClass(n.i.toString());
		
		n.i.accept(this);
		n.j.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		
		this.currClass = null;
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(VarDecl n) {
		n.t.accept(this);
		n.i.accept(this);
		return null;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Type visit(MethodDecl n) {
		
		this.currMethod = this.currClass.getMethod(n.i.toString());
		
		n.t.accept(this);
		n.i.accept(this);
	
		for (int i = 0; i < n.fl.size(); i++) {
			n.fl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		n.e.accept(this);
		
		if(!symbolTable.compareTypes(n.t, n.e.accept(this))){
			System.out.println("A função a seguir possui retorno e declação de retorno incompatíveis");
			n.accept(new PrettyPrintVisitor());
			System.out.println();
			
		}
		
		this.currMethod = null;
		
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(Formal n) {
		n.t.accept(this);
		n.i.accept(this);
		return null;
	}

	public Type visit(IntArrayType n) {
		return new IntArrayType();
	}

	public Type visit(BooleanType n) {
		return new BooleanType();
	}

	public Type visit(IntegerType n) {
		return new IntegerType();
	}

	// String s;
	public Type visit(IdentifierType n) {
		return n;
	}

	// StatementList sl;
	public Type visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Type visit(If n) {
		if(!(n.e.accept(this) instanceof BooleanType)){
			System.out.println("If - A expressão não é booleana: " + n.e.toString() );
			//throw new RuntimeException(msg);
		}else{
			n.s1.accept(this);
			n.s2.accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s;
	public Type visit(While n) {
		if(!(n.e.accept(this) instanceof BooleanType)){
			System.out.println("While - A expressão não é booleana: " + n.e.toString() );
			//throw new RuntimeException(msg);
		}else{
			n.s.accept(this);
		}
		
		return null;
	}

	// Exp e;
	public Type visit(Print n) {
		if(!(n.e.accept(this) instanceof Type)){
			System.out.println("Print - Tipo invalido: " + n.e.toString() );
			//throw new RuntimeException(msg);	
		}
		
		return null;
	}

	// Identifier i;
	// Exp e;
	public Type visit(Assign n) {
		//verifica se a variavel existe
		boolean declarededVar = true;
		Variable var;
		if(!currMethod.equals(null)){
			//verifica nos parametros
			 var = currMethod.getParam(n.i.toString());
			 //verifica nas variaveis
			 if(var == null)
				 var = currMethod.getVar(n.i.toString());
			 
		// verifica se existe no metodo
			if(var == null){
				// verifica se existe na classe
				var = currClass.getVar(n.i.toString());
				// se não existir = erro
				if(var == null){
					declarededVar = false;
					System.out.println("Variavel não declarada: " + n.i.toString());
					//throw new RuntimeException(msg);
				}
					
			}
		}else{
			//verifica na classe
			 var = currClass.getVar(n.i.toString());
			// se não existir = erro
			if(var == null){
				declarededVar = false;
				System.out.println("Variavel não declarada: " + n.i.toString());
				//throw new RuntimeException(msg);
			}
		
		}
		
		if(declarededVar){
			//o var vai estar iniciado; verificar os tipos
			if(!this.symbolTable.compareTypes(var.type(), n.e.accept(this))){
				System.out.print("Tipos incompativeis " );
				n.accept(new PrettyPrintVisitor());
				System.out.println();
				//throw new RuntimeException(msg);
			}
		}
		
		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Type visit(ArrayAssign n) {
		Variable var;
		boolean declarededVar = true;
		if(!currMethod.equals(null)){
			 var = currMethod.getParam(n.i.toString());
			 
			 if(var == null)
				 var = currMethod.getVar(n.i.toString());
			 
		// verifica se existe no metodo
			if(var == null){
				// verifica se existe na classe
				var = currClass.getVar(n.i.toString());
				// se não existir = erro
				if(var == null){
					declarededVar = false;
					System.out.println("Variavel não declarada: "+ n.i.toString());
					//throw new RuntimeException(msg);
				}
					
			}
		}else{
			//verifica na classe
			 var = currClass.getVar(n.i.toString());
			// se não existir = erro
			if(var == null){
				declarededVar = false;
				System.out.println("Variavel não declarada: "+ n.i.toString());
				//throw new RuntimeException(msg);
			}
		
		}
		
		if(declarededVar){
			//o var vai estar iniciado; verificar os tipos
			if(!(var.type() instanceof IntArrayType && n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)){
				System.out.println("Erro na atribuição do array: ");
				n.accept(new PrettyPrintVisitor());
				System.out.println();
				//throw new RuntimeException(msg);
			}
		}
		return null;
	}

	// Exp e1,e2;
	public Type visit(And n) {
		
		if(!(n.e1.accept(this) instanceof BooleanType && n.e2.accept(this) instanceof BooleanType)){
			System.out.println("O operador '&&' deve operar com variaveis do tipo int");
			n.accept(new PrettyPrintVisitor());
			System.out.println();
			//throw new RuntimeException("Erro na expressão And");
		}
		
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(LessThan n) {
		if(!(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)){
			System.out.println("O operador '<' deve operar com variaveis do tipo int");
			n.accept(new PrettyPrintVisitor());
			System.out.println();
			//throw new RuntimeException("Erro na expressão Less");
		}
		
		return new BooleanType();

	}

	// Exp e1,e2;
	public Type visit(Plus n) {
		if(!(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)){
			System.out.println("O operador '+' deve operar com variaveis do tipo int");
			n.accept(new PrettyPrintVisitor());
			System.out.println();
			//throw new RuntimeException("Erro na expressão Plus");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Minus n) {
		if(!(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)){
			System.out.println("O operador '-' deve operar com variaveis do tipo int");
			n.accept(new PrettyPrintVisitor());
			System.out.println();
			
			//throw new RuntimeException("Erro na expressão Minus");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Times n) {
		if(!(n.e1.accept(this) instanceof IntegerType && n.e2.accept(this) instanceof IntegerType)){
			System.out.println("O operador '*' deve operar com variaveis do tipo int");
			n.accept(new PrettyPrintVisitor());
			System.out.println();			
			//throw new RuntimeException("Erro na expressão Times");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(ArrayLookup n) {
		if(!(n.e1.accept(this) instanceof IntArrayType && n.e2.accept(this) instanceof IntegerType)){
			System.out.println("Erro no LookUp do Array a seguir: ");
			n.accept(new PrettyPrintVisitor());
			System.out.println();
			//throw new RuntimeException("Erro no lookUp do array");
		}
		
		return new IntegerType();
	}

	// Exp e;
	public Type visit(ArrayLength n) {
		if(!(n.e.accept(this) instanceof IntArrayType)){
			System.out.println("A expressão a seguir não é um array: ");
			n.accept(new PrettyPrintVisitor());
			System.out.println();
			
			//throw new RuntimeException("O tipo não é um array");
		}
		
		return new IntegerType();
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Type visit(Call n) {
		boolean declarededMethod = true;
		Type tipo = n.e.accept(this);
		if(!(tipo instanceof IdentifierType)){
			System.out.println("O retorno do call não é aceito, função: " + n.i.toString());
			//throw new RuntimeException("O retorno não é um tipo aceito");
		
		}else{
			// ja imprime se o metodo não tiver no escopo
			Method metodo = this.symbolTable.getMethod(n.i.toString(), ((IdentifierType)tipo).s);
			if(metodo == null){
				declarededMethod = false;
			}else if(n.el.size() != metodo.size()){
				System.out.println("Você chamou a função " + n.i.toString() + " com o numero de parametros incorreto");
				//throw new RuntimeException("numero de parametros da chamada incorretos");
			}else{
				for(int i = 0; i<metodo.size(); i++){
					if(!this.symbolTable.compareTypes(metodo.getParamAt(i).type(), n.el.elementAt(i).accept(this))){
						System.out.println("Você chamou o metodo: " + n.i.toString() +" com parametros de tipo incompativeis");
						break;
						//throw new RuntimeException("Tipos invalidos de parametros");
					}
					
				}
				
				//System.out.println(metodo.type());
				
			}
			//n.accept(new PrettyPrintVisitor());
			
			if(declarededMethod){
				return metodo.type();
			}
		}
		
		

		 return null;
	}

	// int i;
	public Type visit(IntegerLiteral n) {
		return new IntegerType();
	}

	public Type visit(True n) {
		return new BooleanType();
	}

	public Type visit(False n) {
		return new BooleanType();
	}

	// String s;
	public Type visit(IdentifierExp n) {
		Variable var;
		
		if(!currMethod.equals(null)){
			 var = currMethod.getParam(n.s.toString());
			 
			 if(var == null)
				 var = currMethod.getVar(n.s.toString());
				 
		// verifica se existe no metodo
			if(var == null){
				// verifica se existe na classe
				var = currClass.getVar(n.s.toString());
				// se não existir = erro
				if(var == null){
					System.out.println("Variavel não declarada: " + n.s.toString());
					//throw new RuntimeException(msg);
				}else {
					return var.type();
				}
			}else{
				return var.type();
			}
		}else{
			//verifica na classe
			 var = currClass.getVar(n.s.toString());
			// se não existir = erro
			if(var == null){
				System.out.println("Variavel não declarada: " + n.s.toString());
				//throw new RuntimeException(msg);
			}else {
				return var.type();
			}
		
		}
		
		return null;
	}

	public Type visit(This n) {
		return currClass.type();
		
	}

	// Exp e;
	public Type visit(NewArray n) {
		
		if(!(n.e.accept(this) instanceof IntegerType)){
			System.out.println("Tamanho do array invalido: ");
			n.accept(new PrettyPrintVisitor());
			System.out.println();
			//throw new RuntimeException("Não é um integer no parametro do array");
		}
		return new IntArrayType();
	}

	// Identifier i;
	public Type visit(NewObject n) {
		return new IdentifierType(n.i.toString());
	}

	// Exp e;
	public Type visit(Not n) {
		if(!(n.e.accept(this) instanceof BooleanType)){
			System.out.println("Argumento booleano invalido: ");
			n.accept(new PrettyPrintVisitor());
			System.out.println();
			
			//throw new RuntimeException("Não é um Boolean no not");
		}
		return new BooleanType();
	}

	// String s;
	public Type visit(Identifier n) {
		
		return null;
	}
}
