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
import static net.minewriter.Color.*;

public class MWStats {

	protected final String diff = "http://minewriter.net/diff.php";
	public volatile List<Book> library = new ArrayList<Book>();
	public static int update;
	public final static SimpleDateFormat sdf = new SimpleDateFormat("H:m:ss");

	public static void main(String[] args) {
		MWStats mw = new MWStats();
		log(BLUE + "Starting new stats generator");
		int i;
		try {
			i = Integer.parseInt(args[0]);
		} catch (Exception ex) {
			i = 10;
		}
		update = i;
		log(BLUE + "Will calculate stats every " + i + " minutes");
		new DiffThread(mw).start();
		new ConsoleThread().start();
	}

	public static void log(String log) {
		String date = sdf.format(new Date());
		System.out.println(date + " [INFO] " + log + RESET);
	}
}

class ConsoleThread extends Thread {

	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String cmd = null;
		try {
			while (true) {
				cmd = br.readLine();
				if (cmd == null) {
					continue;
				}
				processCommand(cmd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processCommand(String cmd) {
		switch (cmd) {
		case "help":
			MWStats.log(BLUE + "Commands: help, exit");
			break;
		case "exit":
			System.exit(0);
			break;
		default:
			MWStats.log(RED + "Command '" + cmd + "' is not recognized, type 'help'");
			break;
		}
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
			total = 0;
		}
		int dif = total - i;
		if (dif < 0) {
			MWStats.log(RED + "Caught invalid difference, less than 0! -> " + dif);
		} else if (dif == 0) {
			MWStats.log(GREEN + "Stats appear to be up to date");
		} else {
			MWStats.log(MAGENTA + "Processing " + dif + " books into our cache");
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
