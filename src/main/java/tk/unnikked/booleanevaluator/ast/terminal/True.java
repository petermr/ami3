package tk.unnikked.booleanevaluator.ast.terminal;

import tk.unnikked.booleanevaluator.ast.Terminal;

public class True extends Terminal {
	public True() {
		super(true);
	}

	public boolean interpret() {
		return value;
	}
}
