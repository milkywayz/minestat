package net.minewriter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateThread extends Thread {

	private int total;
	private int diff;
	private MWStats mw;
	private int p = -1;

	UpdateThread(MWStats mw, int t, int i) {
		this.mw = mw;
		this.total = t;
		this.diff = total - i;
	}

	/**
	 * The SmartDiff code
	 */
	@Override
	public void run() {
		int c = diff + 1;
		int t = total + 1;
		for (int b = 1; b < (diff + 1); b++) {
			fetch(t - (c - 1));
			c--;
		}
		new StatThread(mw).start();
	}

	public void fetch(int i) {
		int percent = (int) ((i * 100F) / total);
		BufferedReader reader = null;
		URL url;
		HttpURLConnection hCon;
		InputStream str;
		StringBuilder buffer = new StringBuilder();
		try {
			url = new URL("http://minewriter.net/lookup.php?id=" + i);
			hCon = (HttpURLConnection) url.openConnection();
			str = hCon.getInputStream();
			reader = new BufferedReader(new InputStreamReader(str));
			int read;
			char[] chars = new char[2048];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			reader.close();
			mw.library.add(new Book(new JSONObject(buffer.toString())));
		} catch (JSONException e) {
			mw.library.add(new Book());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (percent % 5 == 0) {
			if (p != percent) {
				p = percent;
				System.out.println();
				MWStats.log("Fetching at " + percent + "%");
				String date = MWStats.sdf.format(new Date());
				System.out.print(date + " [INFO] ");
			} else {
				System.out.print("|");
			}
		}
	}
}