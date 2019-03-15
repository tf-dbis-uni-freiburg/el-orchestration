package uni.freiburg.sp.pe.processor.ed;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class EntityDisambiguationParameters extends EventProcessorBindingParams {

	private String system;

	public EntityDisambiguationParameters(DataProcessorInvocation graph, String system) {
		super(graph);
		this.setSystem(system);
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

}
