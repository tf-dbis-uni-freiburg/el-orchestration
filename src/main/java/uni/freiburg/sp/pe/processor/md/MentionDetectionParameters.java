package uni.freiburg.sp.pe.processor.md;

import java.util.List;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class MentionDetectionParameters extends EventProcessorBindingParams {

	private String nlpModel;
//	private List<String> stopWords;
//	private float minLinkProbability;

	public MentionDetectionParameters(DataProcessorInvocation graph, String nlpModel/*, List<String> stopWords, float minLinkProbabality*/) {
		super(graph);
		this.setNlpModel(nlpModel);
//		this.setStopWords(stopWords);
//		this.setMinLinkProbability(minLinkProbabality);
	}

	public String getNlpModel() {
		return nlpModel;
	}

	public void setNlpModel(String nlpModel) {
		this.nlpModel = nlpModel;
	}
//
//	public List<String> getStopWords() {
//		return stopWords;
//	}
//
//	public void setStopWords(List<String> stopWords) {
//		this.stopWords = stopWords;
//	}
//
//	public float getMinLinkProbability() {
//		return minLinkProbability;
//	}
//
//	public void setMinLinkProbability(float minLinkProbability) {
//		this.minLinkProbability = minLinkProbability;
//	}
}
