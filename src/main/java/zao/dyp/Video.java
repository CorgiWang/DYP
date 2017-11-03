package zao.dyp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static zao.dyp.DYP.*;
import static zao.dyp.Video.Status.DONE;
import static zao.dyp.Video.Status.READY;

class Video extends Job {

	enum Status {
		READY,
		DONE,
		SHIT
	}

	private static final String[] theFormats = {
			"",
			"-f best",
			"-f bestvideo+bestaudio --merge-output-format mkv"
	};

	private final Integer owner;
	private Integer idx;
	private Status status;
	private String fileName;

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

		return String.format("%s %s --no-part %s %s %s %s", theCommand, theProxyPart, theSubtitlePart, url, output, formatPart);
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
			case READY:
				synchronized (theVideos) {
					status = READY;
					theVideos.save(theVideosJsonFile);
				}
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
			System.out.printf("Done: \"%s\"\n\n", fileName);
		} else {
			printResult(res);
		}
	}

	private String parseFinaName(String[] res) {

		String ans = null;
		Playlist playlist = thePlaylists.get(owner);
		String[] lines = res[0].split("\\v");

		switch (playlist.formatLv) {
			case 1:
				for (String line : lines) {
					if (line.startsWith("[download] Destination: ")) {
						String path = line.substring(24);
						ans = new File(path).getName();
						break;
					}
					if (line.endsWith(" has already been downloaded")) {
						String path = line.substring(11, line.length() - 28);
						ans = new File(path).getName();
						break;
					}
				}
				break;
			case 2:
				for (String line : lines) {
					if (line.startsWith("[ffmpeg] Merging formats into ")) {
						String path = line.substring(31, line.length() - 1);
						ans = new File(path).getName();
						break;
					}
					if (line.endsWith(" has already been downloaded and merged")) {
						String path = line.substring(11, line.length() - 39);
						ans = new File(path).getName();
						break;
					}
				}
				break;
		}

		if (ans == null)
			ans = Integer.toString(hashCode());
		return ans;
	}
}
