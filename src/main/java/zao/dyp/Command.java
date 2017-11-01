package zao.dyp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static zao.dyp.DYP.theRepoDir;

class Command {

	private static final Runtime theRT = Runtime.getRuntime();
	private static final String theCS = "GBK";

	private static final String theBasicCmd = "youtube-dl.exe --proxy localhost:1080";
	private static final String thePlaylistCmd = theBasicCmd + " --flat-playlist -J";
	private static final String theVideoCmd = theBasicCmd + " --no-part --write-sub --sub-lang zh-CN,zh-TW,en --embed-subs";
	private static final String[] theFormatParts = {
			"",
			"-f best",
			"-f bestvideo+bestaudio --merge-output-format mkv"
	};

	private final String cl;

	@Override
	public String toString() {
		return cl;
	}


	Command(String playlistID) throws MalformedURLException {
		URL url = new URL("https://www.youtube.com/playlist?list=" + playlistID);
		cl = String.format("%s \"%s\"", thePlaylistCmd, url);
	}

	Command(String videoID, int formatIndex, String dirPath, Integer orderIndex, boolean withDate) throws MalformedURLException {

		URL url = new URL("https://www.youtube.com/watch?v=" + videoID);
		String formatPart = theFormatParts[formatIndex];

		String orderPart = (orderIndex == null) ? "" : String.format("%03d｜", orderIndex);
		String datePart = withDate ? "%(upload_date)s｜" : "";
		String outputPart = String.format("-o \"%s/%s/%s%s%%(title)s.%%(ext)s\"", theRepoDir, dirPath, orderPart, datePart);

		cl = String.format("%s \"%s\" %s %s", theVideoCmd, url, formatPart, outputPart);
	}


	static String[] runCL(String cl, String cs) throws IOException {

		System.out.println(cl);

		Process proc = theRT.exec(cl);
		String[] ans = new String[2];

		InputStream is = proc.getInputStream();
		ans[0] = new String(is.readAllBytes(), cs);
		is.close();

		InputStream es = proc.getErrorStream();
		ans[1] = new String(es.readAllBytes(), cs);
		es.close();

		return ans;
	}

	String[] run() throws IOException {
		return runCL(cl, theCS);
	}
}
