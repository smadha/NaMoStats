package namostats

class ProfileController {

	def twitterService
    def solrService

    def index() {
        println("userID : ${params.userid}")

		[c:solrService.getCandidate(params.userid)]
    }
}
