package zao.dyp;

import java.io.File;
import java.io.IOException;

class Videos extends Jobs<Video> {

	Videos(File jsonFile) throws IOException {
		super(jsonFile, Video[].class);
	}

}
