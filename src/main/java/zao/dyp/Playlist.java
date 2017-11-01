package zao.dyp;

import com.google.gson.JsonParser;

class Playlist extends Job {

	private static final JsonParser theJsonParser = new JsonParser();

	String dirPath;
	Integer formatIndex;
	Character orderSign;
	Boolean withDate;

	public Playlist(String id) {
		super(id);
	}

	@Override
	public String call() throws Exception {
		return null;
	}


}




