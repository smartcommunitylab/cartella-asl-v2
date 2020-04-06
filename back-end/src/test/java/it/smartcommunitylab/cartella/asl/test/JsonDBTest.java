package it.smartcommunitylab.cartella.asl.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.smartcommunitylab.cartella.asl.util.JsonDB;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableConfigurationProperties
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JsonDBTest {

	private static final int COMPETENZE_N = 10000;
	private static final int COMPETENZE_W = 100;
	
	@Autowired
	private JsonDB jsonDB;
	
	@Autowired
	ApplicationContext applicationContext;
	
//	@Test
	public void t0Export() throws Exception {
		jsonDB.exportCompetenze();
		jsonDB.exportEsperienzeSvolte();
//		jsonDB.exportAnniAlternanza();
		jsonDB.exportPianiAlternanza();
	}

	@Test
	public void t2Import() throws Exception {
		jsonDB.importCompetenze();
		jsonDB.importEsperienzeSvolte();
//		jsonDB.importAnniAlternanza();
		jsonDB.importPianiAlternanza();
	}


}