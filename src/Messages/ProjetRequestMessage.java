package Messages;

import Datas.Project;
import Datas.enums.DeviceInfoTypes;

public class ProjetRequestMessage {
	
	private Project projet;
	private DeviceInfoTypes demande;
	
	public Project getProjet() {
		return projet;
	}
	public void setProjet(Project projet) {
		this.projet = projet;
	}
	public DeviceInfoTypes getDemande() {
		return demande;
	}
	public void setDemande(DeviceInfoTypes demande) {
		this.demande = demande;
	}
	
}
	
	