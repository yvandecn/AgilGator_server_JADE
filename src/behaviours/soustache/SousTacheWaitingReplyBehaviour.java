package behaviours.soustache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Agents.UtilisateursAgent;
import Datas.SubTask;
import Datas.Utilisateur;
import Datas.Constantes.ConstantesTables;
import Messages.BDDAnswerMessage;
import Messages.ServerLiaisonMessage;
import Messages.UserListMessage;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behaviour attendant une r�ponse � sa request
 * 
 * @author Nicolas
 *
 */
public class SousTacheWaitingReplyBehaviour extends Behaviour {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private String conversationId;
	private SubTask soustache;
	
	public SousTacheWaitingReplyBehaviour(String conversationId, SubTask soustache) {
		this.conversationId = conversationId;
		this.soustache = soustache;
	}
	
	@Override
	public void action() {
		ACLMessage message = myAgent.receive(MessageTemplate.and(MessageTemplate.MatchConversationId(conversationId), MessageTemplate.and(MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM), MessageTemplate.MatchConversationId(conversationId)), MessageTemplate.MatchSender(getBDDAgent()))));
		if (message != null) {
			System.out.println(myAgent.getLocalName() + " re�u -> " + message.getContent());
			// Il r�cup�re le r�sultat de la requ�te.
			ObjectMapper omap = new ObjectMapper();
			try {
				BDDAnswerMessage answer = omap.readValue(message.getContent(),BDDAnswerMessage.class);
				if(answer !=null){
					if(answer !=null){
						switch(answer.getDemande()){
							case CREE_SOUS_TACHE:
								this.createMessage(answer);
								break;
							case MODIFIER_SOUS_TACHE:
								this.createMessage(answer);
								break;
							case SUPPRIMER_SOUS_TACHE:
								this.createMessage(answer);
								break;
							default:
								break;
						}
					}
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void createMessage(BDDAnswerMessage answer){
		// ServeurLiaison message model
		ObjectMapper omapSL = new ObjectMapper();
		ServerLiaisonMessage sl = new ServerLiaisonMessage();
		sl.setDemande(answer.getDemande());
		soustache.setId(answer.getId());
		sl.setSousTache(soustache);
	
		String content ="";
		try {
			content = omapSL.writeValueAsString(sl);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Ecriture du message
		ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
		reply.addReceiver(getServeurAgent());
		reply.setContent(content);
		reply.setConversationId(conversationId);
		myAgent.send(reply);
	}

	@Override
	public boolean done() {
		if (step == 1) {
			return true;
		}
		return false;
	}
	
	private AID getBDDAgent() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("BDD");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			return result[0].getName();
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
		return null;
	}
	private AID getServeurAgent() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Serveur");
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
