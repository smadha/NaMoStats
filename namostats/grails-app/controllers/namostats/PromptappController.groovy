package namostats

import grails.converters.JSON

class PromptappController {

    def solrService
    def index() {}

    def message(){
        def reqMessage = request.JSON
        def message = reqMessage["message"]
        String[] candidates = ['Ben Carson(RealBenCarson)', 'Bernie Sanders(SenSanders)', 'Donald Trump(realDonaldTrump)', 'Hillary Clinton(HillaryClinton)', 'John Kasich(JohnKasich)', 'Marco Rubio(marcorubio)', 'Ted Cruz(tedcruz)']
        def resp = [sendmms: false,
                    showauthurl: false,
                    authstate: null,
                    text: "",
                    speech: "",
                    status: "OK",
                    webhookreply: null,
                    ]

        println "Got a request"
        println (message)

        // Show Menu to the user
        if(message == null || message.toString().length() > 1 || !message.isNumber() || Integer.parseInt(message) > candidates.length || Integer.parseInt(message) < 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("Hello,\nSelect the candidate\n")
            for(int i = 0; i < candidates.length; i++)
                builder.append(i + 1 + ". " + candidates[i] + "\n")
            resp["text"] = builder.toString()
            resp["speech"] = builder.toString()
        }
        else {
            String candidate = candidates[Integer.parseInt(message) - 1]
            def topTags = solrService.getTopTags(candidate.substring(candidate.indexOf("(")+1,candidate.indexOf(")")))
            int i = 1
            StringBuilder builder = new StringBuilder();
            for(def t in topTags) {
                builder.append(t.name)
                i++
                if(i >= 5)
                    break
                builder.append(", ")
            }
            resp["text"] = "Trending Hash Tags for candidate '" + candidates[Integer.parseInt(message) - 1] + "': " + builder.toString()
            resp["speech"] = "Trending Hash Tags for candidate '" + candidates[Integer.parseInt(message) - 1] + "': " + builder.toString()
        }

        render (resp as JSON)
    }

}
