package namostats

import grails.transaction.Transactional
import namostats.model.PostBean
import twitter4j.*
import twitter4j.conf.ConfigurationBuilder

@Transactional
class TwitterService {

    def solrService

    Twitter twitter = new TwitterFactory(
            new ConfigurationBuilder().setJSONStoreEnabled(true).build())
            .getInstance()

    /**
     * Gets user profile from twitter
     * @param username : twitter username
     * @return an instance of {@link Twitter}
     */
    def getUser(String username){
        return twitter.showUser(username)
    }


    def PostBean transform(Status s) {
        def dict = [
                id: "twitter::${s.id}",
                sourceid: "${s.id}",
                source: 'twitter.com',
                content : s.text,
                userid : s.user.screenName,
                username : s.user.name,
                lang : s.lang,
                type : 'tweet',
                created : s.createdAt,
                indexed : new Date(),
                numlikes : s.favoriteCount,
                numshares : s.retweetCount,
                tags : s.hashtagEntities?.collect {t -> t.text},
                connections : s.userMentionEntities?.collect {m -> m.screenName},
                urls : s.URLEntities?.collect() {u -> u.expandedURL}?.findAll {u -> !u.isEmpty()},
                composed : !s.retweeted]
        if (dict.urls) {
            dict['urlhosts'] = ((dict.urls.collect {new URL(it).host} as HashSet) as ArrayList)
        }
        return new PostBean(dict)
    }
    /**
     * gets timeline of user
     * @param username
     * @return
     */
    def Collection<PostBean> getTimeline(String username) {
        return twitter.getUserTimeline(username, new Paging(1, 200)).collect {
            s -> return transform(s)
        }
    }

    private def checkRateLimit(boolean canWait) {
        //not implemented yet
    }


    public int saveAllTweets(String username, String fileName){
        def paging = new Paging(1, 200)
        int count = 0
        new File(fileName).withWriter {out ->
            while (true) {
                log.info("Got $count tweets of $username, next page=$paging ")
                checkRateLimit(true)
                def statuses = twitter.getUserTimeline(username, paging)
                if (statuses == null || statuses.empty) {
                    log.info("End")
                    break
                }
                statuses.collect{Status s ->
                    out.println(TwitterObjectFactory.getRawJSON(s))
                    count++
                }
                paging.page = paging.page + 1
                println(count)
            }
        }
        return count
    }

    public int getAllTweetsFromHashTag(String hashTag, String fileName){
        int count = 0
        def Query query = new Query("#" + hashTag)
        long lastId = Long.MAX_VALUE
        int maxTweets = 1000

        new File(fileName).withWriter {out ->
            while (true) {
                log.info("Got $count tweets of $hashTag ")
                checkRateLimit(true)
                query.setCount(100)
                def QueryResult statuses = twitter.search(query)
                if (statuses == null || statuses.getTweets().size() == 0 || count > maxTweets) {
                    log.info("End")
                    break
                }
                statuses.getTweets().collect{Status s ->
                    out.println(TwitterObjectFactory.getRawJSON(s))
                    if(s.getId() < lastId) lastId = s.getId();
                    count++
                }
                query.setMaxId(lastId - 1)
                println(count)
            }
        }

        return count
    }

    def synchronized indexAllTweets(String username){
        def paging = new Paging(1, 200)
        int count = 0
        while(true) {
            log.info("Got $count tweets of $username, next page=$paging ")
            checkRateLimit(true)
            def statuses = twitter.getUserTimeline(username, paging)
            if(statuses == null || statuses.empty) {
                log.info("End")
                break
            }
            List<PostBean> beans =  statuses.collect{ Status s -> transform(s) }
            count += beans.size()
            solrService.index(beans)
            paging.page = paging.page + 1
        }
        return count
    }
}
