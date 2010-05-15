package edu.fudan.ml.types;
public class Instance {
	private Object data;
	private Object target;
	private Object clause;
	public Instance(Object data) {
		this(data, null, 0);
	}
	public Instance(Object data, Object target) {
		this(data, target, 0);
	}
	public Instance(Object data, Object target, Object clause) {
		this.data = data;
		this.target = target;
		this.clause = clause;
	}
	public Object getTarget() {
		if (target == null)
			return data;
		return this.target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public void setClasue(String s) {
		this.clause = s;
	}
	public String getClasue() {
		return (String) this.clause;
	}
}
