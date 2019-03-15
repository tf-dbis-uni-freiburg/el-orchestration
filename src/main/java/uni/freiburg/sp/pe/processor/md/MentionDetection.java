
package uni.freiburg.sp.pe.processor.md;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uni.freiburg.sp.evaluation.KFoldValidator;
import uni.freiburg.sp.parser.CustomEntityMention;
import uni.freiburg.sp.parser.Document;
import uni.freiburg.sp.parser.Parser;
import uni.freiburg.sp.pe.processor.Configurations;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MentionDetection extends StandaloneEventProcessorEngine<MentionDetectionParameters> {

	private static Logger LOG;
	private String nlpModel;
	private static final String NLP_NGRAM_MODEL_VALUE = "ngram";
	private static final String NLP_POS_MODEL_VALUE = "pos";
	private static final String NLP_NER_MODEL_VALUE = "ner";

//	private List<String> stopWords;
//	private float minLinkProbability;

	public MentionDetection(MentionDetectionParameters params) {
		super(params);
	}

	@Override
	public void onInvocation(MentionDetectionParameters parameters, DataProcessorInvocation graph) {
		this.nlpModel = parameters.getNlpModel();
		// input parameters
//		this.minLinkProbability = parameters.getMinLinkProbability();
//		this.stopWords = parameters.getStopWords();

		// load data and request mention detections
		List<Document> dbDocument = Parser.loadDBData();
		HashMap<String, List<CustomEntityMention>> firstComponentDBDocuments = requestMentionDetections(NLP_NGRAM_MODEL_VALUE, 0.0, dbDocument);
		HashMap<String, List<CustomEntityMention>> secondComponentDBDocuments = requestMentionDetections(NLP_NER_MODEL_VALUE, 0.0, dbDocument);
		
		KFoldValidator validator = new KFoldValidator(5);
		validator.runValidation(dbDocument, firstComponentDBDocuments, secondComponentDBDocuments);
		
		// List<Document> koreData = uni.freiburg.sp.parser.Parser.loadKoreData();
	}

	@Override
	// in - events, out for the next component
	public void onEvent(Map<String, Object> in, String sourceInfo, SpOutputCollector out) {
		System.out.println("Mention Detection Step!");

		// construct a json object that is given as an input to the mention detection
		// step
		JsonObject requestInputJson = constructJsonRequest(nlpModel, 0.0, in);
		// only xLisa supports mention detection
		JsonObject response = connectToXLisa(requestInputJson);
		in.put(Configurations.MENTIONS_TAG, response.get(Configurations.MENTIONS_TAG).toString());
		out.onEvent(in);
	}

	@Override
	public void onDetach() {

	}

	/**
	 * Connect to XLisa for executing a mention detection step.
	 * 
	 * @param requestInputJson Json that builds the body of the request
	 * @return response as a json object, it contains the input json object
	 */
	private JsonObject connectToXLisa(JsonObject requestInputJson) {
		// only xLisa supports mention detection
		String responseXLisa = null;
		try {
			responseXLisa = Request.Post(Configurations.host + Configurations.xLisaSystem)
					.bodyString(requestInputJson.toString(), ContentType.APPLICATION_JSON).execute().returnContent()
					.asString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonParser parser = new JsonParser();
		JsonObject o = parser.parse(responseXLisa).getAsJsonObject();
		return o;
	}

	private JsonObject constructJsonRequest(String nlpModel, double minLinkProbability, Map<String, Object> inParams) {
		JsonObject requestInputJson = new JsonObject();
		requestInputJson.addProperty(Configurations.TASK_TAG, Configurations.TASK_MD_VALUE);
		JsonObject params = new JsonObject();
		// default "ner"
		params.addProperty(Configurations.NLP_MODEL_TAG, nlpModel);
		// default 0.0
		params.addProperty(Configurations.MIN_LINK_PROBABILITY_TAG, minLinkProbability);
		// TODO add stop words
		requestInputJson.add(Configurations.PARAMS_TAG, params);

		// parse input stream and add its properties to the json file
		// such are text, knowledge-base and language
		JsonObject data = new JsonObject();
		for (Entry<String, Object> entry : inParams.entrySet()) {
			data.addProperty(entry.getKey(), (String) entry.getValue());
		}
		requestInputJson.add(Configurations.DATA_TAG, data);
		return requestInputJson;
	}

	private JsonObject constructJsonRequest(String nlpModel, double minLinkProbability, String text) {
		Map<String, Object> inParams = new HashMap<String, Object>();
		inParams.put(Configurations.TEXT_TAG, text);
		inParams.put(Configurations.LANGUAGE_TAG, Configurations.LANGUAGE_VALUE);
		inParams.put(Configurations.KB_TAG, Configurations.KB_VALUE);
		return constructJsonRequest(nlpModel, minLinkProbability, inParams);
	}

	public HashMap<String, List<CustomEntityMention>> requestMentionDetections(String nlpModel, double minLinkProbability,
			List<Document> documents) {
		HashMap<String, List<CustomEntityMention>> retrievedDocumentMentions = new HashMap<>();
		// take retrieved mentions for each document
		for (Document document : documents) {
			List<CustomEntityMention> retrievedMentions = new ArrayList<>();
			// detects mentions using xLisa
			JsonObject jsonRequest = constructJsonRequest(nlpModel, minLinkProbability, document.getText());
			JsonObject mentionDetections = connectToXLisa(jsonRequest);
			// extract mentions
			JsonArray mentions = (JsonArray) mentionDetections.get(Configurations.MENTIONS_TAG);
			Iterator<JsonElement> mentionsIterator = mentions.iterator();
			while (mentionsIterator.hasNext()) {
				JsonObject next = (JsonObject) mentionsIterator.next();
				int start = next.get(Configurations.START_TAG).getAsInt();
				int length = next.get(Configurations.LENGTH_TAG).getAsInt();
				CustomEntityMention entityMention = new CustomEntityMention();
				entityMention.setBeginIndex(start);
				entityMention.setEndIndex(start + length);
				entityMention.setMention(document.getText().substring(start, start + length));
				retrievedMentions.add(entityMention);
			}
			retrievedDocumentMentions.put(document.getId(), retrievedMentions);
		}
		return retrievedDocumentMentions;
	}
}
