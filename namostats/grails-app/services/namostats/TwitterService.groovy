package namostats

import grails.transaction.Transactional
import io.hacktech.uscff.NlpPipeline
import namostats.model.PersonBean
import namostats.model.PostBean
import twitter4j.*
import twitter4j.conf.ConfigurationBuilder

@Transactional
class TwitterService {

    def solrService
    NlpPipeline pipeline = new NlpPipeline()

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
        def candidates = ['realdonaldtrump', 'tedcruz', 'marcorubio', 'johnkasich', 'realbencarson']
        def news = ['cnnpolitics', 'huffpostpol', 'foxnewspolitics', 'nytpolitics']
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
                media : s.mediaEntities?.collect() {m -> m.mediaURL}?.findAll {m -> !m.isEmpty()},
                symbols : s.symbolEntities?.collect() {se -> se.text}?.findAll() {se -> !se.isEmpty()},
                composed : !s.retweeted,
                inreplytostatusid : s.inReplyToStatusId,
                inreplytouserid : s.inReplyToUserId,
                retweetedfrom : s.retweetedStatus?.id
                ]
        if(s.place != null) {
            dict["location"] = s.place.geometryCoordinates
            dict["country"] = s.place.country
            dict["countrycode"] = s.place.countryCode
            dict["place"] = s.place.name
            dict["fullplacename"] = s.place.fullName

            dict["boundingboxpt1"] = "" + s.place.boundingBoxCoordinates[0][0].latitude + ", " +  s.place.boundingBoxCoordinates[0][0].longitude
            dict["boundingboxpt2"] = "" + s.place.boundingBoxCoordinates[0][1].latitude + ", " +  s.place.boundingBoxCoordinates[0][1].longitude
            dict["boundingboxpt3"] = "" + s.place.boundingBoxCoordinates[0][2].latitude + ", " +  s.place.boundingBoxCoordinates[0][2].longitude
            dict["boundingboxpt4"] = "" + s.place.boundingBoxCoordinates[0][3].latitude + ", " +  s.place.boundingBoxCoordinates[0][3].longitude

        }
        if (dict.urls) {
            dict['urlhosts'] = ((dict.urls.collect {new URL(it).host} as HashSet) as ArrayList)
            dict['media'] = ((dict.media.collect {new URL(it).host} as HashSet) as ArrayList)
        }
        if(candidates.contains(s.user.screenName.toLowerCase()))
            dict["category"] = "candidate"
        else if(news.contains(s.user.screenName.toLowerCase()))
            dict["category"] = "news"
        else
            dict["category"] = "public"


        if(s.text != null && !s.text.isEmpty()) {
            Map<String, List<String>> ner = pipeline.ner(s.text)
            if (ner.hasProperty("PERSON"))
                dict["ner_persons"] = ner.get("PERSON")
            if (ner.hasProperty("ORGANIZATION"))
                dict["ner_organizations"] = ner.get("ORGANIZATION")
            if (ner.hasProperty("LOCATION"))
                dict["ner_locations"] = ner.get("LOCATION")

            String sentiment = pipeline.aggregatedSentiment(s.text)
            dict["sentiment"] = (sentiment != null && !sentiment.isEmpty()) ? sentiment : null
        }

        return new PostBean(dict)
    }

    def PersonBean transformPerson(Status s) {
        def dict = [
                id: "twitter::${s.user.id}",
                sourceid: "${s.user.id}",
                source: 'twitter.com',
                content : s.user.description,
                userid : s.user.screenName,
                username : s.user.name,
                lang : s.user.lang,
                type : 'profile',
                created : s.user.createdAt,
                indexed : new Date(),
                url : s.user.URL,
                friendscount : s.user.friendsCount,
                listedcount : s.user.listedCount,
                favouritescount : s.user.favouritesCount,
                statusescount : s.user.statusesCount,
                followerscount : s.user.followersCount,
                fullplacename : s.user.location,
                profileimgurl : s.user.biggerProfileImageURL
        ]

        return new PersonBean(dict)
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
        int maxTweets = 100

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

    def synchronized indexAllTweetsFromJSON(String filename){
        BufferedReader reader = null
        try {
            println("Reading File")
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))
            String line = null
            int count = 0
            List<PostBean> beans = new ArrayList<PostBean>()
            List<PersonBean> personBeans = new ArrayList<PersonBean>()

            println("File Read")
            // Reading JSON line by line
            while((line = reader.readLine()) != null) {
                // Converting JSON to Twitter4j Status
                Status status = TwitterObjectFactory.createObject(line)
                PostBean bean = transform(status)
                PersonBean personBean = transformPerson(status)
                beans.add(bean)
                personBeans.add(personBean)
                //println(status.getId())
                //break;
                count++
                if(count % 1000 == 0) {
                    println("Adding to Solr...")
                    solrService.index(beans)
                    solrService.index(personBeans)
                    beans.removeAll()
                    personBeans.removeAll()
                    //beans = new ArrayList<PostBean>()
                    println("Completed " + count)
                }
            }
            if(count % 1000 != 0) {
                solrService.index(beans)
                println("Completed " + count)
            }

        }
        catch (Error e) {
            e.printStackTrace()
            throw  e;
        }
        finally {
            reader.close()
        }
    }
}
