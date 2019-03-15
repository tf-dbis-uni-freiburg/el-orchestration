package uni.freiburg.sp.parser;

import org.nlp2rdf.parser.EntityMention;

public class CustomEntityMention extends EntityMention {
	private String identReference;

	public CustomEntityMention() {
		super();
	}

	public String getIdentReference() {
		return identReference;
	}

	public void setIdentReference(String identReference) {
		this.identReference = identReference;
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof CustomEntityMention) {
			CustomEntityMention mention = (CustomEntityMention) object;
			if(this.getBeginIndex() == mention.getBeginIndex() &&
					this.getEndIndex() == mention.getEndIndex() &&
					this.getMention() == mention.getMention()) {
				return true;
			}	
		}
		return false;
	}
}
