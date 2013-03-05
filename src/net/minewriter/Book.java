package net.minewriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {

	private JSONObject obj;
	private boolean inv;
	private String content;
	private String license;

	public Book(JSONObject obj) {
		this.obj = obj;		
		String cont = null;
		try {
			cont = obj.getString("Content");
			license = obj.getString("License");
			this.inv = false;
		} catch(Exception ex) {		
			this.inv = true;
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
	
	public String getLicense() {
		return license;
	}
	
	public String getTitle() throws JSONException {
		return obj.getString("Title");
	}
	
	public boolean isInvalid() {
		return inv;
	}
}
