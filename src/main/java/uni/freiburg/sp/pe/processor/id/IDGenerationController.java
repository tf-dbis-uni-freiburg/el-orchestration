package uni.freiburg.sp.pe.processor.id;

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

public class IDGenerationController extends StandaloneEventProcessingDeclarer<IDGenerationParameters> {


	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder
				.create("uni.freiburg.sp-id", "ID Generation", "Component that attaches id to each event in the input stream.")
				.category(DataProcessorType.ENRICH)
				.requiredStream(
						StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
				.supportedFormats(SupportedFormats.jsonFormat())
				.supportedProtocols(SupportedProtocols.kafka()).outputStrategy(OutputStrategies.keep()).build();
	}

	@Override
	public ConfiguredEventProcessor<IDGenerationParameters> onInvocation(DataProcessorInvocation graph) {
		ProcessingElementParameterExtractor extractor = getExtractor(graph);
		IDGenerationParameters params = new IDGenerationParameters(graph);

		return new ConfiguredEventProcessor<>(params, () -> new IDGeneration(params));
	}

}
