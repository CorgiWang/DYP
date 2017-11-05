package zao.dyp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import static zao.dyp.DYP.theJsonParser;
import static zao.dyp.DYP.theVideos;
import static zao.dyp.Video.Status.NEW;
import static zao.dyp.Video.Status.SHIT;

class Playlist extends Job {

	private static final Set<String> theBadTitles = Set.of("[Deleted video]", "[Private video]");

	boolean disabled;
	String dirName;
	Boolean reverse;
	Boolean withDate;


	@Override
	int calcHashCode() {
		int ans = id.hashCode();
		ans = 31 * ans + (dirName == null ? 0 : dirName.hashCode());
		ans = 31 * ans + (reverse == null ? 0 : reverse.hashCode());
		ans = 31 * ans + (withDate == null ? 0 : withDate.hashCode());
		return ans;
	}

	private String genCL() throws MalformedURLException {
		URL url = new URL("https://www.youtube.com/playlist?list=" + id);
		return String.format("%s %s --flat-playlist -J \"%s\"", theCommand, theProxyPart, url);

	}

	@Override
	public Object call() throws Exception {

		if (!disabled) {
			String[] res = runCL(genCL(), false);
			JsonArray videoEntries = theJsonParser.parse(res[0]).getAsJsonObject().getAsJsonArray("entries");

			int size = videoEntries.size();
			for (int i = 0; i < videoEntries.size(); i++) {
				JsonObject entry = videoEntries.get(i).getAsJsonObject();

				String videoID = entry.get("id").getAsString();
				Integer idx = (reverse == null) ? null : (reverse ? size - i : i + 1);
				Video video = new Video(videoID, hashCode(), idx);

				if (!theVideos.contains(video)) {
					String videoTitle = entry.get("title").getAsString();
					video.status = (theBadTitles.contains(videoTitle) ? SHIT : NEW);
					synchronized (theVideos) {
						theVideos.add(video);
					}
				}
			}
		}

		return null;
	}
}
