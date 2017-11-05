package zao.dyp;

import java.io.File;
import java.io.IOException;

import static zao.dyp.DYP.thePlaylists;
import static zao.dyp.DYP.theRepoDir;
import static zao.dyp.Video.Status.DONE;
import static zao.dyp.Video.Status.READY;

class Videos extends Jobs<Video> {

	Videos(File jsonFile) throws IOException {
		super(jsonFile, Video[].class);
	}



	@Override
	Object run() throws InterruptedException {

		for (Video video : values()) {
			if (video.status == DONE) {
				Playlist playlist = thePlaylists.get(video.owner);
				File playlistDir = new File(theRepoDir, playlist.dirName);
				File videoFile = new File(playlistDir, video.fileName);
				if (!videoFile.exists()) {
					System.out.printf("\nFile disappeared: \"%s\"\n", videoFile.getAbsolutePath());
					video.status = READY;
				}
			}
		}

		return super.run();
	}
}
