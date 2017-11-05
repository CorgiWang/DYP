package zao.dyp;

import java.io.File;
import java.io.IOException;

class Playlists extends Jobs<Playlist> {


	Playlists(File jsonFile) throws IOException {
		super(jsonFile, Playlist[].class);
	}

	@Override
	Object run() throws InterruptedException {

		System.out.println("\nPlaylist Job started ...");
		Object ans = super.run();
		System.out.println("\nPlaylist Job finished !!!");
		return ans;
	}
}
