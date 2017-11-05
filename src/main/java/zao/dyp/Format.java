package zao.dyp;

class Format implements Comparable<Format> {
	Integer format_id;
	String vcodec;
	String acodec;
	Double tbr = 0D;
	String ext;

	boolean hasV() {
		return !vcodec.equals("none");
	}

	boolean hasA() {
		return !acodec.equals("none");
	}


	@Override
	public int compareTo(Format o) {
		return tbr.compareTo(o.tbr);
	}
}
