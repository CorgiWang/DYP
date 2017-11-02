package zao.dyp;

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

	private static boolean analyzeResult(String[] res) {

		boolean ans = true;

		String[] lines = res[1].split("\\v");
		for (String line : lines) {
			if (!line.toLowerCase().contains("subtitle")) {
				ans = false;
				System.out.println(line);
				break;
			}
		}

		return ans;
	}


	@Override
	public String call() throws Exception {
		if (!done) {
			String[] res = Command.runCL(cl, "GBK");
			done = analyzeResult(res);
			if (done) {
				theVideos.writeBack();
			}
		}
		return null;
	}
}
