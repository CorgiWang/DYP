package zao.dyp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class DYP {

	private static class Conf {
		private String theRepoDirPath;
		private String thePlaylistsJsonFileName;
		private String theVideosJsonFileName;
		private String theCommand;
		private String theProxy;
		private String theLanguages;
		private Integer MX;
		private boolean needUpdate;
	}

	static final Gson theGson = new GsonBuilder().setLenient().setPrettyPrinting().disableHtmlEscaping().create();
	static final JsonParser theJsonParser = new JsonParser();

	static File theRepoDir;
	static File thePlaylistsJsonFile;
	static File theVideosJsonFile;


	static Playlists thePlaylists;
	static Videos theVideos;

	private static boolean needUpdate;


	static String readTextFile(File textFile, String cs) throws IOException {
		FileInputStream fis = new FileInputStream(textFile);
		String ans = new String(fis.readAllBytes(), cs);
		fis.close();
		return ans;
	}

	static void writeTextFile(String raw, File destFile, String cs) throws IOException {
		FileOutputStream fos = new FileOutputStream(destFile);
		fos.write(raw.getBytes(cs));
		fos.close();
	}


	private static void init(File dypJsonFile) throws IOException {

		String json = readTextFile(dypJsonFile, "UTF-8");
		Conf conf = theGson.fromJson(json, Conf.class);

		theRepoDir = new File(conf.theRepoDirPath);
		thePlaylistsJsonFile = new File(theRepoDir, conf.thePlaylistsJsonFileName);
		theVideosJsonFile = new File(theRepoDir, conf.theVideosJsonFileName);

		Job.theCommand = conf.theCommand;
		Job.theProxyPart = (conf.theProxy == null) ? "" : ("--proxy " + conf.theProxy);
		Job.theSubtitlePart = (conf.theLanguages == null) ? "" : ("--embed-subs --write-sub --sub-lang " + conf.theLanguages);

		Jobs.MX = conf.MX;

		needUpdate = conf.needUpdate;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		{
			File dypJsonFile = new File((0 == args.length) ? "DYP.json" : args[0]);
			init(dypJsonFile);

			theVideos = new Videos(theVideosJsonFile);
			thePlaylists = new Playlists(thePlaylistsJsonFile);
		}

		if (needUpdate) {
			thePlaylists.run();
			thePlaylists.save(thePlaylistsJsonFile);
			theVideos.save(theVideosJsonFile);
		}

		theVideos.run();

	}


}
