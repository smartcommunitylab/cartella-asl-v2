
// configuration.
var folderName = "Trento_import_12_05_10_2017"; //Trento_import_12_05_10_2017, Trento_import_agg_1_11_09_2017
var csvLocalCompetenzeFilePath = folderName + "/competenze.csv";
var csvLocalAbilitaFilePath = folderName + "/abilita.csv";
var csvLocalConoscenzeFilePath = folderName + "/conoscenze.csv";
var csvLocalProfiliFilePath = folderName + "/profili.csv";

// 1. create CSV in utf8 format and then run (node convertToJson.js from command line)

console.log("generating json for " + folderName);

//1.
var csv = require('csv');

var q = require('q');
var fs = require('fs');
const utf8 = require('utf8');

//2.
var objAbilita = csv();
var objCon = csv();
var objCompetenze = csv();
var objProfili = csv();

//4.
var competenzeObjs = [];
var abilitaObjs = {};
var conoscenzeObjs = {};
var profiliMap = {};


objProfili.from.path(csvLocalProfiliFilePath).to.array(function (data) {
	
	// read profili and create map of idProfilo->Descrizione.
	for (var index = 1; index < data.length; index++) {
		
		profiliMap[data[index][0]] = data[index][1].toString();
		
	}
	
	
	// read abilita and create map of idCompetenza->[ abilita1, abilita2, ..]
	objAbilita.from.path(csvLocalAbilitaFilePath).to.array(function (data) {
	
	for (var index = 1; index < data.length; index++) {
		
		if (!abilitaObjs[data[index][0]]) {
			abilitaObjs[data[index][0]] = [];			
		}
		abilitaObjs[data[index][0]].push(data[index][2].toString());
	}
	
		// read conoscenze and create map of idCompetenza->[ conoscenza1, conoscenza2, ..]
		objCon.from.path(csvLocalConoscenzeFilePath).to.array(function (csData) {
		
			for (var co = 1; co < csData.length; co++) {
				if (!conoscenzeObjs[csData[co][0]]) {
					conoscenzeObjs[csData[co][0]] = [];
				}
				conoscenzeObjs[csData[co][0]].push(csData[co][2]);			
			} 
		
			// read competenze and create objects
			objCompetenze.from.path(csvLocalCompetenzeFilePath).to.array(function (data) {
				for (var index = 1; index < data.length; index++) {
					var competenza = { idCompetenza: data[index][1], idProfilo: data[index][0], livelloEQF: data[index][5], titolo: data[index][2], profilo: profiliMap[data[index][0]], 
					conoscenze: conoscenzeObjs[data[index][1]],
					abilita: abilitaObjs[data[index][1]]
				}
		
				competenzeObjs.push(competenza);
			}
			for (var competenza of competenzeObjs ) {
				console.log(competenza.abilita);
			}
		
			var json = JSON.stringify(competenzeObjs);
			fs.writeFile(folderName+ '.json', json);
		
			});

		});
	});

});


