package uni.freiburg.sp.pe.processor.id;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class IDGenerationParameters extends EventProcessorBindingParams {

	public IDGenerationParameters(DataProcessorInvocation graph) {
		super(graph);
	}
}
