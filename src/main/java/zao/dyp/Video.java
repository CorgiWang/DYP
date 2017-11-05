package zao.dyp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static zao.dyp.DYP.*;
import static zao.dyp.Video.Status.*;

class Video extends Job {

	enum Status {
		NEW,
		READY,
		ERROR,
		DONE,
		SHIT,
	}

	final Integer owner;
	final Integer idx;
	Status status;
	String fileName;
	List<Integer> formatIDs;

	Video(String id, Integer owner, Integer idx) {
		this.id = id;
		this.owner = owner;
		this.idx = idx;
	}

	@Override
	int calcHashCode() {
		return 127 * owner + id.hashCode();
	}

	private String genCL_New() throws MalformedURLException {
		URL url = new URL("https://www.youtube.com/watch?v=" + id);
		return String.format("%s %s %s --print-json --skip-download", theCommand, theProxyPart, url);
	}

	private String genCL_Ready() throws MalformedURLException {

		URL url = new URL("https://www.youtube.com/watch?v=" + id);
		String output = String.format("-o \"%s/%s/%s\"", theRepoDir, thePlaylists.get(owner).dirName, fileName);
		String formatPart = "";
		switch (formatIDs.size()) {
			case 1:
				formatPart = "-f " + formatIDs.get(0);
				break;
			case 2:
				formatPart = String.format("-f %d+%d --merge-output-format mkv", formatIDs.get(0), formatIDs.get(1));
				break;
		}
		return String.format("%s %s --no-part --print-json %s %s %s %s", theCommand, theProxyPart, theSubtitlePart, url, output, formatPart);
	}


	@Override
	public Object call() throws IOException {
		String cl;
		String[] res = new String[2];
		boolean ok = false;
		while (!ok) {
			switch (status) {
				case NEW:
					cl = genCL_New();
					res = runCL(cl, false);
					if (res[1].isEmpty()) {
						JsonObject videoInfo = theJsonParser.parse(res[0]).getAsJsonObject();

						String idxPart = (idx == null) ? "" : String.format("%03d｜", idx);
						String datePart = (thePlaylists.get(owner).withDate) ? videoInfo.get("upload_date").getAsString() + '｜' : "";
						String titlePart = fixTitle(videoInfo.get("title").getAsString());
						JsonArray formatJsonArray = videoInfo.getAsJsonArray("formats");
						String extPart = pickFormats(formatJsonArray);
						fileName = String.format("%s%s%s.%s", idxPart, datePart, titlePart, extPart);

						status = READY;
						theVideos.save(theVideosJsonFile);
					} else {
						status = ERROR;
						theVideos.save(theVideosJsonFile);
						System.out.println("\n[NEW]  WTF !!!");
						System.out.printf("\n%s\n", cl);
					}
					break;
				case READY:
					cl = genCL_Ready();
					res = runCL(cl, false);
					if (res[1].isEmpty()) {
						status = DONE;
						theVideos.save(theVideosJsonFile);
					} else {
						status = ERROR;
						theVideos.save(theVideosJsonFile);
						System.out.println("\n[READY]  WTF !!!");
						System.out.printf("\n%s\n", cl);
					}
					break;
				case ERROR:
					System.out.printf("\n[ERROR]  %s  \"%s\"\n", id, fileName);
					printResult(res);
					ok = true;
					break;
				case DONE:
					System.out.printf("\n[DONE]  %s  \"%s\"\n", id, fileName);
					ok = true;
					break;
				case SHIT:
					ok = true;
					break;
				default:
			}
		}
		return status;
	}

	private String pickFormats(JsonArray formatJsonArray) {

		Format bestVideoFormat = new Format();
		Format bestAudioFormat = new Format();

		for (JsonElement element : formatJsonArray) {
			JsonObject entry = element.getAsJsonObject();
			Format format = theGson.fromJson(entry, Format.class);

			if (format.hasV()) {
				if (format.compareTo(bestVideoFormat) > 0) {
					bestVideoFormat = format;
				}
			}

			if (format.hasA()) {
				if (format.compareTo(bestAudioFormat) > 0) {
					bestAudioFormat = format;
				}
			}
		}

		String ans;
		if (bestAudioFormat == bestVideoFormat) {
			formatIDs = List.of(bestVideoFormat.format_id);
			ans = bestVideoFormat.ext;
		} else {
			formatIDs = List.of(bestVideoFormat.format_id, bestAudioFormat.format_id);
			ans = "mkv";
		}

		return ans;
	}

	private static String fixTitle(String oriTitle) {
		String ans = oriTitle;
		ans = ans.replaceAll("/", "_");
		ans = ans.replaceAll("\"", "`");
		return ans;
	}

}
