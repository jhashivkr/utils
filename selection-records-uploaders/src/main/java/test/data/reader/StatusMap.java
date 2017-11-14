package test.data.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class StatusMap<T, V extends SelectionRecord> implements AutoCloseable {
public class StatusMap<T, V> {

	private Map<T, Map<String, List<V>>> statusMap;

	public StatusMap() {
		statusMap = new HashMap<T, Map<String, List<V>>>();
	}

	public void close() throws Exception {
		System.out.println("Statusmap closed");
	}

	public Map<T, Map<String, List<V>>> getStatusMap() {
		return statusMap;
	}

}
