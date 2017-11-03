package zao.dyp;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static zao.dyp.DYP.*;


abstract class Jobs<T extends Job> extends HashSet<T> {

	static int MX;


	Jobs(File jsonFile, Class<T[]> jobArrayType) throws IOException {
		load(jsonFile, jobArrayType);
	}

	private void load(File jsonFile, Class<T[]> jobArrayType) throws IOException {
		if (jsonFile.exists()) {
			String json = readTextFile(jsonFile, "UTF-8");
			T[] jobArray = theGson.fromJson(json, jobArrayType);
			List<T> jobList = Arrays.asList(jobArray);
			addAll(jobList);
		} else {
			clear();
			save(jsonFile);
		}
	}

	private void save(File jsonFile) throws IOException {
		String json = theGson.toJson(this);
		writeTextFile(json, jsonFile, "UTF-8");
	}

	Object run() throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(1);
		List<Future<Object>> ans = pool.invokeAll(this);
		pool.shutdownNow();
		return ans;
	}
}
