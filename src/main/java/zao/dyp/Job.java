package zao.dyp;

import java.util.concurrent.Callable;

abstract class Job implements Callable<String> {
	private  String id;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Job job = (Job) o;
		return id.equals(job.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
