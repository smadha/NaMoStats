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
    @Field String location
    @Field boolean composed

    @Field Date created
    @Field Date updated
    @Field Date indexed

}
