package it.smartcommunitylab.cartella.asl.model.eccezioni;

import java.util.Map;

import com.google.common.collect.Maps;

public class EccezioniGroup {

	private String corsoDiStudio;
	private String corsoDiStudioId;
	
	Map<Long, Eccezione> eccezioni = Maps.newTreeMap();


	public EccezioniGroup(String corsoDiStudio, String corsoDiStudioId) {
		super();
		this.corsoDiStudio = corsoDiStudio;
		this.corsoDiStudioId = corsoDiStudioId;
	}	
	
	public String getCorsoDiStudio() {
		return corsoDiStudio;
	}

	public void setCorsoDiStudio(String corsoDiStudio) {
		this.corsoDiStudio = corsoDiStudio;
	}

	public String getCorsoDiStudioId() {
		return corsoDiStudioId;
	}

	public void setCorsoDiStudioId(String corsoDiStudioId) {
		this.corsoDiStudioId = corsoDiStudioId;
	}

	public Map<Long, Eccezione> getEccezioni() {
		return eccezioni;
	}

	public void setEccezioni(Map<Long, Eccezione> eccezioni) {
		this.eccezioni = eccezioni;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((corsoDiStudioId == null) ? 0 : corsoDiStudioId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EccezioniGroup other = (EccezioniGroup) obj;
		if (corsoDiStudioId == null) {
			if (other.corsoDiStudioId != null)
				return false;
		} else if (!corsoDiStudioId.equals(other.corsoDiStudioId))
			return false;
		return true;
	}

	
	
	
}
