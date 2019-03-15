package uni.freiburg.sp.pe.processor.ed.mv;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class EDMajorityVotingController extends StandaloneEventProcessingDeclarer<EDMajorityVotingParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create("uni.freiburg.sp-edmv", "Entity Disambiguation Majority Voting",
				"Majority Voting component that merges the output of two ED components. Based on the scores for the candidate, a winner is chosen and outputed.")
				.category(DataProcessorType.AGGREGATE)
				.requiredStream(
						StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
				.requiredStream(
						StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
				.supportedFormats(SupportedFormats.jsonFormat()).supportedProtocols(SupportedProtocols.kafka())
				.outputStrategy(OutputStrategies.keep()).build();
	}

	@Override
	public ConfiguredEventProcessor<EDMajorityVotingParameters> onInvocation(DataProcessorInvocation graph) {
		ProcessingElementParameterExtractor extractor = getExtractor(graph);
		EDMajorityVotingParameters params = new EDMajorityVotingParameters(graph);

		return new ConfiguredEventProcessor<>(params, () -> new EDMajorityVoting(params));
	}

}
