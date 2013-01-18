package net.minewriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateThread extends Thread {

	private int total;
	private int diff;
	private int i;
	private MWStats mw;

	UpdateThread(MWStats mw, int t, int i) {
		this.mw = mw;
		this.total = t;
		this.diff = total - i;
		this.i = i;
	}

	/**
	 * The SmartDiff code
	 */
	@Override
	public void run() {
		int c = diff + 1;
		for (int b = 1; b < (diff + 1); b++) {
			fetch((total + 1) - (c - 1));
			c--;
		}
		sleep(100);
		new StatThread(mw).start();
	}

	public void sleep(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void fetch(int i) {
		MWStats.log("Fetching book " + i);
		BufferedReader reader = null;
		try {
			URL url = new URL("http://minewriter.net/query.php?id=" + i
					+ "&type=JSON&mode=lookup");
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			try {
				mw.library.add(new Book(new JSONObject(buffer.toString())));
			} catch (JSONException e) {
				mw.library.add(new Book());
				MWStats.log("Fetching failed for book " + i + " Exception: "
						+ e.getLocalizedMessage());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}