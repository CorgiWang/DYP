package zao.dyp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class DYP {

	private static final Gson theGson = new GsonBuilder().setPrettyPrinting().create();

	private static File theRepoDir = null;

	private static File thePlaylistsJsonFile = null;
	private static Jobs<Playlist> thePlaylists = null;

	private static File theVideosJsonFile = null;
	private static Jobs<Video> theVideos = null;


	private static void init(String[] args) throws IOException {
		int mx = (args.length < 1) ? 4 : Integer.valueOf(args[0]);
		theRepoDir = new File((args.length < 2) ? "D:/_Stream/_from YouTube/" : args[1]);

		thePlaylistsJsonFile = new File(theRepoDir, "Playlists.json");
		thePlaylists = new Jobs<>(mx, 10);

		theVideosJsonFile = new File(theRepoDir, "Videos.json");
		theVideos = new Jobs<>(mx, 60);
	}

	private static void loadPlaylists() {

	}


	private static void updateVideoTasksFile() throws IOException {
	}


	public static void main(String[] args) throws IOException, InterruptedException {

		//	参数初始化
		init(args);

	}
}
