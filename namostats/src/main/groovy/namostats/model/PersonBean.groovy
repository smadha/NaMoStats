package namostats.model

import org.apache.solr.client.solrj.beans.Field

/**
 */
public class PersonBean {

    @Field String id
    @Field String sourceid
    @Field String source
    @Field String userid
    @Field String username
    @Field String url
    @Field String content
    @Field long friendscount
    @Field long listedcount
    @Field long favouritescount
    @Field long statusescount
    @Field long followerscount
    @Field String lang
    @Field String fullplacename
    @Field String profileimgurl
    @Field String type
    //@Field List<String> followers

    @Field Date created
    @Field Date updated
    @Field Date indexed

}
