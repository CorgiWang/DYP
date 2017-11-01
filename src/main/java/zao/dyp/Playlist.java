package zao.dyp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashSet;
import java.util.Set;

import static zao.dyp.DYP.theVideos;

class Playlist extends Job {

	private static final JsonParser theJsonParser = new JsonParser();
	private static final Set<String> theBadTitles = Set.of("[Deleted video]", ";[Deleted video]");

	private String dirPath;
	private Integer formatIndex;
	private Character orderSign;
	private Boolean withDate;

	public Playlist(String id) {
		super(id);
	}

	@Override
	public String call() throws Exception {
		Command cmd = new Command(id);
		String[] res = cmd.run();
		JsonArray jsonArray = theJsonParser.parse(res[0]).getAsJsonObject().getAsJsonArray("entries");

		int size = jsonArray.size();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject entry = jsonArray.get(i).getAsJsonObject();
			String videoID = entry.getAsJsonPrimitive("id").getAsString();
			if (!theVideos.jobs.contains(videoID)) {
				String title = entry.getAsJsonPrimitive("title").getAsString();
				if (theBadTitles.contains(title)) {
					theVideos.jobs.add(new Video(videoID));
				} else {
					Integer orderIndex = null;
					switch (orderSign) {
						case '+':
							orderIndex = i + 1;
							break;
						case '-':
							orderIndex = size - i;
							break;
					}
					cmd = new Command(videoID, formatIndex, dirPath, orderIndex, withDate);
					theVideos.jobs.add(new Video(videoID, false, cmd.toString()));
				}
			}
		}
		return null;
	}


}




