package namostats

import grails.converters.JSON

class PromptappController {

    def index() {}

    def message(){
        def message = request.JSON
        println "Got a request"
        println (message)

        def resp = [
            sendmms: true,
            showauthurl: false,
            authstate: null,
            text: "Hello World! You said 'Hello World!'.",
            speech: "Hello World! You said 'Hello World!'.",
            status: "OK",
            webhookreply: null,
            images: [[
                        imageurl: "http://api.dev.promptapp.io/images/random/helloworld.gif",
                        alttext: "Hello World!"
                    ]
            ]
        ]
        render (resp as JSON)
    }

}
