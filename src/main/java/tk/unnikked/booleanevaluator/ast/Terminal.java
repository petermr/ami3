package tk.unnikked.booleanevaluator.ast;

public abstract class Terminal implements BooleanExpression{
	protected boolean value;

	public Terminal(boolean value) {
		this.value = value;
	}

	public String toString() {
		return String.format("%s", value);
	}
}
