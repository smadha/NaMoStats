# NaMoStats
###Analyzing 2016 US Presidential Elections
============
HackTech Team project. Analysing US President nominees 2016

###Inspiration
============
Elections Polls are based on sampling a portion of the voting public which is incomplete NaMoStats was inspired to provide a holistic view to the voting public to know the candidates better

###What it does?
============
Showcase spatial & temporal trends of twitter activity of candidates, their mentions, people's view

###How we built it?
============
We used twitter API to get data. We used open source tools like solr to create and analyse the content Used google maps api and D3 to visualize the analytics. We also created a promptapp command based interface.

###Challenges we ran into
============
Indexing & Enriching our Solr Index was time-consuming despite performing Atomic Updates. We were limited by our AWS nano account

###Accomplishments that we're proud of
============
We were able to visualize temporal & spatial trends in Tweets

###What's next?
============
Grab and parse data from Facebook, news sites to enrich our Solr index Run LDA Topic Modeling on URLs being shared by candidates Compute clustering of left wing & right wing followers based on affinity to Candidates

###Technologies we use
============
- twitter4j
- solr
- groovy
- grails
- Stanford core NLP
- bootstrap
- D3
- Google Maps
- jqCloud

###Contributors
============
- [Thamme Gowda Narayanaswamy](https://github.com/thammegowda)
- [Madhav Sharan](https://github.com/smadha)
- [Karanjeet Singh](https://github.com/karanjeets)
- [Harsha Manjunath](https://github.com/harsham05)
