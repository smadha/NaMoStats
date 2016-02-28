package edu.hacktech.namo.fetcher.query;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class QueryTwitter {

	Twitter twitter = new TwitterFactory(new ConfigurationBuilder().setJSONStoreEnabled(true).build()).getInstance();// TwitterFactory.getSingleton();

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

	public void getTweetsUsingSearch(String keyword, long maxId) throws Exception {
		QueryTwitter query = new QueryTwitter();
		Query q = new Query(keyword);
		q.setCount(100);
		q.setSinceId(0);
		q.setResultType(Query.ResultType.popular);
		if (maxId >= 0) {
			q.setMaxId(maxId);
		}
		int count = 0;

		while (true) {

			QueryResult result = twitter.search(q);
			List<Status> statuses = result.getTweets();
			if (statuses == null || statuses.isEmpty()) {
				System.out.println("End");
				break;
			}
			// result.getRateLimitStatus();
			int writeCount = 0;
			for (Status s : statuses) {
				
/*				if (s.getUser().getScreenName().trim().equals(tweeple)) {
					writeCount++;
					query.write(tweeple+"-search" + ".json", TwitterObjectFactory.getRawJSON(s), true);
				} else {
					System.err.println(s.getUser().getScreenName());
					System.out.println(TwitterObjectFactory.getRawJSON(s));
				} */
				// print(s.user.id + "::" + s.createdAt + " :: " + s.text)
				query.write(keyword+"-search" + ".json", TwitterObjectFactory.getRawJSON(s), true);
				count++;
				writeCount++;
			}
			System.out.println("Written " + writeCount);
			System.out.println("Count = " + count);
			RateLimitStatus rLimit = result.getRateLimitStatus();
			System.out.println("Remaining " + rLimit.getRemaining() + ", resets in " + rLimit.getResetTimeInSeconds() + " Reset: "
					+ rLimit.getSecondsUntilReset());

			if (rLimit.getRemaining() < 1) {
				System.out.println("going to sleep for " + rLimit.getSecondsUntilReset());
				Thread.sleep(1000 * (rLimit.getSecondsUntilReset() + 1));
			}
			//if(1==1) throw new RuntimeException("FUNNY");
			q.setMaxId(statuses.get(statuses.size() - 1).getId());
			q.setCount(100);
			q.setSinceId(0);
			q.setResultType(Query.ResultType.recent);
			Thread.sleep(2000);
		}

	}

	public void write(String fileName, String content, boolean isAppend) throws Exception {
		content+="\n";
		FileOutputStream out = new FileOutputStream(fileName, isAppend);
		out.write(content.getBytes());
		out.write("\n".getBytes());
		out.close();
		//System.out.println("Written " + fileName);
	}

	/**
	 * @param start
	 * @param size
	 * @param allStatus
	 * @param candidate
	 * @throws Exception
	 */
	public static void outputRecord(int start, int size, List<String> allStatus, String candidate) throws Exception {
		QueryTwitter query = new QueryTwitter();
		System.out.println(start);
		System.out.println(size);
		StringBuilder json = new StringBuilder();
		for(String s: allStatus){
			json.append(s).append("\n");
		}
		// System.err.println(json);
		query.write(candidate + System.currentTimeMillis() + "-" + start + ".json", json.toString(), false);
	}

	/**
	 * @param candidate
	 * @return
	 * @throws Exception
	 */
	public void storeTweetsFromProfile(String candidate) throws Exception {
		int start = 1;
		int size = 400;
		List<String> allStatus = new ArrayList<String>();
		
		while (true) {

			ResponseList<Status> statusBatch = null;
			
			try {
				statusBatch = getTweetsFromProfile(candidate, start, size);
			} catch (Exception e) {
				e.printStackTrace();
				outputRecord(start, size, allStatus, candidate);
				break;
			}

			start++;

			if (statusBatch == null || statusBatch.size() == 0) {
				outputRecord(start, size, allStatus, candidate);
				break;
			}
			for (Status s : statusBatch) {
				allStatus.add(TwitterObjectFactory.getRawJSON(s));
			}

		}

	}
	/*
		HillaryClinton	@
		SenSanders		@
		
		realDonaldTrump
		tedcruz
		marcorubio
		JohnKasich
		RealBenCarson
		
			from:
		gov			
		elect2016 	
		CNNPolitics
		HuffPostPol
		foxnewspolitics
		nytpolitics */
	public static void main(String[] args) throws Exception {
		QueryTwitter query = new QueryTwitter();
		// System.err.println(query.getProfile("HillaryClinton"));

		String candidate = "marcorubio";

		//query.getTweetsUsingSearch("@" + candidate, -1);
		query.getTweetsUsingSearch("@" + candidate, -1);
		//query.storeTweetsFromProfile(candidate);
	}


}