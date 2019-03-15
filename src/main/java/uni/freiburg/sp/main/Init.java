package uni.freiburg.sp.main;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.messaging.kafka.SpKafkaProtocolFactory;

import uni.freiburg.sp.config.Config;
import uni.freiburg.sp.pe.processor.cg.CandidateGenerationController;
import uni.freiburg.sp.pe.processor.cg.mv.CGMajorityVotingController;
import uni.freiburg.sp.pe.processor.ed.EntityDisambiguationController;
import uni.freiburg.sp.pe.processor.ed.mv.EDMajorityVotingController;
import uni.freiburg.sp.pe.processor.id.IDGenerationController;
import uni.freiburg.sp.pe.processor.md.MentionDetectionController;
import uni.freiburg.sp.pe.processor.md.mv.MDMajorityVotingController;

public class Init extends StandaloneModelSubmitter {

	public static void main(String[] args) throws Exception {
		DeclarersSingleton.getInstance().add(new MentionDetectionController()).add(new MDMajorityVotingController())
				.add(new EntityDisambiguationController()).add(new EDMajorityVotingController())
				.add(new CandidateGenerationController()).add(new CGMajorityVotingController())
				.add(new IDGenerationController());

		DeclarersSingleton.getInstance().setPort(Config.INSTANCE.getPort());
		DeclarersSingleton.getInstance().setHostName(Config.INSTANCE.getHost());

		DeclarersSingleton.getInstance().registerDataFormat(new JsonDataFormatFactory());
		DeclarersSingleton.getInstance().registerProtocol(new SpKafkaProtocolFactory());

		new Init().init(Config.INSTANCE);
	}
}
