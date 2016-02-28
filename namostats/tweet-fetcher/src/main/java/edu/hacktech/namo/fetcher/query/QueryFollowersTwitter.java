package edu.hacktech.namo.fetcher.query;

import twitter4j.PagableResponseList;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class QueryFollowersTwitter {

	Twitter twitter = new TwitterFactory(new ConfigurationBuilder().setJSONStoreEnabled(true).build()).getInstance();// TwitterFactory.getSingleton();

	QueryFollowersTwitter() {

		twitter.addRateLimitStatusListener(new RateLimitStatusListener() {

			public void onRateLimitStatus(RateLimitStatusEvent arg0) {
				System.out.println(arg0.getRateLimitStatus());

			}

			public void onRateLimitReached(RateLimitStatusEvent arg0) {
				System.out.println(arg0.getRateLimitStatus());
			}
		});
	}

	public void storeProfileFollower(String tweeple) throws Exception {
		long cursor = -1;
		PagableResponseList<User> followers;
		do {
			followers = twitter.getFollowersList(tweeple, cursor);
			for (User follower : followers) {
				System.out.println(follower.getScreenName() + " has " + follower.getFollowersCount() + " follower(s)");
			}
		} while ((cursor = followers.getNextCursor()) != 0);

	}

	/*
	 * HillaryClinton @ SenSanders
	 * 
	 * realDonaldTrump tedcruz marcorubio JohnKasich RealBenCarson
	 * 
	 * from: gov elect2016 CNNPolitics HuffPostPol foxnewspolitics nytpolitics
	 */
	public static void main(String[] args) throws Exception {
		QueryFollowersTwitter query = new QueryFollowersTwitter();
		String candidate = "SenSanders";

		query.storeProfileFollower(candidate);
	}

}