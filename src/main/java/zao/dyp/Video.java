package zao.dyp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static zao.dyp.DYP.*;
import static zao.dyp.Video.Status.DONE;
import static zao.dyp.Video.Status.OK;

class Video extends Job {

	enum Status {
		NEW,
		OK,
		DONE,
		UNABLE
	}

	private static final String[] theFormats = {
			"",
			"-f best",
			"-f bestvideo+bestaudio --merge-output-format mkv"
	};

	Integer owner;
	Integer idx;
	Status status;
	String fileName;

	Video(String id, Integer owner, Integer idx, Status status) {
		this.id = id;
		this.owner = owner;
		this.idx = idx;
		this.status = status;
	}


	@Override
	int calcHashCode() {
		return 127 * owner + id.hashCode();
	}

	@Override
	String genCL() throws MalformedURLException {

		Playlist playlist = thePlaylists.get(owner);

		String idxPart = (idx == null) ? "" : String.format("%03d｜", idx);
		String datePart = (playlist.withDate) ? "%(upload_date)s｜" : "";
		URL url = new URL("https://www.youtube.com/watch?v=" + id);
		String output = String.format("-o \"%s/%s/%s%s%%(title)s.%%(ext)s\"", theRepoDir, playlist.dirName, idxPart, datePart);
		String formatPart = theFormats[playlist.formatLv];

		String ans = String.format("%s %s --no-part %s %s %s %s", theCommand, theProxyPart, theSubtitlePart, url, output, formatPart);
		return ans;
	}

	@Override
	public Object call() throws Exception {
		Playlist playlist = thePlaylists.get(owner);
		File playlistsDir = new File(theRepoDir, playlist.dirName);
		switch (status) {
			case DONE:
				File file = new File(playlistsDir, fileName);
				if (file.exists()) {
					break;
				} else {
					System.out.printf("WTF! Where is %s ?!", fileName);
				}
			case NEW:
			case OK:
				status = OK;
				String[] res = (String[]) super.call();
				updateInfo(res);
				break;
		}
		return null;
	}

	private void updateInfo(String[] res) throws IOException {
		boolean ok = true;
		String[] errorLines = res[1].split("\\v");
		for (String errorLine : errorLines) {
			if (!errorLine.toLowerCase().contains("subtitle")) {
				ok = false;
				break;
			}
		}
		if (ok) {
			synchronized (theVideos) {
				fileName = parseFinaName(res);
				status = DONE;
				theVideos.save(theVideosJsonFile);
			}
			System.out.println("Done: " + fileName);
		} else {
			printResult(res);
		}
	}

	private String parseFinaName(String[] res) {

		return null;
	}
}
