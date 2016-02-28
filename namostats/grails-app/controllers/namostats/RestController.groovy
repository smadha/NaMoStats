package namostats

import grails.converters.JSON

class RestController {

    def solrService
    def index() {
        render "try different api end point"
    }

    def candidates(){
        if (params.id) {
            def candidate = solrService.getCandidate(params.id.toString())
            if (candidate) {
                render([result:candidate, message:"Candidate found"] as JSON)
            } else {
                response.status = 400
                render([message:"Candidate not found"] as JSON)
            }
        } else {
            def candidates = solrService.getCandidates()
            render([result:candidates, message:"Candidates"] as JSON)
        }
    }

    def temporalstats(){
        Date from = params.from ? new Date(Long.parseLong(params.from.trim())) : new Date(1388534400000)
        Date to = params.to ? new Date(Long.parseLong(params.to.trim())) : new Date()
        def gap = params.gap ?: '1MONTH'
        def res = solrService.getTemporalTrend(from, to, gap, params.mention)
        render ([result:res] as JSON)
    }

    def toptags(){
        render(solrService.getTopTags(params.userid) as JSON)
    }

    def toptweets(){
        def tweets = solrService.topTweets(params.userid, params.tag, params.start, params.rows, params.sortfield)
        render( tweets as JSON)
    }

    def datefacets(){
        try {
            def q = params.q?:"*:*"
            def start
            if (params.start) {
                start = new Date(Long.parseLong(params.start))
            } else {
                def cal = Calendar.getInstance()
                cal.set(2015, 05, 01)
                start = cal.time
            }
            def end = params.end ? new Date(Long.parseLong(params.end)) : new Date()
            def gap = params.gap?: "7DAY"
            def values = solrService.getDateFacet(q, start, end, gap)
            render (values as JSON)
        }catch (Exception e){
            response.status = 400
            render ([msg:e.getMessage()] as JSON)
        }
    }

    public boundingbox(){
        def userid = params.userid
        //FIXME
        def max = params.max?Integer.parseInt(params.max):25000
        def boxes = solrService.getBoundingBox(userid, max)
        render([boxes:boxes, count:boxes.size()] as JSON)
    }

    public sentiments(){
        def sentiments = solrService.getSentiments(params.userid, params.tag)
        render (sentiments as JSON)
    }
}
