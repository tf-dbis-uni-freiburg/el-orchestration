package uni.freiburg.sp.evaluation;

import java.util.HashMap;
import java.util.List;

import uni.freiburg.sp.parser.CustomEntityMention;
import uni.freiburg.sp.parser.Document;

/**
 * Component that contains all the methods for calculating evaluation metrics.
 * 
 * @author Polina Koleva
 *
 */
public class EvaluationMetrics {

	public static double macroPrecision(List<Document> documents, HashMap<String, List<CustomEntityMention>> retrieved) {
		int precisionSum = 0;
		for (int i = 0; i < documents.size(); i++) {
			Document currentDocument = documents.get(i);
			precisionSum += microPrecision(retrieved.get(currentDocument.getId()), currentDocument.getRelevant());
		}
		return (double) precisionSum / documents.size();
	}

	public static double macroRecall(List<Document> documents, HashMap<String, List<CustomEntityMention>> retrieved) {
		int precisionSum = 0;
		for (int i = 0; i < documents.size(); i++) {
			Document currentDocument = documents.get(i);
			precisionSum += microRecall(retrieved.get(currentDocument.getId()), currentDocument.getRelevant());
		}
		return (double) precisionSum / documents.size();
	}

	public static double macroF1Score(List<Document> documents, HashMap<String, List<CustomEntityMention>> retrieved) {
		// 2 times (precision*recall)/precision+recall
		double precision = macroPrecision(documents, retrieved);
		double recall = macroRecall(documents, retrieved);
		return 2 * (precision * recall) / (precision + recall);
	}

	public static double microPrecision(List<CustomEntityMention> retrieved, List<CustomEntityMention> relevant) {
		// relevant intersection retrieved over retrieved
		relevant.retainAll(retrieved);
		return (double) (relevant.size()) / retrieved.size();
	}

	public static double microRecall(List<CustomEntityMention> retrieved, List<CustomEntityMention> relevant) {
		// relevant intersection retrieved over relevant
		relevant.retainAll(retrieved);
		return (double) (relevant.size()) / relevant.size();
	}

	public static double microF1Score(List<CustomEntityMention> retrieved, List<CustomEntityMention> relevant) {
		// 2 times (precision*recall)/precision+recall
		double precision = microPrecision(retrieved, relevant);
		double recall = microRecall(retrieved, relevant);
		return 2 * (precision * recall) / (precision + recall);
	}
}
