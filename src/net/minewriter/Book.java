package net.minewriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {

	private JSONObject obj;
	private boolean inv;

	public Book(JSONObject obj) {
		this.obj = obj;
		this.inv = false;
	}
	
	public Book() {
		this.inv = true;
	}
	
	public JSONObject getJSON() {
		return this.obj;
	}
	
	public String getTitle() throws JSONException {
		return obj.getString("Title");
	}
	
	public boolean isInvalid() {
		return inv;
	}
}
