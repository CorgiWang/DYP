package zao.dyp;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static zao.dyp.DYP.*;


abstract class Jobs<T extends Job> extends HashMap<Integer, T> {

	static int MX;


	Jobs(File jsonFile, Class<T[]> jobArrayType) throws IOException {
		load(jsonFile, jobArrayType);
	}

	void add(T job) {
		put(job.hashCode(), job);
	}

	private void addAll(Collection<T> jobs) {
		for (T job : jobs) {
			add(job);
		}
	}

	boolean contains(T job) {
		return containsValue(job);
	}


	private void load(File jsonFile, Class<T[]> jobArrayType) throws IOException {

		if (jsonFile.exists()) {
			String json = readTextFile(jsonFile, "UTF-8");
			T[] jobArray = theGson.fromJson(json, jobArrayType);
			List<T> jobList = Arrays.asList(jobArray);
			addAll(jobList);
		} else {
			save(jsonFile);
		}
	}

	synchronized void save(File jsonFile) throws IOException {
		String json = theGson.toJson(this.values());
		writeTextFile(json, jsonFile, "UTF-8");
	}

	Object run() throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(MX);
		List<Future<Object>> ans = pool.invokeAll(this.values());
		pool.shutdown();
		return ans;
	}
}
