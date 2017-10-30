package zao.util.dyp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DYP {

	private static final Runtime theRuntime = Runtime.getRuntime();
	private static final Gson theGson = new GsonBuilder().setPrettyPrinting().create();
	static final JsonParser theJsonParser = new JsonParser();

	static final String theCommand = "youtube-dl.exe";
	static final String theProxyPart = "--proxy localhost:1080";
	static final String theSubtitlePart = "--write-sub --sub-lang zh-CN,zh-TW,en";
	static final String[] theFormatParts = {
			"",
			"-f best",
			"-f bestvideo+bestaudio --merge-output-format mkv"
	};
	static final Set<String> theBadTitles = new HashSet<>(List.of("[Deleted video]", "Private video"));

	private static int MX;
	static File theRepoDir;
	private static File theTasksFile;
	private static File theExVideoIDsFile;

	static Set<String> theExVideoIDs = null;
	static List<PlaylistTask> thePlaylistTasks = null;
	static List<VideoTask> theVideoTasks = null;

	private static String readTextFile(File file, String charSet) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		String ans = new String(fis.readAllBytes(), charSet);
		fis.close();
		return ans;
	}

	private static void writeTextFile(String raw, File file, String charSet) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(raw.getBytes(charSet));
		fos.close();
	}


	static String[] runCL(String cmdLine, String charSet) throws IOException {

		System.out.println(cmdLine);

		Process proc = theRuntime.exec(cmdLine);
		String[] ans = new String[2];

		InputStream is = proc.getInputStream();
		ans[0] = new String(is.readAllBytes(), charSet);
		is.close();

		InputStream es = proc.getErrorStream();
		ans[1] = new String(es.readAllBytes(), charSet);
		es.close();

		return ans;
	}

	private static void runTasks(List tasks, int mx, int timeoutSecs) throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(mx);
		pool.invokeAll(tasks, timeoutSecs, TimeUnit.SECONDS);
		pool.shutdownNow();
	}

	private static void init(String[] args) {
		int N = args.length;
		MX = (N < 1) ? 4 : Integer.valueOf(args[0]);
		theRepoDir = new File((N < 2) ? "D:/_Stream/_from YouTube/" : args[1]);
		theTasksFile = new File(theRepoDir, "tasks.json");
		theExVideoIDsFile = new File(theRepoDir, "videoIDs.txt");
	}

	private static void updateExVideoIDs() throws IOException {
		StringBuilder builder = new StringBuilder();
		for (String id : theExVideoIDs) {
			if (id.length() == 11) {
				builder.append(id);
				builder.append('\n');
			}
		}
		writeTextFile(builder.toString(), theExVideoIDsFile, "UTF-8");
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		//	参数初始化
		init(args);

		//	获取 theExVideoIDs
		String raw = readTextFile(theExVideoIDsFile, "UTF-8");
		theExVideoIDs = new HashSet<>(Arrays.asList(raw.split("\\v")));

		//	获取 thePlaylistTasks
		String tasksJson = readTextFile(theTasksFile, "UTF-8");
		thePlaylistTasks = Arrays.asList(theGson.fromJson(tasksJson, PlaylistTask[].class));

		boolean done = false;
		while (!done) {
			//	获取 theVideoTasks
			theVideoTasks = new ArrayList<>();
			runTasks(thePlaylistTasks, MX, 60);
			done = theVideoTasks.isEmpty();

			//	下载视频
			runTasks(theVideoTasks, MX, 600);

			//	更新 theExVideoIDsFile
			updateExVideoIDs();
		}
	}
}
