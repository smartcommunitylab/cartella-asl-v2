package it.smartcommunitylab.cartella.asl.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import it.smartcommunitylab.cartella.asl.services.parix.DettaglioImpresa;
import it.smartcommunitylab.cartella.asl.services.parix.IscrizioneRea;

@Component
public class PARIXService {

	private static final transient Logger logger = LoggerFactory.getLogger(PARIXService.class);
	
	@Value("${parix.endpoint}")
	private String endpoint;

	@Value("${parix.keystore.uri}")
	private String keystore;

	@Value("${parix.keystore.password}")
	private String keystorePassword;

	@Value("${parix.user}")
	private String user;

	@Value("${parix.password}")
	private String password;

	private SSLSocketFactory sslSocketFactory;
	
	private DocumentBuilder db;
	
	@PostConstruct
	public void init() throws Exception {
		KeyStore ksClient = KeyStore.getInstance("PKCS12");
		ksClient.load(new FileInputStream(ResourceUtils.getFile(keystore)), keystorePassword.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ksClient, keystorePassword.toCharArray());
		
		TrustManager[] trust = new TrustManager[] { 
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
	
				@SuppressWarnings("unused")
				public boolean isServerTrusted(X509Certificate[] certs) {
					return true;
				}
	
				@SuppressWarnings("unused")
				public boolean isClientTrusted(X509Certificate[] certs) {
					return true;
				}
	
				public void checkServerTrusted(X509Certificate[] certs, String authType)
						throws CertificateException {
					return;
				}
	
				public void checkClientTrusted(X509Certificate[] certs, String authType)
						throws CertificateException {
					return;
				}
			} 
		};
		
		// Create new SSLContext using our new TrustManagerFactory
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(kmf.getKeyManagers(), trust, null);
		// Get a SSLSocketFactory from our SSLContext
		sslSocketFactory = context.getSocketFactory();
		
