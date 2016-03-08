package edu.usc.cs.ir.nersentiment;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Executor {

	@Option(name="-s",usage="solr start index")
	private Integer start;
	
	@Option(name="-e",usage="solr end index")
	private Integer end;
	
	@Option(name="-o",usage="output file")
	private String outFile;
	
	private String SolrUrl = "http://ec2-54-200-205-158.us-west-2.compute.amazonaws.com:8983/solr/namostats";
	
	public void processArgs(String[] args) throws IOException, ClassNotFoundException {
		CmdLineParser parser = new CmdLineParser(this);
		try {
            // parse the arguments.
            parser.parseArgument(args);

            // you can parse additional arguments if you want.
            // parser.parseArgument("more","args");

        } catch( CmdLineException e ) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java Executor [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            return;
        }
		
		NlpPipeline pipeline = new NlpPipeline();
		// Initialize Solr
		SolrServer solrServer = new HttpSolrServer(this.SolrUrl);
		SolrQuery query = new SolrQuery();
		query.setQuery("type:tweet");
		query.setStart(start);
		query.setRows(end - start);
		boolean insert = false;
		try {
			QueryResponse response = solrServer.query(query);
			SolrDocumentList docs = response.getResults();
			JSONArray jsonArray = new JSONArray();
			
			for(int i = 0; i < docs.size(); i++) {
				SolrDocument doc = docs.get(i);
				insert = false;
				if(doc.containsKey("content") && !doc.get("content").toString().trim().isEmpty()){
					//Map<String, List<String>> ner = pipeline.ner(doc.get("content").toString());
					String sentiment = pipeline.aggregatedSentiment(doc.get("content").toString());
					
					JSONObject object = new JSONObject();
					object.put("id", doc.get("id"));
					/*
					if(ner.containsKey("PERSON")){
						insert = true;
						object.put("ner_persons", ner.get("PERSON"));
					}
					if(ner.containsKey("ORGANIZATION")){
						insert = true;
						object.put("ner_organizations", ner.get("ORGANIZATION"));
					}
					if(ner.containsKey("LOCATION")){
						insert = true;
						object.put("ner_locations", ner.get("LOCATION"));
					}
					*/
					if(sentiment != null && !sentiment.trim().isEmpty()){
						insert = true;
						object.put("sentiment", sentiment);
					}
					
					
					if(insert) {
						jsonArray.add(object);
						System.out.println("" + object);
					}
				}
			}
			if(jsonArray.size() > 0) {
				JSONObject mainObj = new JSONObject();
				mainObj.put("", jsonArray);
				
				try (FileWriter file = new FileWriter(outFile)) {
					file.write(jsonArray.toJSONString());
					//System.out.println("Successfully Copied JSON Object to File...");
					System.out.println("\nJSON Object: " + jsonArray);
				}
			}
			else {
				System.out.println("Sorry, nothing found!");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
	
	
	
	public static void main(String args[]) throws ClassNotFoundException, IOException  {
		Executor executor = new Executor();
		executor.processArgs(args);
	}
}
