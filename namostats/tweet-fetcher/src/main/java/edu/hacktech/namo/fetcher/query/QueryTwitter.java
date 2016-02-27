package edu.hacktech.namo.fetcher.query;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import twitter4j.*;

public class QueryTwitter {

	Twitter twitter = TwitterFactory.getSingleton();

	QueryTwitter() {
		twitter.addRateLimitStatusListener(new RateLimitStatusListener() {

			public void onRateLimitStatus(RateLimitStatusEvent arg0) {
				System.out.println(arg0.getRateLimitStatus());

			}

			public void onRateLimitReached(RateLimitStatusEvent arg0) {
				System.out.println(arg0.getRateLimitStatus());
			}
		});
	}

	public User getProfile(String tweeple) throws Exception {
		User usr = twitter.showUser(tweeple);
		return usr;
	}

	public ResponseList<Status> getTweetsFromProfile(String tweeple, int start, int size) throws Exception {
		ResponseList<Status> timeLine = twitter.getUserTimeline(tweeple, new Paging(start, size));

		return timeLine;
	}
	
	public void write(String fileName, String content) throws Exception{
		FileOutputStream out = new FileOutputStream(fileName);
		out.write(content.getBytes());
		out.close();
	}

	public static void main(String[] args) throws Exception {
		QueryTwitter query = new QueryTwitter();
		// System.err.println(query.getProfile("HillaryClinton"));
		
		int start = 1;
		int size = 400;
		
		List<Status> allStatus = new ArrayList<Status>();
		String candidate = "HuffPostPol";

		while (true) {

			ResponseList<Status> obj = null;
			try {
				obj = query.getTweetsFromProfile(candidate, start, size);
			} catch (Exception e) {
				e.printStackTrace();
				outputRecord(start, size, allStatus, candidate);
				break;
			}

			start ++;
			//size += incr;

			if (obj == null || obj.size() == 0) {
				outputRecord(start, size, allStatus, candidate);
				break;
			}
			allStatus.addAll(obj);

		}
	}

	/**
	 * @param start
	 * @param size
	 * @param allStatus
	 * @param candidate
	 * @throws Exception
	 */
	public static void outputRecord(int start, int size, List<Status> allStatus, String candidate) throws Exception {
		Gson gson = new Gson();
		QueryTwitter query = new QueryTwitter();
		System.out.println(start);
		System.out.println(size);
		String json = gson.toJson(allStatus);
		//System.err.println(json);
		query.write(candidate+System.currentTimeMillis()+"-"+start+".json", json);
	}
}