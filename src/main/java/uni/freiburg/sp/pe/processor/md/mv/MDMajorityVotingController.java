package uni.freiburg.sp.pe.processor.md.mv;

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

public class MDMajorityVotingController extends StandaloneEventProcessingDeclarer<MDMajorityVotingParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder
				.create("uni.freiburg.sp-mdmv", "Mention Detection Majority Voting",
						"Majority voting component which works over the output of mention detection step."
								+ "The longest entity wins.")
				.category(DataProcessorType.AGGREGATE)
				.requiredStream(
						StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
				.requiredStream(
						StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
				.supportedFormats(SupportedFormats.jsonFormat()).supportedProtocols(SupportedProtocols.kafka())
				.outputStrategy(OutputStrategies.keep()).build();
	}

	@Override
	public ConfiguredEventProcessor<MDMajorityVotingParameters> onInvocation(DataProcessorInvocation graph) {
		ProcessingElementParameterExtractor extractor = getExtractor(graph);
		MDMajorityVotingParameters params = new MDMajorityVotingParameters(graph);

		return new ConfiguredEventProcessor<>(params, () -> new MDMajorityVoting(params));
	}

}
