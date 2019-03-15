package uni.freiburg.sp.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.nlp2rdf.nif20.NIF20Format;
import org.nlp2rdf.parser.NIFParser;

public class Parser {

	public static final String DB_TTL_FILE_NAME = "db.ttl";
	public static final String KORE_TTL_FILE_NAME = "kore.ttl";

	public static List<Document> loadDBData() {
		String text;
		List<Document> documents = new ArrayList<>();
		try {
			text = readFile(DB_TTL_FILE_NAME, Charset.defaultCharset());
			NIFParser parser = new NIFParser(text);
			documents = readNIFString(text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return documents;
	}

	public static List<Document> loadKoreData() {
		String text;
		List<Document> documents = new ArrayList<>();
		try {
			text = readFile(KORE_TTL_FILE_NAME, Charset.defaultCharset());
			NIFParser parser = new NIFParser(text);
			documents = readNIFString(text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return documents;
	}

	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	private static List<Document> readNIFString(String nifString) {
		Map<String, ArrayList<CustomEntityMention>> phrasesMap = new HashMap<String, ArrayList<CustomEntityMention>>();
		Model model = ModelFactory.createDefaultModel();
		model.read(new ByteArrayInputStream(nifString.getBytes()), null, "TTL");
		// get all phrases
		StmtIterator iter = model.listStatements(null, RDF.type, model.getResource(NIF20Format.NIF_PROPERTY_PHRASE));
		while (iter.hasNext()) {
			Statement stm = iter.nextStatement();
			Resource entityRes = stm.getSubject().asResource();
			String mention = entityRes.getProperty(model.getProperty(NIF20Format.NIF_PROPERTY_ANCHOR_OF)).getObject()
					.asLiteral().getString();
			String referenceContext = entityRes
					.getProperty(model.getProperty(NIF20Format.NIF_PROPERTY_REFERENCE_CONTEXT)).getObject().toString();
			int beginIndex = entityRes.getProperty(model.getProperty(NIF20Format.NIF_PROPERTY_BEGININDEX)).getObject()
					.asLiteral().getInt();
			int endIndex = entityRes.getProperty(model.getProperty(NIF20Format.NIF_PROPERTY_ENDINDEX)).getObject()
					.asLiteral().getInt();
			String identRef = entityRes.getProperty(model.getProperty(NIF20Format.RDF_PROPERTY_IDENTREF)).getObject()
					.toString();
			CustomEntityMention em = new CustomEntityMention();
			em.setMention(mention);
			em.setBeginIndex(beginIndex);
			em.setEndIndex(endIndex);
			em.setContext(stm.getSubject().getNameSpace());
			em.setIdentReference(identRef);
			em.setReferenceContext(referenceContext);

			// add the mention detection entity
			if (phrasesMap.get(referenceContext) != null) {
				ArrayList<CustomEntityMention> mentionDetection = phrasesMap.get(referenceContext);
				mentionDetection.add(em);
				phrasesMap.put(referenceContext, mentionDetection);
			} else {
				ArrayList<CustomEntityMention> mentionDetection = new ArrayList<>();
				mentionDetection.add(em);
				phrasesMap.put(referenceContext, mentionDetection);
			}
		}
		List<Document> documents = new ArrayList<>();
		// list all context
		iter = model.listStatements(null, RDF.type, model.getResource(NIF20Format.NIF_PROPERTY_CONTEXT));
		while (iter.hasNext()) {
			Statement stm = iter.nextStatement();
			Resource contextRes = stm.getSubject().asResource();
			Statement textProperty = contextRes.getProperty(model.getProperty(NIF20Format.NIF_PROPERTY_ISSTRING));
			if (textProperty != null) {
				String text = textProperty.getObject().asLiteral().getString();
				Document doc = new Document(phrasesMap.get(contextRes.toString()), text);
				documents.add(doc);
			}
		}
		return documents;
	}
}
