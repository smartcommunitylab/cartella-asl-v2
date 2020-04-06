"use strict";

var express = require('express')
    //     bodyParser  = require('body-parser'),
    //     fs          = require('fs'),
var  app = express()
//     attivitaTipologie   = JSON.parse(fs.readFileSync('data/attivitaTipologie.json', 'utf-8')),
//     attivitaArchivio   = JSON.parse(fs.readFileSync('data/attivitaArchivio.json', 'utf-8')),
//     attivitaAttive   = JSON.parse(fs.readFileSync('data/attivitaAttive.json', 'utf-8')),
//     pianiArchivio   = JSON.parse(fs.readFileSync('data/pianiAlternanzaArchivio.json', 'utf-8')),
// 	pianiAttivi   = JSON.parse(fs.readFileSync('data/pianiAlternanzaAttivi.json', 'utf-8')),
// 	pianiStudio   = JSON.parse(fs.readFileSync('data/pianiStudio.json', 'utf-8')),
//     pianoSingolo   = JSON.parse(fs.readFileSync('data/pianoInstance.json', 'utf-8')),
//     richieste   = JSON.parse(fs.readFileSync('data/richieste.json', 'utf-8')),
//     competenzeAbilita   = JSON.parse(fs.readFileSync('data/competenzeAbilita.json', 'utf-8')),
//     competenzeCompetenze   = JSON.parse(fs.readFileSync('data/competenzeCompetenze.json', 'utf-8')),
//     competenzeConoscenze   = JSON.parse(fs.readFileSync('data/competenzeConoscenze.json', 'utf-8')),
//     competenzeProfili   = JSON.parse(fs.readFileSync('data/competenzeProfili.json', 'utf-8')),
//     programmiArchivio   = JSON.parse(fs.readFileSync('data/programmaAlternanzaArchivio.json', 'utf-8')),
//     programmiAttivi   = JSON.parse(fs.readFileSync('data/programmaAlternanzaAttivi.json', 'utf-8')),
//     reportClassi = JSON.parse(fs.readFileSync('data/reportClassiPerProgrammi.json', 'utf-8')),
//     reportClasse = JSON.parse(fs.readFileSync('data/reportClasse.json', 'utf-8')),
//     reportStudenti = JSON.parse(fs.readFileSync('data/reportStudentiPerProgrammi.json', 'utf-8')),
//     attivitaMancanti = JSON.parse(fs.readFileSync('data/attivitaMancanti.json', 'utf-8')),
//     singleCompetenza =JSON.parse(fs.readFileSync('data/singleCompetenza.json', 'utf-8')),
//     singleAttivita =JSON.parse(fs.readFileSync('data/singleAttivita.json', 'utf-8')),
//     attivitaGiornalieraLista = JSON.parse(fs.readFileSync('data/attivitaGiornalieraLista.json', 'utf-8')),
//     attivitaGiornalieraSingola = JSON.parse(fs.readFileSync('data/attivitaGiornalieraSingola.json', 'utf-8')),
//     attivitaGiornalieraEccezioni = JSON.parse(fs.readFileSync('data/attivitaGiornalieraEccezioni.json', 'utf-8')),
//     attivitaGiornalieraStudenti = JSON.parse(fs.readFileSync('data/attivitaGiornalieraStudenti.json', 'utf-8')),
//     attivitaGiornalieraStudente = JSON.parse(fs.readFileSync('data/attivitaGiornalieraStudente.json', 'utf-8'))
    
// app.use(bodyParser.urlencoded({ extended: true }));
// app.use(bodyParser.json());

// //The dist folder has our static resources (index.html, css, images)
app.use(express.static(__dirname + '/dist'));

// app.get('/api/tipologie', (req, res) => {
//     res.json(attivitaTipologie);
// });

