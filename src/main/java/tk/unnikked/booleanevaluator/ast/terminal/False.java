package tk.unnikked.booleanevaluator.ast.terminal;

import tk.unnikked.booleanevaluator.ast.Terminal;

public class False extends Terminal {
	public False() {
		super(false);
	}

	public boolean interpret() {
		return value;
	}
}
