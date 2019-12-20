package tk.unnikked.booleanevaluator.ast;

import tk.unnikked.booleanevaluator.ast.nonterminal.And;
import tk.unnikked.booleanevaluator.ast.nonterminal.Not;
import tk.unnikked.booleanevaluator.ast.nonterminal.Or;
import tk.unnikked.booleanevaluator.ast.terminal.False;
import tk.unnikked.booleanevaluator.ast.terminal.True;

public class Test {
	public static void main(String[] args) {
		True t = new True();
		False f = new False();

		Or or = new Or();
		or.setLeft(t);
		or.setRight(f);

		Not not = new Not();
		not.setChild(f);
		And and = new And();
		and.setLeft(or);
		and.setRight(not);

		BooleanExpression root = and;

		System.out.println(root);
		System.out.println(root.interpret());
	}
}
