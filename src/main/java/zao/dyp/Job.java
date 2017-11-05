package zao.dyp;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

abstract class Job implements Callable<Object> {

	private static final Runtime theRT = Runtime.getRuntime();
	static String theCommand;
	static String theProxyPart;
	static String theSubtitlePart;

	private Integer hash;
	String id;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return (hashCode() == o.hashCode()) && ((Job) o).id.equals(id);
	}

	@Override
	public int hashCode() {
		if (hash == null)
			hash = calcHashCode();
		return hash;
	}

	@Override
	public String toString() {
		return Integer.toString(hashCode());
	}

	abstract int calcHashCode();

	static String[] runCL(String cl, boolean display) throws IOException {

		if (display) System.out.printf("\n%s\n", cl);

		Process p = theRT.exec(cl);
		String[] ans = new String[2];
		String csName = "GBK";

		InputStream is = p.getInputStream();
		ans[0] = new String(is.readAllBytes(), csName);
		is.close();

		InputStream es = p.getErrorStream();
		ans[1] = new String(es.readAllBytes(), csName);
		es.close();

		return ans;
	}

	static void printResult(String[] res) {
		System.out.println();
		System.out.println("================================================================");
		System.out.println();
		System.out.println(res[0]);
		System.out.println();
		System.out.println("                --------------------------------                ");
		System.out.println();
		System.out.println(res[1]);
		System.out.println();
		System.out.println("================================================================");
	}
}
