package zao.dyp;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static zao.dyp.DYP.*;
import static zao.dyp.Video.Status.*;

class Video extends Job {

	enum Status {
		READY,
		DONE,
		SHIT,
	}

	private static final String[] theFormats = {
			"",
			"-f best",
			"-f bestvideo+bestaudio --merge-output-format mkv",
	};

	private final Integer owner;
	private Status status;
	private String fileName;

	private Playlist myPlaylist() {
		return thePlaylists.get(owner);
	}

	Video(String id, Integer owner, Status status, Integer idx) {
		this.id = id;
		this.owner = owner;
		this.status = status;

		String idxPart = (idx == null) ? "" : String.format("%03d｜");
		String datePart = myPlaylist().withDate ? "%%(upload_date)s｜" : "";
		fileName = String.format("%s%s%%(title)s.%%(ext)s", idxPart, datePart);
	}

	@Override
	int calcHashCode() {
		return 127 * owner + id.hashCode();
	}


	private String genCL() throws MalformedURLException {

		URL url = new URL("https://www.youtube.com/watch?v=" + id);
		String output = String.format("-o \"%s/%s/%s\"", theRepoDir, thePlaylists.get(owner).dirName, fileName);
		String formatPart = theFormats[myPlaylist().formatLv];
		return String.format("%s %s --no-part --print-json %s %s %s %s", theCommand, theProxyPart, theSubtitlePart, url, output, formatPart);
	}

	@Override
	public Object call() throws IOException, InterruptedException {
		boolean ok = false;
		while (!ok) {
			if (status == READY) {
				String cl = genCL();
				String[] res = runCL(cl, true);

				if (analyzeResult(res) == 0) {
					JsonObject info = theJsonParser.parse(res[0]).getAsJsonObject();
					fileName = info.get("_filename").getAsString();
					theVideos.save(theVideosJsonFile);
					status = DONE;
				} else {
					System.out.println(res[1]);
				}
				continue;
			}
			if (status == DONE) {
				System.out.printf("\n[DONE]  %s  \"%s\\%s\"\n", id, thePlaylists.get(owner).dirName, fileName);
				ok = true;
				continue;
			}
			if (status == SHIT) {
				ok = true;
				continue;
			}
		}
		return status;
	}

	private static int analyzeResult(String[] res) {
		if (!res[1].isEmpty()) {
			return 1;
		}
		return 0;
	}


	private static String fixTitle(String oriTitle) {
		String ans = oriTitle;
		ans = ans.replaceAll("/", "_");
		ans = ans.replaceAll("\"", "`");
		return ans;
	}

}
