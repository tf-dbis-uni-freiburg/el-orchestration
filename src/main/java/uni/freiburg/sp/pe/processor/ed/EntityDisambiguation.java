
package uni.freiburg.sp.pe.processor.ed;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uni.freiburg.sp.pe.processor.Configurations;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public class EntityDisambiguation extends StandaloneEventProcessorEngine<EntityDisambiguationParameters> {

	private static Logger LOG;
	private String system;

	public EntityDisambiguation(EntityDisambiguationParameters params) {
		super(params);
	}

	@Override
	public void onInvocation(EntityDisambiguationParameters parameters, DataProcessorInvocation graph) {
		this.system = parameters.getSystem();
	}

	@Override
	// in - events, out for the next component
	public void onEvent(Map<String, Object> in, String sourceInfo, SpOutputCollector out) {
		// entity disambiguation
		System.out.println("Entity Disambiguation Step!");

		// construct a json object that is given as an input to the entity
		// disambiguation
		// step
		JsonObject requestInputJson = new JsonObject();
		requestInputJson.addProperty(Configurations.TASK_TAG, Configurations.TASK_ED_VALUE);
		// empty params object
		JsonObject params = new JsonObject();
		requestInputJson.add(Configurations.PARAMS_TAG, params);

		// parse input stream and add its properties to the json file
		// such are text, knowledge-base and language, mentions and candidates received
		// from the
		// previous step
		JsonObject data = new JsonObject();
		for (Entry<String, Object> entry : in.entrySet()) {
			if (entry.getKey().equals(Configurations.MENTIONS_TAG)) {
				String mentionedArray = ((String) entry.getValue());
				JsonParser parser = new JsonParser();
				JsonArray mentionsArray = parser.parse(mentionedArray).getAsJsonArray();
				data.add(Configurations.MENTIONS_TAG, mentionsArray);
			} else {
				data.addProperty(entry.getKey(), (String) entry.getValue());
			}
		}
		requestInputJson.add(Configurations.DATA_TAG, data);

		String edResponse = null;
		try {
			edResponse = Request.Post(Configurations.host + system)
					.bodyString(requestInputJson.toString(), ContentType.APPLICATION_JSON).execute().returnContent()
					.asString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonParser parser = new JsonParser();
		JsonObject o = parser.parse(edResponse).getAsJsonObject();
		in.put(Configurations.MENTIONS_TAG, o.get(Configurations.MENTIONS_TAG).toString());
		out.onEvent(in);
	}

	@Override
	public void onDetach() {

	}
}
