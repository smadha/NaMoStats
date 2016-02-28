package io.hacktech.uscff;

import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by tg on 2/27/16.
 *
 * @author  Thamme Gowda N.
 */
public class NlpPipeline {

    public static final String POS = "Positive";
    public static final String VERY_POS = "Very Positive";
    public static final String NEG = "Negative";
    public static final String VERY_NEG = "Very Negative";
    public static final String NEU = "Neutral";
    public static final String MIX = "Mixed";
    private StanfordCoreNLP tokenizer;
    private StanfordCoreNLP sentimentAnalyser;
    private CRFClassifier<CoreLabel> nerClassifier;

    public NlpPipeline() throws IOException, ClassNotFoundException {
        Properties pipelineProps = new Properties();
        pipelineProps.setProperty("annotators", "parse, sentiment");
        pipelineProps.setProperty("enforceRequirements", "false");
        Properties tokenizerProps = new Properties();
        tokenizerProps.setProperty("annotators", "tokenize, ssplit");

        tokenizer = new StanfordCoreNLP(tokenizerProps);
        sentimentAnalyser = new StanfordCoreNLP(pipelineProps);
        String modelPath = "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz";
        nerClassifier = CRFClassifier.getClassifier(modelPath);
    }

    public Map<String, List<String>> ner(String text) throws IOException, ClassNotFoundException {
        List<Triple<String, Integer, Integer>> nes = nerClassifier.classifyToCharacterOffsets(text);
        Map<String, List<String>> result = new HashMap<>();
        for (Triple<String, Integer, Integer> ne : nes) {
            if (!result.containsKey(ne.first)) {
                result.put(ne.first, new ArrayList<>());
            }
            result.get(ne.first)
                    .add(text.substring(ne.second, ne.third));
        }
        return result;
    }

    //copied from SentimentPipeline
    public List<String> sentimentAnalyse(String text){
        Annotation annotation = new Annotation(text);
        tokenizer.annotate(annotation);
        List<Annotation> annotations = Generics.newArrayList();
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Annotation nextAnnotation = new Annotation(sentence.get(CoreAnnotations.TextAnnotation.class));
            nextAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
            annotations.add(nextAnnotation);
        }

        List<String> sentiClasses = new ArrayList<>();
        for (Annotation ann : annotations) {
            sentimentAnalyser.annotate(ann);
            for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
                String sentiClass = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
                sentiClasses.add(sentiClass);
            }
        }
        return sentiClasses;
    }

    public String aggregatedSentiment(String text){
        return aggregateSentiment(sentimentAnalyse(text));
    }

    public String aggregateSentiment(List<String> classes){
        System.out.println(classes);
        Map<String, Integer> counts = new TreeSortedMap<>();
        for (String field: classes) {
            counts.put(field, counts.getOrDefault(field, 0) + 1);
        }
        //if only one class ; then it is it
        if (counts.size() == 1) {
            return counts.keySet().iterator().next();
        }
        // Mostly mixed
        // lets average
        int count = 0;
        for (String field : classes) {
            switch (field){
                case VERY_POS:
                    count += 2;
                    break;
                case POS:
                    count += 1;
                    break;
                case NEU:
                    //nothing +0
                    break;
                case NEG:
                    count -= 1;
                    break;
                case VERY_NEG:
                    count -= 2;
                    break;
                default:
                    throw new RuntimeException( field + " -- WTH ?");
            }
        }
        double avg = 1.0 * count / counts.size();
        if (avg < -1.000) {
            return VERY_NEG;
        } else if (avg <= -0.50){
            return NEG;
        } else if (avg < 0.50) {
            return MIX;
        } else if (avg <= 1.000){
            return POS;
        } else {
            return VERY_POS;
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //String tweet = "It was an honor to be the Grand Marshall- in the Salute to Israel Parade back in 2004";
        String tweet = "This is fucking irritating. But I see some hope in it.";
        NlpPipeline pipeline = new NlpPipeline();
        System.out.println("NER");
        System.out.println(pipeline.ner(tweet));
        System.out.println("Sentiment");
        System.out.println(pipeline.aggregatedSentiment(tweet));
    }

}
