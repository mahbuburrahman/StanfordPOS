package demo.pos;
import com.aliasi.classify.ConditionalClassification;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;

import com.aliasi.symbol.SymbolTable;

import com.aliasi.tag.TagLattice;
import com.aliasi.tag.ScoredTagging;
import com.aliasi.tag.Tagging;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.RegExTokenizerFactory;

import com.aliasi.util.FastCache;
import com.aliasi.util.ScoredObject;
import com.aliasi.util.Streams;
import com.aliasi.util.Strings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileReader;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class RunBrownPost {

	//static TokenizerFactory TOKENIZER_FACTORY 
    //    = new RegExTokenizerFactory("(-|'|\\d|\\p{L})+|\\S");
    static IndoEuropeanTokenizerFactory  TOKENIZER_FACTORY 
    	= IndoEuropeanTokenizerFactory.INSTANCE;
    public static String dataPath;
    public static String dataPath1;
    


    public static void main(String[] args)
        throws ClassNotFoundException, IOException {
        
            /* Read Path for model and data files */
            try{
                //InputStream input = new FileInputStream("../../../../../config.properties");
                BufferedReader br = new BufferedReader(new FileReader("../../../config.txt"));
                dataPath = br.readLine();
                dataPath1 = br.readLine();
                //System.out.println("Yes"+dataPath);
            }
            catch (Exception e) {
                //System.out.println("No");
            }
            /* End */
    
            
    	String modelLocation = dataPath+"/model/lingpipe/pos-en-general-brown.HiddenMarkovModel";
        System.out.println("Reading model from file=" + modelLocation);
        FileInputStream fileIn = new FileInputStream(modelLocation);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);
        HiddenMarkovModel hmm = (HiddenMarkovModel) objIn.readObject();
        Streams.closeInputStream(objIn);
        FastCache<String, double[]> emissionCache = new FastCache<String, double[]>(10000);
        FastCache<String, double[]> emissionLog2Cache = new FastCache<String, double[]>(10000);
         HmmDecoder decoder = new HmmDecoder(hmm, emissionCache, emissionLog2Cache);

        InputStreamReader isReader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(isReader);
        while (true) {
            System.out.print("\n\nINPUT> ");
            System.out.flush();
            String line = bufReader.readLine();
            if (line == null || line.length() < 1 
                || line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
                break;
            char[] cs = line.toCharArray();

            Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(cs,0,cs.length);
            String[] tokens = tokenizer.tokenize();
            List<String> tokenList = Arrays.asList(tokens);

            firstBest(tokenList,decoder);

            //nBest(tokenList,decoder);

            //confidence(tokenList,decoder);
        }
        Streams.closeReader(bufReader);
    }


    static void firstBest(List<String> tokenList, HmmDecoder decoder) {
        Tagging<String> tagging = decoder.tag(tokenList);
        System.out.println("\nFIRST BEST");
        for (int i = 0; i < tagging.size(); ++i)
            System.out.print(tagging.token(i) + "_" + tagging.tag(i) + " ");
        System.out.println();

    }

    static final int MAX_N_BEST = 5;

    static void nBest(List<String> tokenList, HmmDecoder decoder) {
        System.out.println("\nN BEST");
        System.out.println("#   JointLogProb         Analysis");
        Iterator<ScoredTagging<String>> nBestIt = decoder.tagNBest(tokenList,MAX_N_BEST);
        for (int n = 0; n < MAX_N_BEST && nBestIt.hasNext(); ++n) {
            ScoredTagging<String> scoredTagging = nBestIt.next();
            double score = scoredTagging.score();
            System.out.print(n + "   " + format(score) + "  ");
            for (int i = 0; i < tokenList.size(); ++i)
                System.out.print(scoredTagging.token(i) + "_" + pad(scoredTagging.tag(i),5));
            System.out.println();
        }        
    }

    static void confidence(List<String> tokenList, HmmDecoder decoder) {
        System.out.println("\nCONFIDENCE");
	System.out.println("#   Token          (Prob:Tag)*");
        TagLattice<String> lattice = decoder.tagMarginal(tokenList);
        for (int tokenIndex = 0; tokenIndex < tokenList.size(); ++tokenIndex) {
            ConditionalClassification tagScores = lattice.tokenClassification(tokenIndex);
	    System.out.print(pad(Integer.toString(tokenIndex),4));
	    System.out.print(pad(tokenList.get(tokenIndex),15));
            for (int i = 0; i < 5; ++i) {
		double conditionalProb = tagScores.score(i);
		String tag = tagScores.category(i);
                System.out.print(" " + format(conditionalProb) 
				 + ":" + pad(tag,4));
	    }
	    System.out.println();
	}
    }

    static String format(double x) {
	return String.format("%9.3f",x);
    }

    static String pad(String in, int length) {
	if (in.length() > length) return in.substring(0,length-3) + "...";
	if (in.length() == length) return in;
	StringBuilder sb = new StringBuilder(length);
	sb.append(in);
	while (sb.length() < length) sb.append(' ');
	return sb.toString();
	
    }
}