		// Create document parser 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		db = dbf.newDocumentBuilder();
	}
	
	
	public DettaglioImpresa getDettaglioImpresa(String cf) throws Exception {
		IscrizioneRea iscrizioneRea = ricercaImpresePerCF(cf);
		DettaglioImpresa dettaglioImpresa = dettaglioCompletoImpresa(
				iscrizioneRea.getProvincia(), iscrizioneRea.getCodiceRea());
		return dettaglioImpresa;
	}
	
	private String getStringDataFromElement(Element e) {
    Node child = e.getFirstChild();
    if (child instanceof org.w3c.dom.CharacterData) {
    	org.w3c.dom.CharacterData cd = (org.w3c.dom.CharacterData) child;
      return cd.getData();
    }
    return "";
  }
	
	private String getError(Document doc) {
		String tipo = "";
		String messaggio = "";
		NodeList nodeList = doc.getElementsByTagNameNS("*", "ERRORE");
		if(nodeList.getLength() > 0) {
			Element errorElement = (Element) nodeList.item(0);
			nodeList = errorElement.getChildNodes();
			for(int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				if(element.getNodeName().contains("TIPO")) {
					tipo = getStringDataFromElement(element);
				}
				if(element.getNodeName().contains("MSG_ERR")) {
					messaggio = getStringDataFromElement(element);
				}
			}			
		}
		return tipo + " - " + messaggio;
	}
	
	private IscrizioneRea ricercaImpresePerCF(String cf) throws Exception {
		IscrizioneRea iscrizioneRea = new IscrizioneRea();
		
		BufferedReader buf = new BufferedReader(
				new InputStreamReader(Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("templates/parix/ricercacf-request.xml"), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = buf.readLine()) != null) {
			sb.append(line).append("\n");
		}
		String contentString = sb.toString();
		contentString = contentString.replace("{{user}}", user);
		contentString = contentString.replace("{{password}}", password);
		contentString = contentString.replace("{{cf}}", cf);
		
		String resultContent = postRequest(contentString, "/RicercaImpresePerCF");
		
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(resultContent));
    Document doc = db.parse(is);
    NodeList nodeList = doc.getElementsByTagNameNS("*", "ESITO");
    if(nodeList.getLength() > 0) {
    	Element esitoElement = (Element) nodeList.item(0);
    	if(getStringDataFromElement(esitoElement).equalsIgnoreCase("OK")) {
    		nodeList = doc.getElementsByTagNameNS("*", "DATI_ISCRIZIONE_REA");
    		if(nodeList.getLength() > 0) {
    			Element iscrizioneReaElement = (Element) nodeList.item(0);
    			nodeList = iscrizioneReaElement.getElementsByTagNameNS("*", "CCIAA");
    			if(nodeList.getLength() > 0) {
    				Element provinciaElement = (Element) nodeList.item(0);
    				iscrizioneRea.setProvincia(getStringDataFromElement(provinciaElement));
    			}
    			nodeList = iscrizioneReaElement.getElementsByTagNameNS("*", "NREA");
    			if(nodeList.getLength() > 0) {
    				Element reaElement = (Element) nodeList.item(0);
    				iscrizioneRea.setCodiceRea(getStringDataFromElement(reaElement));
    			}
    			nodeList = iscrizioneReaElement.getElementsByTagNameNS("*", "CESSAZIONE");
    			if(nodeList.getLength() > 0) {
    				Element cessazioneElement = (Element) nodeList.item(0);
    				nodeList = cessazioneElement.getElementsByTagNameNS("*", "DT_CANCELLAZIONE");
    				if (nodeList.getLength() > 0) {
    					Element dataCancelElement = (Element) nodeList.item(0);
    					String dataCancel = getStringDataFromElement(dataCancelElement);
    					if (!StringUtils.isEmpty(dataCancel)) {
    				        throw new RuntimeException("ricercaImpresePerCF: DATI_ISCRIZIONE_REA removed from REA - " + cf);
    					}
    				}
    			}
    			
    			return iscrizioneRea;
    		}
        logger.warn("ricercaImpresePerCF: DATI_ISCRIZIONE_REA not found - " + cf);
        throw new RuntimeException("ricercaImpresePerCF: DATI_ISCRIZIONE_REA not found - " + cf);
    	} else {
    		String errorMsg = getError(doc);
    		logger.warn(String.format("ricercaImpresePerCF[%s]:%s", cf, errorMsg));
    		throw new RuntimeException(String.format("ricercaImpresePerCF[%s]:%s", cf, errorMsg));
    	}
    }
    logger.warn("ricercaImpresePerCF: ESITO not found - " + cf);
    throw new RuntimeException("ricercaImpresePerCF: ESITO not found - " + cf);
	}
	
	private DettaglioImpresa dettaglioCompletoImpresa(String provincia, String codiceRea) throws Exception {
		DettaglioImpresa dettaglioImpresa = new DettaglioImpresa();
		
		BufferedReader buf = new BufferedReader(
				new InputStreamReader(Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("templates/parix/dettaglio-request.xml"), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = buf.readLine()) != null) {
			sb.append(line).append("\n");
		}
		String contentString = sb.toString();
		contentString = contentString.replace("{{user}}", user);
		contentString = contentString.replace("{{password}}", password);
		contentString = contentString.replace("{{provincia}}", provincia);
		contentString = contentString.replace("{{rea}}", codiceRea);
		
		String resultContent = postRequest(contentString, "/DettaglioCompletoImpresa");
		
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(resultContent));
    Document doc = db.parse(is);
    NodeList nodeList = doc.getElementsByTagNameNS("*", "ESITO");
    if(nodeList.getLength() > 0) {
    	Element esitoElement = (Element) nodeList.item(0);
    	if(getStringDataFromElement(esitoElement).equalsIgnoreCase("OK")) {
    		nodeList = doc.getElementsByTagNameNS("*", "ESTREMI_IMPRESA");
    		if(nodeList.getLength() > 0) {
    			Element estremiElement = (Element) nodeList.item(0);
    			nodeList = estremiElement.getElementsByTagNameNS("*", "DENOMINAZIONE");
    			if(nodeList.getLength() > 0) {
	      			Element denominazioneElement = (Element) nodeList.item(0);
	      			dettaglioImpresa.setDenominazione(getStringDataFromElement(denominazioneElement));
    			}
    			nodeList = estremiElement.getElementsByTagNameNS("*", "CODICE_FISCALE");
    			if(nodeList.getLength() > 0) {
	      			Element cfElement = (Element) nodeList.item(0);
	      			dettaglioImpresa.setCodiceFiscale(getStringDataFromElement(cfElement));
    			}
    			nodeList = estremiElement.getElementsByTagNameNS("*", "PARTITA_IVA");
    			if(nodeList.getLength() > 0) {
	      			Element cfElement = (Element) nodeList.item(0);
	      			dettaglioImpresa.setPiva(getStringDataFromElement(cfElement));
    			}
    			
    		}
    		
    		nodeList = doc.getElementsByTagNameNS("*", "OGGETTO_SOCIALE");
    		if(nodeList.getLength() > 0) {
    			Element oggettoElement = (Element) nodeList.item(0);
    			dettaglioImpresa.setOggettoSociale(getStringDataFromElement(oggettoElement));
    		}

    		nodeList = doc.getElementsByTagNameNS("*", "FORMA_GIURIDICA");
    		if(nodeList.getLength() > 0) {
    			Element oggettoElement = (Element) nodeList.item(0);
      			nodeList = oggettoElement.getElementsByTagNameNS("*", "C_FORMA_GIURIDICA");
      			if(nodeList.getLength() > 0) {
      				Element forma = (Element) nodeList.item(0);
      				dettaglioImpresa.setFormaGiuridica(getStringDataFromElement(forma));
      			}
    			
    		}

    		nodeList = doc.getElementsByTagNameNS("*", "INFORMAZIONI_SEDE");
    		if(nodeList.getLength() > 0) {
    			Element infoSedeElement = (Element) nodeList.item(0);
    			nodeList = infoSedeElement.getElementsByTagNameNS("*", "INDIRIZZO");
    			if(nodeList.getLength() > 0) {
    				Element indirrizzoElement = (Element) nodeList.item(0);
	      			nodeList = indirrizzoElement.getElementsByTagNameNS("*", "PROVINCIA");
	      			if(nodeList.getLength() > 0) {
	      				Element provinciaElement = (Element) nodeList.item(0);
	      				dettaglioImpresa.setProvincia(getStringDataFromElement(provinciaElement));
	      			}
	      			nodeList = indirrizzoElement.getElementsByTagNameNS("*", "COMUNE");
	      			if(nodeList.getLength() > 0) {
	      				Element comuneElement = (Element) nodeList.item(0);
	      				dettaglioImpresa.setComune(getStringDataFromElement(comuneElement));
	      			}
	      			nodeList = indirrizzoElement.getElementsByTagNameNS("*", "C_COMUNE");
	      			if(nodeList.getLength() > 0) {
	      				Element ccomuneElement = (Element) nodeList.item(0);
	      				dettaglioImpresa.setCodiceComune(getStringDataFromElement(ccomuneElement));
	      			}
	      			nodeList = indirrizzoElement.getElementsByTagNameNS("*", "TOPONIMO");
	      			if(nodeList.getLength() > 0) {
	      				Element toponimoElement = (Element) nodeList.item(0);
	      				dettaglioImpresa.setToponimo(getStringDataFromElement(toponimoElement));
	      			}
	      			nodeList = indirrizzoElement.getElementsByTagNameNS("*", "VIA");
	      			if(nodeList.getLength() > 0) {
	      				Element viaElement = (Element) nodeList.item(0);
	      				dettaglioImpresa.setVia(getStringDataFromElement(viaElement));
	      			}
	      			nodeList = indirrizzoElement.getElementsByTagNameNS("*", "N_CIVICO");
	      			if(nodeList.getLength() > 0) {
	      				Element ncivicoElement = (Element) nodeList.item(0);
	      				dettaglioImpresa.setNumeroCivico(getStringDataFromElement(ncivicoElement));
	      			}
	      			nodeList = indirrizzoElement.getElementsByTagNameNS("*", "CAP");
	      			if(nodeList.getLength() > 0) {
	      				Element capElement = (Element) nodeList.item(0);
	      				dettaglioImpresa.setCap(getStringDataFromElement(capElement));
	      			}
	      			nodeList = indirrizzoElement.getElementsByTagNameNS("*", "TELEFONO");
	      			if(nodeList.getLength() > 0) {
	      				Element telefonoElement = (Element) nodeList.item(0);
	      				dettaglioImpresa.setTelefono(getStringDataFromElement(telefonoElement));
	      			}
	      			nodeList = indirrizzoElement.getElementsByTagNameNS("*", "INDIRIZZO_PEC");
	      			if(nodeList.getLength() > 0) {
	      				Element pecElement = (Element) nodeList.item(0);
	      				dettaglioImpresa.setPec(getStringDataFromElement(pecElement));
	      			}    				
    			}
        		nodeList = infoSedeElement.getElementsByTagNameNS("*", "CODICE_ATECO_UL");
        		if(nodeList.getLength() > 0) {
        			Element atecoElement = (Element) nodeList.item(0);
        			nodeList = atecoElement.getElementsByTagNameNS("*", "ATTIVITA_ISTAT");
        			for(int i=0; i < nodeList.getLength(); i++) {
        				Element istatElement = (Element) nodeList.item(i);
        				NodeList codiceList = istatElement.getElementsByTagNameNS("*", "C_ATTIVITA");
        				if(codiceList.getLength() > 0) {
        					Element codiceIstatElement = (Element) codiceList.item(0); 
        					String codiceIstat = getStringDataFromElement(codiceIstatElement);
        					//check existing codice ateco
        					if(!dettaglioImpresa.getCodiciAteco().contains(codiceIstat)) {
        						//add codice ateco
        						dettaglioImpresa.getCodiciAteco().add(getStringDataFromElement(codiceIstatElement));
        						//add codice description
            				NodeList descList = istatElement.getElementsByTagNameNS("*", "DESC_ATTIVITA");
            				if(descList.getLength() > 0) {
            					Element descElement = (Element) descList.item(0); 
            					dettaglioImpresa.getDescAteco().add(getStringDataFromElement(descElement));
            				} else {
            					dettaglioImpresa.getDescAteco().add("");
            				}
        					}
        				}
        			}
        		}
    		}
    		return dettaglioImpresa;
    	} else {
    		String errorMsg = getError(doc);
    		logger.warn(String.format("dettaglioCompletoImpresa[%s]:%s", codiceRea, errorMsg));
    		throw new RuntimeException(String.format("dettaglioCompletoImpresa[%s]:%s", codiceRea, errorMsg));
    	}
    }
    logger.warn("dettaglioCompletoImpresa: ESITO not found - " + codiceRea);
    throw new RuntimeException("dettaglioCompletoImpresa: ESITO not found - " + codiceRea);
	}
	
	private String postRequest(String contentString, String action) throws Exception {
		URL url = new URL(endpoint);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setSSLSocketFactory(sslSocketFactory);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		conn.setRequestProperty("SOAPAction", action);
		
		OutputStream out = conn.getOutputStream();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		writer.write(contentString);
		writer.close();
		out.close();		

		int responseCode = conn.getResponseCode();
		if (responseCode >= 300) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(
				new InputStreamReader((conn.getInputStream()), Charset.defaultCharset()));
		StringBuffer response = new StringBuffer();
		String output = null;
		while ((output = br.readLine()) != null) {
			response.append(output);
		}
		conn.disconnect();
		String res = new String(response.toString().getBytes(), Charset.forName("UTF-8"));
		return res;
	}
}
