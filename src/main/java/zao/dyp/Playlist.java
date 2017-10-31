package zao.dyp;

import com.google.gson.JsonParser;

class Playlist extends Job {

	private static final JsonParser theJsonParser = new JsonParser();

	private String dirName;
	private int formatIndex;
	private char orderSign;
	private boolean withDate;

	@Override
	public String call() throws Exception {
		return null;
	}
}




