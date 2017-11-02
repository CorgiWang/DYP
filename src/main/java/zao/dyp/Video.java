package zao.dyp;

import java.util.Arrays;
import java.util.HashSet;

import static zao.dyp.DYP.theVideos;

public class Video extends Job {

	private Boolean done;
	private String cl;


	Video(String id) {
		super(id);
	}

	Video(String id, Boolean done, String cl) {
		this(id);
		this.done = done;
		this.cl = cl;
	}

	private static boolean isDone(String[] res) {

		boolean ans = false;

		String[] lines = res[0].split("\\v");
		for (String line : lines) {
			if (line.startsWith("[download] 100% of ")) ans = true;
		}

		return ans;
	}


	@Override
	public String call() throws Exception {
		if (!done) {
			String[] res = Command.runCL(cl, "GBK");
			synchronized (theVideos.jobs) {
				done = isDone(res);
			}
			if (done) {
				theVideos.writeBack();
			} else {
				Command.printResult(res);
			}
		}
		return null;
	}
}
