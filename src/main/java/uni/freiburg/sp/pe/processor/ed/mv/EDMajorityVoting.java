
package uni.freiburg.sp.pe.processor.ed.mv;

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

public class EDMajorityVoting extends StandaloneEventProcessorEngine<EDMajorityVotingParameters> {

	private static Logger LOG;

	// uuid of an event, event
	HashMap<String, Map<String, Object>> events = new HashMap<>();

	public EDMajorityVoting(EDMajorityVotingParameters params) {
		super(params);
	}

	@Override
	public void onInvocation(EDMajorityVotingParameters parameters, DataProcessorInvocation graph) {

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
				// select winner from the previously received input
				JsonObject previousObj = previousIterator.next().getAsJsonObject();
				JsonArray prevCandidates = previousObj.get(Configurations.CANDIDATES_TAG).getAsJsonArray();
				JsonArray prevScores = previousObj.get(Configurations.SCORES_TAG).getAsJsonArray();
				// take biggest score
				float prevMaxScore = Float.MIN_VALUE;
				int prevMaxIndex = -1;
				for (int i = 0; i < prevScores.size(); i++) {
					float score = prevScores.get(i).getAsFloat();
					if(score > prevMaxScore) {
						prevMaxScore = score;
						prevMaxIndex = i;
					}
				}
				// select winner from the current received input
				JsonObject currentObj = currentIterator.next().getAsJsonObject();
				JsonArray currentCandidates = currentObj.get(Configurations.CANDIDATES_TAG).getAsJsonArray();
				JsonArray currentScores = previousObj.get(Configurations.SCORES_TAG).getAsJsonArray();
				// take biggest score
				float currMaxScore = Float.MIN_VALUE;
				int currMaxIndex = -1;
				for (int i = 0; i < currentScores.size(); i++) {
					float score = currentScores.get(i).getAsFloat();
					if(score > currMaxScore) {
						currMaxScore = score;
						currMaxIndex = i;
					}
				}
				if(prevMaxScore >= currMaxScore) {
					previousObj.remove(Configurations.CANDIDATES_TAG);
					previousObj.remove(Configurations.SCORES_TAG);
					previousObj.add("winner", prevCandidates.get(prevMaxIndex));
					previousObj.addProperty("score", prevMaxScore);
					finalMentions.add(previousObj);
				} else {
					currentObj.remove(Configurations.CANDIDATES_TAG);
					currentObj.remove(Configurations.SCORES_TAG);
					currentObj.add("winner", currentCandidates.get(currMaxIndex));
					currentObj.addProperty("score", currMaxScore);
					finalMentions.add(currentObj);
				}
			}
			// remove from cache
			this.events.remove(uuid);
			in.put(Configurations.MENTIONS_TAG, finalMentions.toString());
			//remove the internal id
			in.remove(Configurations.ID_TAG);
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
