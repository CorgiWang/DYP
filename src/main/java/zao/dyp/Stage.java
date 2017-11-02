package zao.dyp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;

import static zao.dyp.DYP.readTextFile;
import static zao.dyp.DYP.theRepoDir;
import static zao.dyp.DYP.writeTextFile;


class Stage<T extends Job> {

	private static final Gson theGson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	private final Integer MX;
	private final File jsonFile;
	Collection<T> jobs;

	Stage(int mx, Class<T> jobType, Class<? extends Collection> colType) throws IOException {
		MX = mx;

		File file = null;
		String jobTypeName = jobType.getSimpleName();
		switch (jobTypeName) {
			case "Playlist":
			case "Video":
				file = new File(theRepoDir, jobTypeName + "s.json");
				if (!file.exists()) {
					writeTextFile("[]", file, "UTF-8");
				}
				break;
			default:
				System.out.println("Wrong Job Type: " + jobTypeName);
		}

		jsonFile = file;
		jobs = getJobs(jobType, colType);
	}

	private <J extends Job> Collection<J> getJobs(Class<J> jobType, Class<? extends Collection> colType) throws IOException {
		String json = readTextFile(jsonFile, "UTF-8");

		Job[] jobArray = (Job[]) Array.newInstance(jobType, 0);
		jobArray = theGson.fromJson(json, jobArray.getClass());
		List<J> jobList = Arrays.asList((J[]) jobArray);

		String colTypeName = colType.getTypeName();
		Collection<J> ans = null;
		switch (colTypeName) {
			case "java.util.List":
				ans = jobList;
				break;
			case "java.util.Set":
				ans = new HashSet<>(jobList);
				break;
			default:
				System.out.println("Wrong Collection Type: " + colTypeName);
		}

		return ans;
	}

	boolean runJobs() throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(MX);
		List<Future<String>> futures = pool.invokeAll(jobs);
		pool.shutdownNow();

		boolean ans = true;
		for (Future<String> future : futures) {
			if (future.isCancelled()) {
				ans = false;
				break;
			}
		}
		return ans;
	}

	void writeBack() throws IOException {
		String json;
		synchronized (jobs) {
			json = theGson.toJson(jobs);
		}
		synchronized (jsonFile) {
			writeTextFile(json, jsonFile, "UTF-8");
		}
	}
}

