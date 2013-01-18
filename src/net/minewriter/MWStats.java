package net.minewriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MWStats {

	protected final String diff = "http://minewriter.net/diff.php";
	public volatile List<Book> library = new ArrayList<Book>();
	public static int update;
	private final static SimpleDateFormat sdf = new SimpleDateFormat("h:m:s");

	public static void main(String[] args) {
		MWStats mw = new MWStats();
		log("Starting new stats generator");
		int i = Integer.parseInt(args[0]);
		update = i;
		log("Will post stats every " + i + " minutes");
		new DiffThread(mw).start();
	}

	public static void log(String log) {
		String date = sdf.format(new Date());
		System.out.println(date + " [INFO] " + log);
	}
}

class DiffThread extends Thread {

	final MWStats mw;

	public DiffThread(MWStats mw) {
		this.mw = mw;
	}

	@Override
	public void run() {
		int c = mw.library.size();
		BufferedReader reader = null;
		try {
			URL url = new URL(mw.diff + "?has=0");
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			processDiff(buffer.toString(), c);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void processDiff(String diff, int i) {
		Integer total = null;
		try {
			total = Integer.parseInt(diff);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		int dif = total - i;
		if (dif < 0) {
			MWStats.log("Caught invalid difference, less than 0! -> " + dif);
		} else if (dif == 0) {
			MWStats.log("Stats are up to date");
		} else {
			MWStats.log("Processing " + dif + " books into our local database");
			new UpdateThread(mw, total, mw.library.size()).start();
		}
		try {
			Thread.sleep(1000 * 60 * MWStats.update);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		run();
	}
}
