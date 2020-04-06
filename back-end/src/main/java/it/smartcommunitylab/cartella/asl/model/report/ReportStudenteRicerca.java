package it.smartcommunitylab.cartella.asl.model.report;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import it.smartcommunitylab.cartella.asl.model.Studente;

public class ReportStudenteRicerca extends Report {

	private String idStudente;
	private String classe;
	
    private OreSvolte oreSvolteTerza = new OreSvolte();
    private OreSvolte oreSvolteQuarta = new OreSvolte();;
    private OreSvolte oreSvolteQuinta = new OreSvolte();;

    public static class OreSvolte {
        private int hours = 0;
        private int total = 0;

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }


    public ReportStudenteRicerca() {

    }

    public ReportStudenteRicerca(Studente studente) {
        idStudente = studente.getId();
        classe = studente.getClassroom();
        annoCorso = studente.getAnnoCorso();
        cognomeNome = studente.getSurname() + " " + studente.getName();
    }

	private String cognomeNome;

	public String getIdStudente() {
		return idStudente;
	}

	public void setIdStudente(String idStudente) {
		this.idStudente = idStudente;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getCognomeNome() {
		return cognomeNome;
	}

	public void setCognomeNome(String cognomeNome) {
		this.cognomeNome = cognomeNome;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

    public OreSvolte getOreSvolteTerza() {
        return oreSvolteTerza;
    }

    public void setOreSvolteTerza(OreSvolte oreSvolteTerza) {
        this.oreSvolteTerza = oreSvolteTerza;
    }

    public OreSvolte getOreSvolteQuarta() {
        return oreSvolteQuarta;
    }

    public void setOreSvolteQuarta(OreSvolte oreSvolteQuarta) {
        this.oreSvolteQuarta = oreSvolteQuarta;
    }

    public OreSvolte getOreSvolteQuinta() {
        return oreSvolteQuinta;
    }

    public void setOreSvolteQuinta(OreSvolte oreSvolteQuinta) {
        this.oreSvolteQuinta = oreSvolteQuinta;
    }
}
