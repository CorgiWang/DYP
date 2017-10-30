package zao.util.dyp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

import static zao.util.dyp.DYP.*;

class PlaylistTask implements Callable {

	private boolean done = false;
	private String id;
	private String dirName;
	private int format;
	private char order;
	private boolean withDate;


	@Override
	public Object call() throws IOException {
		if (!done) {
			URL url = new URL("https://www.youtube.com/playlist?list=" + id);
			String cl = String.format("%s %s --flat-playlist -J \"%s\"", theCommand, theProxyPart, url);
			String json = runCL(cl, "GBK")[0];

			JsonArray videoEntries = theJsonParser.parse(json).getAsJsonObject().getAsJsonArray("entries");

			done = true;
			int size = videoEntries.size();
			String dirPath = new File(theRepoDir, dirName).getAbsolutePath();

			for (int i = 0; i < size; i++) {
				JsonObject videoEntry = videoEntries.get(i).getAsJsonObject();
				String videoID = videoEntry.getAsJsonPrimitive("id").getAsString();

				if (!theExVideoIDs.contains(videoID)) {
					String videoTitle = videoEntry.getAsJsonPrimitive("title").getAsString();
					if (theBadTitles.contains(videoTitle)) {
						synchronized (theExVideoIDs) {
							theExVideoIDs.add(videoID);
						}
					} else {
						int idx = 0;
						switch (order) {
							case '+':
								idx = i + 1;
								break;
							case '-':
								idx = size - i;
								break;
						}
						VideoTask videoTask = new VideoTask(videoID, format, dirPath, idx, withDate, videoTitle);
						synchronized (theVideoTasks) {
							theVideoTasks.add(videoTask);
							done = false;
						}
					}
				}
			}
		}
		return null;
	}
}




