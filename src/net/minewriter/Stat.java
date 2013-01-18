package net.minewriter;

public class Stat {

	private String title;
	private Object value;
	
	public Stat(String title, Object value) {
		this.title = title;
		this.value = value;
	}

	public String getTitle() {
		return title;
	}


	public Object getValue() {
		return value;
	}
}
