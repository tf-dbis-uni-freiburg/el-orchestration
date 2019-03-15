
package uni.freiburg.sp.pe.processor.id;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import uni.freiburg.sp.pe.processor.Configurations;

import org.slf4j.Logger;
import java.util.Map;
import java.util.UUID;

public class IDGeneration extends StandaloneEventProcessorEngine<IDGenerationParameters> {

	private static Logger LOG;

	public IDGeneration(IDGenerationParameters params) {
		super(params);
	}

	@Override
	public void onInvocation(IDGenerationParameters parameters, DataProcessorInvocation graph) {

	}

	@Override
	// in - events, out for the next component
	public void onEvent(Map<String, Object> in, String sourceInfo, SpOutputCollector out) {
		String uniqueID = UUID.randomUUID().toString();
		in.put(Configurations.ID_TAG, uniqueID);
		out.onEvent(in);
	}

	@Override
	public void onDetach() {

	}
}
