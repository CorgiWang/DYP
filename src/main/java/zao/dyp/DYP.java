package zao.dyp;

import java.io.*;
import java.util.*;

public class DYP {

	static File theRepoDir = null;
	static Stage<Video> theVideos = null;
	static Stage<Playlist> thePlaylists = null;

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

	private static void init(String[] args) throws IOException {
		int mx = (args.length < 1) ? 4 : Integer.valueOf(args[0]);
		theRepoDir = new File((args.length < 2) ? "D:/_Stream/_from YouTube/" : args[1]);
		theVideos = new Stage<>(mx, Video.class, Set.class);
		thePlaylists = new Stage<>(mx, Playlist.class, List.class);
	}


	public static void main(String[] args) throws IOException, InterruptedException {

		init(args);

		thePlaylists.runJobs();
		System.out.println();
		theVideos.writeBack();

		while (!theVideos.runJobs()) ;
	}
}
