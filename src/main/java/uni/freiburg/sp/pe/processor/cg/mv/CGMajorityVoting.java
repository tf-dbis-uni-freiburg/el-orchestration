
package uni.freiburg.sp.pe.processor.cg.mv;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uni.freiburg.sp.pe.processor.Configurations;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CGMajorityVoting extends StandaloneEventProcessorEngine<CGMajorityVotingParameters> {

	private static Logger LOG;

	// uuid of an event, event
	HashMap<String, Map<String, Object>> events = new HashMap<>();

	public CGMajorityVoting(CGMajorityVotingParameters params) {
		super(params);
	}

	@Override
	public void onInvocation(CGMajorityVotingParameters parameters, DataProcessorInvocation graph) {

	}

	@Override
	// in - events, out for the next component
	public void onEvent(Map<String, Object> in, String sourceInfo, SpOutputCollector out) {
		// extract uuid from the input
		String uuid = (String) in.get(Configurations.ID_TAG);
		// such event already arrived and was cached. Compare both events and forward
		// only one of it
		if (this.events.containsKey(uuid)) {
			JsonArray finalMentions = new JsonArray();

			Map<String, Object> previousEvent = this.events.get(uuid);
			JsonParser parser = new JsonParser();
			JsonArray previousEventMentions = parser.parse((String) previousEvent.get(Configurations.MENTIONS_TAG))
					.getAsJsonArray();
			JsonArray currentEventMentions = parser.parse((String) in.get(Configurations.MENTIONS_TAG))
					.getAsJsonArray();
			Iterator<JsonElement> previousIterator = previousEventMentions.iterator();
			Iterator<JsonElement> currentIterator = currentEventMentions.iterator();
			while (previousIterator.hasNext() && currentIterator.hasNext()) {
				JsonObject previousObj = previousIterator.next().getAsJsonObject();
				JsonArray prevCandidates = previousObj.get("candidates").getAsJsonArray();
				JsonObject currentObj = currentIterator.next().getAsJsonObject();
				JsonArray currentCandidates = currentObj.get("candidates").getAsJsonArray();
				for (int i = 0; i < currentCandidates.size(); i++) {
					if(!prevCandidates.contains(currentCandidates.get(i))) {
						prevCandidates.add(currentCandidates.get(i));
					}
				}
				finalMentions.add(previousObj);
			}

			this.events.remove(uuid);
			in.put(Configurations.MENTIONS_TAG, finalMentions.toString());
			out.onEvent(in);
			// cache the input
		} else {
			this.events.put(uuid, in);
		}
	}

	@Override
	public void onDetach() {

	}
}
