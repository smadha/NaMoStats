package edu.hacktech.namo.fetcher.query;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import twitter4j.*;

public class QueryTwitter {

	Twitter twitter = TwitterFactory.getSingleton();

	public User getProfile(String tweeple) throws Exception {
		User usr = twitter.showUser(tweeple);
		return usr;
	}

	public ResponseList<Status> getTweetsFromProfile(String tweeple, int start, int size) throws Exception {
		ResponseList<Status> timeLine = twitter.getUserTimeline(tweeple, new Paging(start, size));
		
		return timeLine;
	}

	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();
		QueryTwitter query = new QueryTwitter();
		// System.err.println(query.getProfile("HillaryClinton"));
		int start = 1;
		int size = 200;
		int incr = size;
		List<Status> allStatus = new ArrayList();
		while (true) {
			
			ResponseList<Status> obj = null;
			try {
				obj = query.getTweetsFromProfile("HillaryClinton", start, size);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(start);
				System.out.println(size);
				String json = gson.toJson(allStatus);
				System.err.println(json);
			}
			
			start += incr;
			size += incr;
			
			if (obj == null || obj.size()==0) {
				System.out.println(start);
				System.out.println(size);
				String json = gson.toJson(allStatus);
				System.err.println(json);
				break;
			}
			allStatus.addAll(obj);
			String json = gson.toJson(obj);
			System.err.println(json);
		}
	}
}