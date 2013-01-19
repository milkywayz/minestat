package net.minewriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

public class StatThread extends Thread {

	MWStats mw;
	private List<Book> localLibrary = new ArrayList<Book>();
	private List<Stat> stats = new ArrayList<Stat>();

	public StatThread(MWStats mw) {
		this.mw = mw;
	}

	@Override
	public void run() {
		for (Book b : mw.library) {
			// Filter valid books and create a nice copy of the library to stat
			if (!b.isInvalid()) {
				localLibrary.add(b);
			}
		}
		try {
			int chars = doCharacterCount();
			average(chars);
			count();
			favoriteWord();
			getLongestBook();
			favoriteLicense();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			writeJson(stats);
		} catch (Exception e) {
			e.printStackTrace();
		}
		localLibrary.clear();
	}

	public int doCharacterCount() throws JSONException {
		int chars = 0;
		for (Book b : localLibrary) {
			int chart = b.getContent().length();
			chars = chars + chart;
		}
		Stat s = new Stat("Chars", chars);
		stats.add(s);
		return chars;
	}

	public int average(int chars) {
		int avg = chars / localLibrary.size();
		Stat s = new Stat("AvgChars", avg);
		stats.add(s);
		return avg;
	}

	public int count() {
		int a = localLibrary.size();
		Stat s = new Stat("Count", a);
		stats.add(s);
		return a;
	}

	@SuppressWarnings("null")
	public String getLongestBook() throws JSONException {
		Map<String, Integer> books = new HashMap<String, Integer>();
		for (Book b : localLibrary) {
			int chart = b.getContent().length();
			books.put(b.getTitle(), chart);
		}
		Entry<String, Integer> maxEntry = null;

		for (Entry<String, Integer> entry : books.entrySet()) {
			if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
				maxEntry = entry;
			}
		}
		Stat s = new Stat("LongestBook", maxEntry.getKey());
		Stat st = new Stat("LongestBookLength", maxEntry.getValue());
		stats.add(s);
		stats.add(st);
		return maxEntry.getKey();
	}
	
	public String favoriteLicense() throws JSONException {
		Map<String, Integer> books = new HashMap<String, Integer>();
		for (Book b : localLibrary) {
			String license = b.getLicense();
			if(books.containsKey(license)) {
				int c = books.get(license);
				books.remove(license);
				books.put(license, c++);
				continue;
			}
			books.put(b.getLicense(), 0);
		}
		Entry<String, Integer> maxEntry = null;

		for (Entry<String, Integer> entry : books.entrySet()) {
			if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
				maxEntry = entry;
			}
		}
		Stat s = new Stat("FavLicense", maxEntry.getKey());
		stats.add(s);
		return maxEntry.getKey();
	}

	public String favoriteWord() throws JSONException {
		String giant = "";
		for (Book b : localLibrary) {
			giant = giant + b.getContent();
		}
		String[] words = giant.split(" ");
		List<String> list = Arrays.asList(words);
		int counter = 0;
		String mfw = "";
		for (String streamed : list) {
			if (streamed.equals(mfw)) {
				counter++;
			} else if (counter == 0) {
				mfw = streamed;
				counter = 1;
			} else {
				counter--;
			}
		}
		Stat st = new Stat("FavWord", mfw);
		stats.add(st);
		return mfw;
	}
	

	public void writeJson(List<Stat> sts) throws Exception {
		String json = "{";
		for (Stat t : sts) {
			json = json + "\"" + t.getTitle() + "\":" + "\"" + t.getValue() + "\",";
		}
		String j = json.substring(0, json.length()-1);
		j = j + "}";
		MWStats.log(j);
		sendJSON(new JSONObject(j));
	}

	public void sendJSON(JSONObject obj) throws JSONException {
		try {
			Upload.postJSON(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}