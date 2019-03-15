package uni.freiburg.sp.pe.processor.cg;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class CandidateGenerationParameters extends EventProcessorBindingParams {

	private String system;
	// xLisa parameters
	private String textProcessor;
	private float minSenseProbabaility;

	// AGDISTIS parameters
	private int nGramSize;
	private float nGramTreshold;
	private boolean doHeuristicExpansion;
	private boolean searchByAcronym;
	private boolean includeCommonEntities;

	// DoSeR parameters

	public CandidateGenerationParameters(DataProcessorInvocation graph, String system) {
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
