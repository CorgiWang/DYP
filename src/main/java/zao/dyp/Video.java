package zao.dyp;

import com.google.gson.JsonObject;

import java.io.File;
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
	private String filePath;

	private Playlist myPlaylist() {
		return thePlaylists.get(owner);
	}

	Video(String id, Integer owner, Status status, Integer idx) {
		this.id = id;
		this.owner = owner;
		this.status = status;

		String idxPart = (idx == null) ? "" : String.format("%03d｜",idx);
		String datePart = myPlaylist().withDate ? "%(upload_date)s｜" : "";
		filePath = String.format("%s/%s/%s%s%%(title)s.%%(ext)s", theRepoDir, myPlaylist().dirName, idxPart, datePart);
	}

	@Override
	int calcHashCode() {
		return 127 * owner + id.hashCode();
	}


	private String genCL() throws MalformedURLException {

		URL url = new URL("https://www.youtube.com/watch?v=" + id);
		String output = String.format("-o \"%s\"", filePath);
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

				switch (analyzeResult(res)) {
					case 0:
						JsonObject info = theJsonParser.parse(res[0]).getAsJsonObject();
						filePath = info.get("_filename").getAsString();
						theVideos.save(theVideosJsonFile);
						status = DONE;
						break;
					case 1:
						status = SHIT;
						break;
					default:
						System.out.println(res[1]);
				}

				continue;
			}
			if (status == DONE) {

				if (new File(filePath).exists()) {
					System.out.printf("\n[DONE]  %s  \"%s\"\n", id, filePath);
					ok = true;
				} else {
					System.out.printf("\n[File Lost]  %s  \"%s\"\n", id, filePath);
					status = READY;
				}


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
		if (res[1].contains("This video contains content from")) {
			return 1;
		}
		if (!res[1].isEmpty()) {
			return -1;
		}
		return 0;
	}


//	private static String fixTitle(String oriTitle) {
//		String ans = oriTitle;
//		ans = ans.replaceAll("/", "_");
//		ans = ans.replaceAll("\"", "`");
//		return ans;
//	}

}
