package zao.dyp;

import java.io.InputStream;
import java.util.concurrent.Callable;

abstract class Job implements Callable<Object> {

	private static final Runtime theRT = Runtime.getRuntime();
	static String theCommand;
	static String theProxyPart;
	static String theSubtitlePart;

	Integer hash;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return hashCode() == o.hashCode();
	}

	@Override
	public int hashCode() {
		if (hash == null) {
			hash = calcHashCode();
		}
		return hash;
	}

	abstract int calcHashCode();

	abstract String genCL();

	@Override
	public Object call() throws Exception {

		String csName = "GBK";
		String[] res = new String[2];
		Process p = theRT.exec(genCL());

		InputStream is = p.getInputStream();
		res[0] = new String(is.readAllBytes(), csName);
		is.close();

		InputStream es = p.getErrorStream();
		res[0] = new String(es.readAllBytes(), csName);
		es.close();

		return res;
	}
}
