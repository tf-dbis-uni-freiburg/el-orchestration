package uni.freiburg.sp.parser;

import java.util.List;
import java.util.UUID;

public class Document {
	private String id;
	private List<CustomEntityMention> relevant;
	private String text;

	public Document(List<CustomEntityMention> relevant, String text) {
		String uniqueID = UUID.randomUUID().toString();
		this.id = uniqueID;
		this.relevant = relevant;
		this.text = text;
	}

	public List<CustomEntityMention> getRelevant() {
		return relevant;
	}

	public String getText() {
		return text;
	}

	public String getId() {
		return id;
	}
}
