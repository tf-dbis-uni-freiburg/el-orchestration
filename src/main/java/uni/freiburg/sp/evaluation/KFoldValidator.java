package uni.freiburg.sp.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import uni.freiburg.sp.parser.CustomEntityMention;
import uni.freiburg.sp.parser.Document;

/**
 * Class that is responsible for k-fold validation process. It splits the data
 * into k equally-sized folds and validates k times the system. For each
 * validation, one subset of the data is used as a test set and the other k-1 as
 * training set.
 * 
 * @author Polina Koleva
 *
 */
public class KFoldValidator {

	private int k;
//	private double microPrecision;
//	private double macroPrecision;
//	private double microRecall;
//	private double macroRecall;
//	private double microF1Score;
//	private double macroF1Score;

	public KFoldValidator(int k) {
		this.k = k;
	}

	/**
	 * Split the documents into k folds.
	 * 
	 * @param documents a corpus of all documents and their mention detections which
	 *                  form the ground truth set.
	 * @return k folds, each contains a particular number of documents
	 */
	public List<List<Document>> splitIntoFolds(List<Document> documents) {
		List<List<Document>> folds = new ArrayList<>();
		int foldSize = documents.size() / k;
		Collections.shuffle(documents);
		for (int index = 0; index < k; index++) {
			List<Document> fold = documents.subList(foldSize * k, foldSize * k + foldSize);
			folds.add(fold);
		}
		return folds;
	}

	public void runValidation(List<Document> documents,
			HashMap<String, List<CustomEntityMention>> firstComponentRetrieved,
			HashMap<String, List<CustomEntityMention>> secondComponentRetrieved) {
		List<List<Document>> folds = splitIntoFolds(documents);
		double c1MacroF1Score = 0.0;
		double c2MacroF1Score = 0.0;
		// for each fold, run evaluation
		for (int i = 0; i < k; i++) {
			// construct test and training set
			List<Document> testSetDocuments = folds.get(i);
			List<Document> trainingSetDocuments = new ArrayList<>();
			for (int j = 0; j < k; j++) {
				if (j != i) {
					trainingSetDocuments.addAll(folds.get(j));
				}
			}
			// calculate micro/macro precision, f1 and recall
			c1MacroF1Score += EvaluationMetrics.macroF1Score(trainingSetDocuments, firstComponentRetrieved);
			c2MacroF1Score += EvaluationMetrics.macroF1Score(trainingSetDocuments, secondComponentRetrieved);
		}
		// average f1 scores over the folds
		c1MacroF1Score /= k;
		c2MacroF1Score /= k;

		// having the weights, evaluate on test set
	}

}
