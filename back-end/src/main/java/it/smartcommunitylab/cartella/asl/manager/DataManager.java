package it.smartcommunitylab.cartella.asl.manager;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

@Component
public class DataManager {

	private Map<String, Object> data;
	
	@PostConstruct
	private void init() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		data = mapper.readValue(Resources.toString(Resources.getResource("data.json"), Charsets.UTF_8), Map.class);
	}
	
	public Object get(String type) {
		return data.get(type);
	}
	
	public Map getAll() {
		return data;
	}	
	
}
