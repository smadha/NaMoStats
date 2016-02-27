package namostats

import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.apache.solr.client.solrj.SolrQuery

@Slf4j
class DashboardController {

    def twitterService
    def solrService

    def index() {
    }

    def twitter() {
        def userid = params.userid
        if (userid?.startsWith('@')){
            userid = userid.substring(1)
        }
        println(userid)
        [userid:userid]
    }

    def dateFacets(){
        def userid = params.userid
        if (userid?.startsWith('@')){
            userid = userid.substring(1)
        }
        if(!userid) {
            response.status = 400
            return render([error: "'userid' parameter is required"] as JSON)
        }
        // get the first post
        String qryStr = "userid:$userid AND created:*"
        SolrQuery preQry = new SolrQuery(qryStr)
                .setRows(1)
                .setSort("created", SolrQuery.ORDER.asc)
                .setFields("created", "id", "userid")
        def resp1 = solrService.searchPosts(preQry).results

        def result = []
        if(!resp1.isEmpty()){
            Date startDate = resp1.get(0).get("created") as Date
            SolrQuery  qry = new SolrQuery(qryStr)
                    .setRows(0)
                    .setFacet(true)
                    .setFacetMinCount(0)
                    .addDateRangeFacet("created", startDate, new Date(), "+1DAY")

            result = solrService.searchPosts(qry)
                    .facetRanges.get(0).counts.collect {
                [date:solrService.dtFormat.parse(it.value).time, count:it.count]}

        }
        return render(result as JSON)
    }
    def indexTwitterTimeline(){
        String userid = params.userid
        if (!userid) {
            return render(status:400, text:'Invalid args. userid not found or invalid!')
        }
        if (userid.startsWith("@")) {
            //get rid of @
            userid = userid.substring(1)
        }
        twitterService.indexAllTweets(userid)
        redirect([action:'twitter', params:[userid:userid]])
    }

    def postFacets(){

        def userid = params.userid
        if (!userid || !params.field){
            response.status = 400
            return render([error: 'invalid args, userid and field are required'] as JSON)
        }
        if(userid.startsWith('@')) {
            userid = userid.substring(1)
        }

        SolrQuery  qry = new SolrQuery("userid:" + userid)
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(params.rows?.toInteger()?:50)
                .addFacetField(params.field)

        def result = solrService.searchPosts(qry)
                .facetFields[0].values
                .collect {f -> [name: f.name, value: f.count]}
        result = [children : result]
        render (result as JSON)
    }
}
