package io.hacktech.uscff;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //String tweet = "It was an honor to be the Grand Marshall- in the Salute to Israel Parade back in 2004";
        String tweet = "Just watched lightweight Marco Rubio lying to a small crowd about my past record. He is not as smart as Cruz, and may be an even bigger liar";
        NlpPipeline pipeline = new NlpPipeline();
        System.out.println("NER");
        System.out.println(pipeline.ner(tweet));
        System.out.println("Sentiment");
        System.out.println(pipeline.sentimentAnalyse(tweet));
    }

}