// app.get('/api/attivitaArchivio', (req, res) => {
//     res.setHeader('X-InlineCount', attivitaArchivio.length);
//     res.json(attivitaArchivio);
// });
// app.get('/api/attivitaArchivio/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//         skipVal = req.params.skip,
//         skip = (isNaN(skipVal)) ? 0 : +skipVal;
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     if (top > attivitaArchivio.length) {
//         top = skip + (attivitaArchivio.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedAttivitaArchivio = attivitaArchivio.slice(skip, top);
//     res.setHeader('X-InlineCount', attivitaArchivio.length);
//     res.json(pagedAttivitaArchivio);
// });
// app.get('/api/attivitaAttive/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//         skipVal = req.params.skip,
//         skip = (isNaN(skipVal)) ? 0 : +skipVal;
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     if (top > attivitaAttive.length) {
//         top = skip + (attivitaAttive.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedAttivitaAttive = attivitaAttive.slice(skip, top);
//     res.setHeader('X-InlineCount', attivitaAttive.length);
//     res.json(pagedAttivitaAttive);
// });
// app.get('/api/attivita/:id', (req, res) => {
//     let attivitaId = req.params.id;
//     res.json(attivitaAttive[0]);
// });


// app.get('/api/pianiArchivio', (req, res) => {
//     res.setHeader('X-InlineCount', pianiArchivio.length);
//     res.json(pianiArchivio);
// });
// app.get('/api/pianiArchivio/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//         skipVal = req.params.skip,
//         skip = (isNaN(skipVal)) ? 0 : +skipVal;
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     if (top > pianiArchivio.length) {
//         top = skip + (pianiArchivio.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedPianiArchivio = pianiArchivio.slice(skip, top);
//     res.setHeader('X-InlineCount', pianiArchivio.length);
//     res.json(pagedPianiArchivio);
// });
// app.get('/api/pianiAttivi/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//         skipVal = req.params.skip,
//         skip = (isNaN(skipVal)) ? 0 : +skipVal;
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     let filteredPiani = pianiAttivi;
//     if (req.query.activeFilter) {
//         filteredPiani = pianiAttivi.filter(function (data) { return data.attivo; });
//     }
//     if (req.query.corsoStudioFilter) {
//         filteredPiani = filteredPiani.filter(function (data) { return data.corsoStudio == req.query.corsoStudioFilter; });
//     }

//     if (top > filteredPiani.length) {
//         top = skip + (filteredPiani.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedPianiAttivi = filteredPiani.slice(skip, top);
//     res.setHeader('X-InlineCount', filteredPiani.length);
//     res.json(pagedPianiAttivi);
// });
// app.get('/api/corsostudio', (req, res) => {
//     res.json([
//         {
//             id: 1,
//             name: "Informatica"
//         }, {
//             id: 2,
//             name: "Elettrotecnica"
//         }, {
//             id: 3,
//             name: "Chimica"
//         }
//     ]);
// });
// app.get('/api/piano/:id', (req, res) => {
//     pianoSingolo.competenze.forEach(element => {
//         let conoscenze = competenzeConoscenze.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.conoscenze = conoscenze;

//         let abilita = competenzeAbilita.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.abilita = abilita;
//     });

//     res.json(pianoSingolo);
// });
// app.get('/api/pianoattivita/:id', (req, res) => {
//     pianoSingolo.competenze.forEach(element => {
//         let conoscenze = competenzeConoscenze.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.conoscenze = conoscenze;

//         let abilita = competenzeAbilita.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.abilita = abilita;
//     });
//     let obj = pianoSingolo.attivita[5][0];
//     obj.anno = 5;
//     res.json(obj);
// });
// app.delete('/api/piani/delete/:id', (req, res) => {
//     res.json({ status: true });
// })
// app.post('/api/piani/modifica', (req, res) => {
//     let postPiani = req.body;
//     res.json({ status: true });
// })
// app.get('/api/richieste/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//         skipVal = req.params.skip,
//         skip = (isNaN(skipVal)) ? 0 : +skipVal;
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     if (top > attivitaAttive.length) {
//         top = skip + (attivitaAttive.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedRichieste = richieste.slice(skip, top);
//     res.setHeader('X-InlineCount', richieste.length);
//     res.json(pagedRichieste);
// });
// app.get('/api/attivitaFiltro', (req, res) => {
//     res.setHeader('X-InlineCount', attivitaArchivio.length);
//     res.json(attivitaArchivio.slice(2, 4));
// });
// app.get('/api/attivitaAttive', (req, res) => {
//     res.setHeader('X-InlineCount', attivitaAttive.length);
//     res.json(attivitaAttive);
// });
// app.delete('/api/attivita/delete/:id', (req, res) => {
//     res.setHeader('X-InlineCount', attivitaAttive.length);
//     res.json({ status: true });
// })
// app.post('/api/attivita/modifica', (req, res) => {
//     let postAttivita = req.body;
//     res.json({ status: true });
// })
// app.get('/api/richieste', (req, res) => {
//     res.setHeader('X-InlineCount', richieste.length);
//     res.json(richieste);
// });
// app.get('/api/richieste/:id', (req, res) => {
//     // res.setHeader('X-InlineCount', richieste.length);
//     res.json(richieste[0]);
// });
// app.post('/api/refuserichiesta', (req, res) => {
//     //res.json(richieste);
//     res.json(req.body);
// });


// app.get('/api/esperienzaSvolta/:id', (req, res) => {
//     let activityId = +req.params.id;
//     console.log(activityId);
//     let selectedActivity = {};
//     for (let activity of activities) {
//         if (activity.id === activityId) {
//             selectedActivity = activity;
//             break;
//         }
//     }
//     res.json(selectedActivity);
// });


// /**
//  * Competenze
//  */

// app.get('/api/competenze/profili', (req, res) => {
//     res.json(competenzeProfili);
// });
// app.get('/api/competenze/:idProfilo', (req, res) => {
//     let profileId = req.params.idProfilo;

//     let competenze = competenzeCompetenze.filter(function (data) {
//         return data.id_profilo == profileId;
//     })
//     competenze.forEach(element => {
//         let conoscenze = competenzeConoscenze.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.conoscenze = conoscenze;

//         let abilita = competenzeAbilita.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.abilita = abilita;
//     });

//     res.json(competenze);
// });
// app.get('/api/competenze', (req, res) => {
//     let competenze = competenzeCompetenze;
//     competenze.forEach(element => {
//         let conoscenze = competenzeConoscenze.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.conoscenze = conoscenze;

//         let abilita = competenzeAbilita.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.abilita = abilita;
//     });

//     res.json(competenze);
// });

// app.get('/api/competenze/attivita/:idAttivita', (req, res) => {
//     let attivitaId = req.params.idAttivita;
//     let i = 0;

//     let competenze = competenzeCompetenze.filter(function (data) {
//         i++;
//         return data.id_profilo == "12.IEFP.1" && (i % 2 == 0); //filter "randomly" to remove some competenze
//     })
//     competenze.forEach(element => {
//         let conoscenze = competenzeConoscenze.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.conoscenze = conoscenze;

//         let abilita = competenzeAbilita.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.abilita = abilita;
//     });

//     res.json(competenze);
// });
// app.post('/api/competenze/attivita/:idAttivita', (req, res) => {
//     let attivitaId = req.params.idAttivita;
//     let i = 0;

//     let competenze = competenzeCompetenze.filter(function (data) {
//         i++;
//         return data.id_profilo == "12.IEFP.1" && (i % 2 == 0); //filter "randomly" to remove some competenze
//     })
//     competenze.forEach(element => {
//         let conoscenze = competenzeConoscenze.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.conoscenze = conoscenze;

//         let abilita = competenzeAbilita.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.abilita = abilita;
//     });

//     res.json(competenze);
// });
// app.delete('/api/competenze/attivita/:idAttivita', (req, res) => {
//     let attivitaId = req.params.idAttivita;
//     let i = 0;

//     let competenze = competenzeCompetenze.filter(function (data) {
//         i++;
//         return data.id_profilo == "12.IEFP.1" && (i % 2 == 0); //filter "randomly" to remove some competenze
//     })
//     competenze.forEach(element => {
//         let conoscenze = competenzeConoscenze.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.conoscenze = conoscenze;

//         let abilita = competenzeAbilita.filter(function (data) {
//             return data.id_competenza == element.id_competenza;
//         })
//         element.abilita = abilita;
//     });

//     res.json(competenze);
// });

// app.get('/api/conoscenze/:idCompetenza', (req, res) => {
//     let competenzaId = req.params.idCompetenza;
//     res.json();
// });
// app.get('/api/abilita/:idCompetenza', (req, res) => {
//     let competenzaId = req.params.idCompetenza;
//     res.json(competenzeAbilita.filter(function (data) {
//         return data.id_competenza == competenzaId;
//     }));
// });


// /********************* 

// Programmi Alternanza

//  **********************/
// app.get('/api/programmiArchivio/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//         skipVal = req.params.skip,
//         skip = (isNaN(skipVal)) ? 0 : +skipVal;
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     if (top > programmiArchivio.length) {
//         top = skip + (programmiArchivio.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedProgrammiArchivio = programmiArchivio.slice(skip, top);
//     res.setHeader('X-InlineCount', programmiArchivio.length);
//     res.json(pagedProgrammiArchivio);
// });
// app.get('/api/programmiAttivi/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//         skipVal = req.params.skip,
//         skip = (isNaN(skipVal)) ? 0 : +skipVal;
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     let filteredProgrammi = programmiAttivi;
//     // if (req.query.stateFilter) {
//     //     filteredProgrammi = programmiAttivi.filter(function (data) { return data.state; });
//     // }
//     // if (req.query.pianoStudio) {
//     //     filteredProgrammi = filteredProgrammi.filter(function (data) { return data.pianoStudio == req.query.pianoStudioFilter; });
//     // }

//     if (top > filteredProgrammi.length) {
//         top = skip + (filteredProgrammi.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedProgrammiArchivio = filteredProgrammi.slice(skip, top);
//     res.setHeader('X-InlineCount', filteredProgrammi.length);
//     res.json(pagedProgrammiArchivio);
// });



// //param annoCorso , annoScolastico
// app.get('/api/reportClassiByPiano/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//         skipVal = req.params.skip,
//         skip = (isNaN(skipVal)) ? 0 : +skipVal;
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     let filteredClassi = reportClassi;
//     // if (req.query.stateFilter) {
//     //     filteredProgrammi = programmiAttivi.filter(function (data) { return data.state; });
//     // }
//     // if (req.query.pianoStudio) {
//     //     filteredProgrammi = filteredProgrammi.filter(function (data) { return data.pianoStudio == req.query.pianoStudioFilter; });
//     // }

//     if (top > filteredClassi.length) {
//         top = skip + (filteredClassi.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedClassi = filteredClassi.slice(skip, top);
//     res.setHeader('X-InlineCount', filteredClassi.length);
//     res.json(pagedClassi);
// });
// app.get('/api/attivitaMancantiForClasse', (req, res) => {
//     res.setHeader('X-InlineCount', attivitaMancanti.length);
//     res.json(attivitaMancanti);
// });



// app.get('/api/reportStudentiByPiano/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//         skipVal = req.params.skip,
//         skip = (isNaN(skipVal)) ? 0 : +skipVal;
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     let filteredStudenti = reportStudenti;
//     // if (req.query.stateFilter) {
//     //     filteredProgrammi = programmiAttivi.filter(function (data) { return data.state; });
//     // }
//     // if (req.query.pianoStudio) {
//     //     filteredProgrammi = filteredProgrammi.filter(function (data) { return data.pianoStudio == req.query.pianoStudioFilter; });
//     // }

//     if (top > filteredStudenti.length) {
//         top = skip + (filteredStudenti.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);
//     var pagedStudenti = filteredStudenti.slice(skip, top);
//     res.setHeader('X-InlineCount', filteredStudenti.length);
//     res.json(pagedStudenti);
// });
// app.get('/api/getCompetenza/:id', (req, res) => {
//     res.json(singleCompetenza);
// });
// app.get('/api/getAttivita/:id', (req, res) => {
//     res.json(singleAttivita);
//     });


// app.get('/api/reportClasse', (req, res) => {
//     let competenzaId = req.params.idClasse;
//     res.json(reportClasse);
// });
// app.get('/api/states', (req, res) => {
//     res.json(states);
// });



// app.get('/api/attivitagiornaliera', (req, res) => {
//     res.json(attivitaGiornalieraLista);
// });
// app.get('/api/attivitagiornaliera/:id', (req, res) => {
//     res.json(attivitaGiornalieraSingola);
// });
// app.get('/api/attivitagiornalieraeccezioni', (req, res) => {
//     res.json(attivitaGiornalieraEccezioni);
// });
// app.get('/api/attivitagiornalierastudenti', (req, res) => {
//     res.json(attivitaGiornalieraStudenti);
// });
// app.get('/api/attivitagiornalierastudenti/:id', (req, res) => {
//     res.json(attivitaGiornalieraStudente);
// });

// app.post('/api/addOpportunita', (req, res) => {
//     res.json(true);
// });




// app.post('/api/auth/login', (req, res) => {
//     var userLogin = req.body;
//     //Add "real" auth here. Simulating it by returning a simple boolean.
//     res.json(true);
// });

// app.post('/api/auth/logout', (req, res) => {
//     res.json(true);
// });

// redirect all others to the index (HTML5 history)
app.all('/*', function (req, res) {
    res.sendFile(__dirname + '/dist/index.html');
});

app.listen(3003);

console.log('Express listening on port 3003.');

//Open browser
var opn = require('opn');

opn('http://localhost:3003').then(() => {
    console.log('Browser closed.');
});


