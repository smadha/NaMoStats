package edu.hacktech.namo.fetcher.query;

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
		System.out.println(timeLine);
		return timeLine;
	}

	public static void main(String[] args) throws Exception {
		Gson gson = new Gson();
		QueryTwitter query = new QueryTwitter();
		// System.err.println(query.getProfile("HillaryClinton"));
		int start = 1;
		int size = 200;
		while (true) {
			start += size;
			ResponseList<Status> obj = query.getTweetsFromProfile("HillaryClinton", start, size);

			if (obj == null) {
				break;
			}
			String json = gson.toJson(obj);
			System.err.println(json);

		}
	}
}