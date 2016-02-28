package namostats.model

import org.apache.solr.client.solrj.beans.Field

/**
 */
public class PostBean {

    @Field String id
    @Field String sourceid
    @Field String source
    @Field String userid
    @Field String username
    @Field String url
    @Field String title
    @Field String content
    @Field List<String> connections
    @Field List<String> tags
    @Field List<String> urls
    @Field List<String> urlhosts
    @Field String type
    @Field String lang
    @Field long numlikes
    @Field long numshares
    @Field long numcomments
    @Field boolean composed
    @Field List<String> media
    @Field List<String> symbols
    @Field String inreplytostatusid
    @Field String inreplytouserid
    @Field List<String> location
    @Field String country
    @Field String countrycode
    @Field String place
    @Field String fullplacename
    @Field String boundingboxpt1
    @Field String boundingboxpt2
    @Field String boundingboxpt3
    @Field String boundingboxpt4
    @Field String retweetedfrom
    @Field String category

    @Field Date created
    @Field Date updated
    @Field Date indexed

}
