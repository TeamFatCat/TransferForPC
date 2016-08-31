package transfer.scan;

import java.util.List;
import java.util.Map;

public interface SearchEndOper {
	public void doIPListWork(List<String> ip_arr);
	public void doIPMapWork(Map<String, String> map);
}
