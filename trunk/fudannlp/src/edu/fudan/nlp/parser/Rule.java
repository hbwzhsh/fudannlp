package edu.fudan.nlp.parser;
public abstract class Rule {
	protected int pState;
	public abstract int hashCode();
	public abstract boolean equals(Object o);
}
