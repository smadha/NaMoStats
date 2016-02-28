package namostats

import grails.transaction.Transactional
import groovy.util.logging.Log
import namostats.model.PersonBean
import namostats.model.PostBean
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrResponse
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.impl.HttpSolrServer
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.util.ClientUtils

import javax.annotation.PostConstruct
import java.text.DateFormat
import java.text.SimpleDateFormat

@Log
@Transactional
class SolrService implements Closeable {

    def static DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    def grailsApplication
    SolrServer postsServer
    SolrServer usersServer
    boolean healthy

    @PostConstruct
    def init(){
        log.info("Initializing solr.")
        def config = grailsApplication.config
        String postsCoreUrl = config?.solr?.postsUrl
        String usersCoreUrl = config?.solr?.usersUrl
        println("Posts $postsCoreUrl")
        println("user $usersCoreUrl")
        this.postsServer = new HttpSolrServer(postsCoreUrl)
        this.usersServer = new HttpSolrServer(usersCoreUrl)
        log.info("Solr config is okay")
        this.healthy = true
    }

    public void index(Collection<PostBean> posts){
        postsServer.addBeans(posts)
        postsServer.commit(false, false, true)
    }

    public void commitPosts(){
        postsServer.commit()
    }

    public searchPosts(SolrQuery query) {
        return postsServer.query(query)
    }

    public getCandidates(){
        //FIXME: get real candidates
        SolrQuery qry = new SolrQuery("type:profile")
                .setRows(10)
                .setSort("followerscount", SolrQuery.ORDER.desc)
        QueryResponse res = postsServer.query(qry)
        return res.getBeans(PersonBean.class)
    }

    public getCandidate(String candidateId){
        log.info("Get Candidate $candidateId")
        def qry = new SolrQuery("id:${ClientUtils.escapeQueryChars(candidateId)}")
        QueryResponse res = postsServer.query(qry)
        return  res.getResults().numFound >= 1 ? res.getBeans(PersonBean.class)[0] : null
    }

    def getTemporalTrend(Date from, Date to, String gap){
        def qry = new SolrQuery("*:*")
        qry.addDateRangeFacet('created', from, to, "+"+gap)
        def resp = postsServer.query(qry)
        return resp.getFacetRanges().get(0).counts.collect{ it ->
            [value:it.value, count:it.count]
        }
    }

    @Override
    void close() throws IOException {
        if(postsServer) {
            log.info("shutting down posts server")
            commitPosts()
            postsServer.shutdown()
        }
        if (usersServer) {
            log.info("shutting down users server")
            usersServer.commit()
            usersServer.shutdown()
        }
        this.healthy = false
    }
}
