import twitter4j.Query
import twitter4j.QueryResult
import twitter4j.TwitterObjectFactory

tw = ctx.twitterService
tc = tw.twitter
Query q = new Query("#GOPDebate")
q.setCount(100)
q.setSinceId(0)
q.setResultType(Query.ResultType.recent)
//q.setMaxId()
int count = 0
new File("gopdebate-1.txt").withWriter {out ->
    while (true) {

        QueryResult result = tc.search(q)
        def statuses = result.getTweets()
        if (statuses == null || statuses.empty) {
            println("End")
            break
        }
        result.rateLimitStatus
        for (def s in statuses) {
            out.println(TwitterObjectFactory.getRawJSON(s))
            //print(s.user.id + "::" + s.createdAt + " :: " + s.text)
            count++
        }
        println("Count = $count")
        def rLimit = result.rateLimitStatus
        println("Remaining ${rLimit.remaining}, resets in ${rLimit.resetTimeInSeconds} Reset:${rLimit.secondsUntilReset}" )
        if(rLimit.remaining < 1){
            print("going to sleep for ${rLimit.secondsUntilReset}")
            Thread.sleep(1000 * (rLimit.secondsUntilReset + 1))
        }
        q.setMaxId(statuses.get(statuses.size() -1).id)
        q.setCount(100)
        q.setSinceId(0)
        q.setResultType(Query.ResultType.recent)
        Thread.sleep(3000)
    }

}
