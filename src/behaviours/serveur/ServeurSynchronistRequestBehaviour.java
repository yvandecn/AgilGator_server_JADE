package behaviours.serveur;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import Messages.SynchroMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *  * Ce behaviour envoie une requete de synchronisation
 * 
 * @author Nicolas
 *
 */
public class ServeurSynchronistRequestBehaviour extends OneShotBehaviour {
	
	private static final long serialVersionUID = 1L;

		private String conversationId;
		private int userId;
		private int timeStamp;
		
		public ServeurSynchronistRequestBehaviour(int userId, int timeStamp) {
			this.userId = userId;
			this.timeStamp = timeStamp;
			conversationId = userId + "sync"+ timeStamp;
		}
		
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setContent(writeMessage());
			message.setConversationId(conversationId);
			message.addReceiver(getSynchroAgent());
			//Envoi de message vers Sync
		}
		
		private String writeMessage() {
			// Séréalisation JSON
			SynchroMessage corps = new SynchroMessage();
			corps.setUserId(userId);
			corps.setTimeStampLast(timeStamp);
			
			ObjectMapper omap = new ObjectMapper();
			String messageCorps = null;
			try {
				messageCorps = omap.writeValueAsString(corps);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
			return messageCorps;
		}
		
		private AID getSynchroAgent() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("Synchro");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				return result[0].getName();
			} catch(FIPAException fe) {
				fe.printStackTrace();
			}
			return null;
		}
		
	}
	