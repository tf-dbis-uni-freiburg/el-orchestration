
package uni.freiburg.sp.pe.processor.md.mv;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uni.freiburg.sp.pe.processor.Configurations;

import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class MDMajorityVoting extends StandaloneEventProcessorEngine<MDMajorityVotingParameters> {

	private static Logger LOG;

	// uuid of an event, event
	HashMap<String, Map<String, Object>> events = new HashMap<>();

	public MDMajorityVoting(MDMajorityVotingParameters params) {
		super(params);
	}

	@Override
	public void onInvocation(MDMajorityVotingParameters parameters, DataProcessorInvocation graph) {

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
			Multimap<Integer, JsonObject> mentionStarts = ArrayListMultimap.create();
			Multimap<Integer, JsonObject> mentionLengths = ArrayListMultimap.create();
			for (JsonElement mention : previousEventMentions) {
				JsonObject mentionObj = mention.getAsJsonObject();
				Integer start = mentionObj.get("start").getAsInt();
				Integer length = mentionObj.get("length").getAsInt();
				// drop duplicates
				if(!mentionStarts.containsEntry(start, mentionObj)) {
					mentionStarts.put(start, mentionObj);
				}
				if(!mentionLengths.containsEntry(length, mention)) {
					mentionLengths.put(length, mentionObj);
				}	
			}
			JsonArray currentEventMentions = parser.parse((String) in.get(Configurations.MENTIONS_TAG))
					.getAsJsonArray();
			for (JsonElement mention : currentEventMentions) {
				JsonObject mentionObj = mention.getAsJsonObject();
				Integer start = mentionObj.get("start").getAsInt();
				Integer length = mentionObj.get("length").getAsInt();
				// drop duplicates
				if(!mentionStarts.containsEntry(start, mentionObj)) {
					mentionStarts.put(start, mentionObj);
				}
				if(!mentionLengths.containsEntry(length, mention)) {
					mentionLengths.put(length, mentionObj);
				}	
			}
			// sort lengths and take the biggest
			SortedSet<Integer> lengthKeys = new TreeSet<>(Collections.reverseOrder());
			lengthKeys.addAll(mentionLengths.keySet());
			HashSet<JsonObject> seen = new HashSet<JsonObject>();
			Iterator<Integer> iter = lengthKeys.iterator();
			while (iter.hasNext()) {
				int lentgh = iter.next();
				Collection<JsonObject> biggest = mentionLengths.get(lentgh);
				Iterator<JsonObject> iter1 = biggest.iterator();
				while (iter1.hasNext()) {
					JsonObject longestObject = iter1.next();
					if(seen.contains(longestObject)) {
						continue;
					} else {
						seen.add(longestObject);
					}
					finalMentions.add(longestObject);
					// remove all that overlaps with the current
					int startOfInterval = longestObject.get("start").getAsInt();
					int endOfInterval = startOfInterval + lentgh;
					for (int i = startOfInterval; i < endOfInterval; i++) {
						if(mentionStarts.containsKey(i)) {
							Collection<JsonObject> toBeRemoved = mentionStarts.get(i);
							Iterator<JsonObject> iter2 = toBeRemoved.iterator();
							while (iter2.hasNext()) {
								JsonObject toBeRemovedObject = iter2.next();
								seen.add(toBeRemovedObject);
							}
						}
					}
				}
			}
			this.events.remove(uuid);
			in.put(Configurations.MENTIONS_TAG, finalMentions.toString());
			out.onEvent(in);
			// cache the input
		} else {
			System.out.println("UUID:" + uuid);
			this.events.put(uuid, in);
		}
	}

	@Override
	public void onDetach() {

	}
}
