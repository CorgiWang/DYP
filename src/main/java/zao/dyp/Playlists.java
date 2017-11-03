package zao.dyp;

import java.io.File;
import java.io.IOException;

class Playlists extends Jobs<Playlist> {


	Playlists(File jsonFile) throws IOException {
		super(jsonFile, Playlist[].class);
	}

}
