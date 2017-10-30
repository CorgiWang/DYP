package zao.util.dyp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import static zao.util.dyp.DYP.*;

public class VideoTask implements Callable {

	private String id;
	private String cl;

	VideoTask(String id, int format, String dirPath, int idx, boolean withDate, String title) throws MalformedURLException {
		this.id = id;
		String formatPart = theFormatParts[format];
		String orderPart = (idx == 0) ? "" : String.format("%03d｜", idx);
		String datePart = withDate ? "%(upload_date)s｜" : "";
		String outputPart = String.format("-o \"%s/%s%s%s.%%(ext)s\"", dirPath, orderPart, datePart, title);
		URL url = new URL("https://www.youtube.com/watch?v=" + id);
		cl = String.format("%s %s %s \"%s\" %s %s", theCommand, theProxyPart, theSubtitlePart, url, formatPart, outputPart);
	}


	@Override
	public Object call() throws Exception {
		String[] res = runCL(cl, "GBK");
//		System.out.println(" 0 ---> " + res[0]);
//		System.out.println(" 1 ---> " + res[1]);
		synchronized (theVideoTasks) {
			theExVideoIDs.add(id);
		}
		return null;
	}
}
