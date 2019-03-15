package uni.freiburg.sp.pe.processor.cg;

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

public class CandidateGenerationController extends StandaloneEventProcessingDeclarer<CandidateGenerationParameters> {

	private static final String SYSTEM = "system";

	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder
				.create("uni.freiburg.sp-cg", "Candidate Generation", "Component that generates candidates for each detected entity.")
				.category(DataProcessorType.AGGREGATE)
				.requiredStream(
						StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
				.supportedFormats(SupportedFormats.jsonFormat())
				.requiredSingleValueSelection(
						Labels.from(SYSTEM, "Select the used system", "System that is used internally."),
						Options.from("X-LiSA", "AGDISTIS", "DoSeR"))
				.supportedProtocols(SupportedProtocols.kafka()).outputStrategy(OutputStrategies.keep()).build();
	}

	@Override
	public ConfiguredEventProcessor<CandidateGenerationParameters> onInvocation(DataProcessorInvocation graph) {
		ProcessingElementParameterExtractor extractor = getExtractor(graph);
		String systemString = extractor.selectedSingleValue(SYSTEM, String.class);
		CandidateGenerationParameters params = new CandidateGenerationParameters(graph, systemString);

		return new ConfiguredEventProcessor<>(params, () -> new CandidateGeneration(params));
	}

}
