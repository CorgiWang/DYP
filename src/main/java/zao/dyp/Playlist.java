package zao.dyp;

class Playlist extends Job {

	String id;
	String dirName;
	Boolean reverse;
	Boolean withDate;

	@Override
	int calcHashCode() {
		int ans = id.hashCode();
		ans = 31 * ans + (dirName != null ? dirName.hashCode() : 0);
		ans = 31 * ans + (reverse != null ? reverse.hashCode() : 0);
		ans = 31 * ans + (withDate != null ? withDate.hashCode() : 0);
		return ans;
	}

	@Override
	String genCL() {
		return null;
	}

	@Override
	public Object call() throws Exception {
		String[] res = (String[]) super.call();
		return res;
	}
}
