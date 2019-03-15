package uni.freiburg.sp.pe.processor.cg.mv;

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

public class CGMajorityVotingController extends StandaloneEventProcessingDeclarer<CGMajorityVotingParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create("uni.freiburg.sp-cgmv", "Candidate Generation Majority Voting",
				"Majority Voting component after candidate generation has finished. The candidates from both input are unioned.")
				.category(DataProcessorType.AGGREGATE)
				.requiredStream(
						StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
				.requiredStream(
						StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
				.supportedFormats(SupportedFormats.jsonFormat()).supportedProtocols(SupportedProtocols.kafka())
				.outputStrategy(OutputStrategies.keep()).build();
	}

	@Override
	public ConfiguredEventProcessor<CGMajorityVotingParameters> onInvocation(DataProcessorInvocation graph) {
		ProcessingElementParameterExtractor extractor = getExtractor(graph);
		CGMajorityVotingParameters params = new CGMajorityVotingParameters(graph);

		return new ConfiguredEventProcessor<>(params, () -> new CGMajorityVoting(params));
	}

}
