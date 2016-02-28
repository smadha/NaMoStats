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
        println from
        println to
        def gap = params.gap ?: '1MONTH'
        def res = solrService.getTemporalTrend(from, to, gap)
        render ([result:res] as JSON)
    }


}
