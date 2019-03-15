package uni.freiburg.sp.pe.processor.md;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class MentionDetectionController extends StandaloneEventProcessingDeclarer<MentionDetectionParameters> {
	
	private static final String NLP_MODEL = "nlp_model";
	private static final String STOP_WORDS = "stop_words";
	private static final String MIN_LINK_PROBABILITY = "min_link_probability";

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder
				.create("uni.freiburg.sp-md", "Mention Detection", "Component that detects entities.")
				.category(DataProcessorType.TRANSFORM).requiredStream(StreamRequirementsBuilder.create()
						// TODO specify properties of the input stream
						.requiredProperty(EpRequirements.anyProperty()).build())
				.supportedFormats(SupportedFormats.jsonFormat()).supportedProtocols(SupportedProtocols.kafka())
				.requiredSingleValueSelection(Labels.from(NLP_MODEL, "Select NLP Model", "The used NLP model."),
						Options.from("ngram", "pos", "ner"))
//				.requiredFloatParameter(Labels.from(MIN_LINK_PROBABILITY, "Select minimum link probability", "Minimum fraction of occurences of a surface form in Wikipedia which links to an article. Between 0 and 1."), "min_link_probablity")
//				.requiredTextParameter(Labels.from(STOP_WORDS, "Select stop words divided by ,", "Words to be ignored."), "stop_words")
				.outputStrategy(OutputStrategies.keep()).build();
	}

	@Override
	public ConfiguredEventProcessor<MentionDetectionParameters> onInvocation(DataProcessorInvocation graph) {
		ProcessingElementParameterExtractor extractor = getExtractor(graph);

		String nlpModel = extractor.selectedSingleValue(NLP_MODEL, String.class);
//		String stopWords = extractor.singleValueParameter(STOP_WORDS, String.class);
//		String[] stopWordsList = stopWords.split(",");
//		float minLinkProbabality = extractor.singleValueParameter(MIN_LINK_PROBABILITY, Float.class);
		
		MentionDetectionParameters params = new MentionDetectionParameters(graph, nlpModel /*,, new ArrayList<>(Arrays.asList(stopWordsList)), minLinkProbabality */);

		return new ConfiguredEventProcessor<>(params, () -> new MentionDetection(params));
	}

}
