package uni.freiburg.sp.pe.processor.md.mv;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class MDMajorityVotingParameters extends EventProcessorBindingParams {

	public MDMajorityVotingParameters(DataProcessorInvocation graph) {
		super(graph);
	}
}
