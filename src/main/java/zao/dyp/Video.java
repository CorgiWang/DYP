package zao.dyp;

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

	@Override
	public String call() throws Exception {
		Command.runCL(cl, "GBK");
		return null;
	}
}
