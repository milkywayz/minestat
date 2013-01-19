package net.minewriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {

	private JSONObject obj;
	private boolean inv;
	private String content;

	public Book(JSONObject obj) {
		this.obj = obj;
		this.inv = false;
		String cont = null;
		try {
			cont = obj.getString("Content");
		} catch(Exception ex) {			
		} finally {
			content = cont;
		}
	}
	
	public Book() {
		this.inv = true;
	}

	public String getContent() {
		return content;
	}
	
	public String getTitle() throws JSONException {
		return obj.getString("Title");
	}
	
	public boolean isInvalid() {
		return inv;
	}
}
