package it.smartcommunitylab.cartella.asl.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.smartcommunitylab.cartella.asl.csv.ImportFromCsv;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ImportCsvTest {
	
	@Autowired
	ImportFromCsv importFromCsv;
	
//	@Test
//	public void importStudente() {
//		List<String> csv = new ArrayList<>();
//		csv.add("11111111,test1@mail.com");
//		csv.add("22222222,test2@mail.com");
//		importFromCsv.importStudente(csv);
//	}
//	
//	@Test
//	public void importFunzioneStrumentale() {
//		List<String> csv = new ArrayList<>();
//		csv.add("33333333,test3@mail.com,14956");
//		csv.add("44444444,test4@mail.com,15675");
//		importFromCsv.importFunzioneStrumentale(csv);
//	}

}
