"use strict";
var express = require('express'),
    // bodyParser = require('body-parser'),
    // fs = require('fs'),
    app = express();
// customers = JSON.parse(fs.readFileSync('data/customers.json', 'utf-8')),
// offers = JSON.parse(fs.readFileSync('data/opportunita.json', 'utf-8')),
// activities = JSON.parse(fs.readFileSync('data/esperienzaSvolta.json', 'utf-8')),
// states = JSON.parse(fs.readFileSync('data/states.json', 'utf-8')),
// competenze = JSON.parse(fs.readFileSync('data/competenze.json', 'utf-8')),
// competenzeAbilita   = JSON.parse(fs.readFileSync('data/competenzeAbilita.json', 'utf-8')),
// competenzeCompetenze = JSON.parse(fs.readFileSync('data/competenzeCompetenze.json', 'utf-8')),
// competenzeConoscenze = JSON.parse(fs.readFileSync('data/competenzeConoscenze.json', 'utf-8')),
// competenzeProfili = JSON.parse(fs.readFileSync('data/competenzeProfili.json', 'utf-8'));

// app.use(bodyParser.urlencoded({ extended: true }));
// app.use(bodyParser.json());

// //The dist folder has our static resources (index.html, css, images)
app.use(express.static(__dirname + '/dist'));


// app.get('/api/competenze', (req, res) => {
//  res.json(competenze);
// });

// app.get('/api/esperienzaSvolta/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//           skipVal = req.params.skip,
//           skip = (isNaN(skipVal)) ? 0 : +skipVal;  
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     if (top > activities.length) {
//         top = skip + (activities.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedEsperienzaSvolta = activities.slice(skip, top);
//     res.setHeader('X-InlineCount', activities.length);
//     res.json(pagedEsperienzaSvolta);
// });

// app.get('/api/esperienzaSvolta/:id', (req, res) => {
//     let activityId = +req.params.id;
// 	console.log(activityId);
//     let selectedActivity = {};
//     for (let activity of activities) {
//         if (activity.id === activityId) {
//            selectedActivity = activity;
//            break;
//         }
//     }  
//     res.json(selectedActivity);
// });

// app.get('/api/customers/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//           skipVal = req.params.skip,
//           skip = (isNaN(skipVal)) ? 0 : +skipVal;  
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     if (top > customers.length) {
//         top = skip + (customers.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedCustomers = customers.slice(skip, top);
//     res.setHeader('X-InlineCount', customers.length);
//     res.json(pagedCustomers);
// });

// app.get('/api/offers/page/:skip/:top', (req, res) => {
//     const topVal = req.params.top,
//           skipVal = req.params.skip,
//           skip = (isNaN(skipVal)) ? 0 : +skipVal;  
//     let top = (isNaN(topVal)) ? 10 : skip + (+topVal);

//     if (top > offers.length) {
//         top = skip + (offers.length - skip);
//     }

//     console.log(`Skip: ${skip} Top: ${top}`);

//     var pagedOffers = offers.slice(skip, top);
//     res.setHeader('X-InlineCount', offers.length);
//     res.json(pagedOffers);
// });

// app.get('/api/offers/:id', (req, res) => {
//     let offerId = +req.params.id;
// 	console.log(offerId);
//     let selectedOffer = {};
//     for (let offer of offers) {
//         if (offer.id === offerId) {
//            selectedOffer = offer;
//            break;
//         }
//     }  
//     res.json(selectedOffer);
// });


// app.post('/api/offers', (req, res) => {
//     let postedOffer = req.body;
//     let maxId = Math.max.apply(Math,customers.map((offer) => offer.id));
//     postedOffer.id = ++maxId;
//     offers.push(postedOffer);
//     res.json(postedOffer);
// });

// app.put('/api/offers/:id', (req, res) => {
//     let putOffer = req.body;
//     let id = +req.params.id;
//     let status = false;


//     for (let i=0,len=offers.length;i<len;i++) {
//         if (offers[i].id === putOffer.id) {
//             offers[i] = putOffer;
//             status = true;
//             break;
//         }
//     }
//     res.json({ status: status });
// });

// app.delete('/api/offers/:id', function(req, res) {
//     let offerId = +req.params.id;
//     for (let i=0,len=offers.length;i<len;i++) {
//         if (offers[i].id === offerId) {
//            offers.splice(i,1);
//            break;
//         }
//     }  
//     res.json({ status: true });
// });




// app.get('/api/customers', (req, res) => {
//     res.json(customers);
// });

// app.get('/api/customers/:id', (req, res) => {
//     let customerId = +req.params.id;
//     let selectedCustomer = {};
//     for (let customer of customers) {
//         if (customer.id === customerId) {
//            selectedCustomer = customer;
//            break;
//         }
//     }  
//     res.json(selectedCustomer);
// });

// app.post('/api/customers', (req, res) => {
//     let postedCustomer = req.body;
//     let maxId = Math.max.apply(Math,customers.map((cust) => cust.id));
//     postedCustomer.id = ++maxId;
//     postedCustomer.gender = (postedCustomer.id % 2 === 0) ? 'female' : 'male';
//     customers.push(postedCustomer);
//     res.json(postedCustomer);
// });

// app.put('/api/customers/:id', (req, res) => {
//     let putCustomer = req.body;
//     let id = +req.params.id;
//     let status = false;

//     //Ensure state name is in sync with state abbreviation 
//     const filteredStates = states.filter((state) => state.abbreviation === putCustomer.state.abbreviation);
//     if (filteredStates && filteredStates.length) {
//         putCustomer.state.name = filteredStates[0].name;
//         console.log('Updated putCustomer state to ' + putCustomer.state.name);
//     }

//     for (let i=0,len=customers.length;i<len;i++) {
//         if (customers[i].id === id) {
//             customers[i] = putCustomer;
//             status = true;
//             break;
//         }
//     }
//     res.json({ status: status });
// });

// app.delete('/api/customers/:id', function(req, res) {
//     let customerId = +req.params.id;
//     for (let i=0,len=customers.length;i<len;i++) {
//         if (customers[i].id === customerId) {
//            customers.splice(i,1);
//            break;
//         }
//     }  
//     res.json({ status: true });
// });

// app.get('/api/orders/:id', function(req, res) {
//     let customerId = +req.params.id;
//     for (let cust of customers) {
//         if (cust.customerId === customerId) {
//             return res.json(cust);
//         }
//     }
//     res.json([]);
// });

// app.get('/api/states', (req, res) => {
//     res.json(states);
// });

// app.post('/api/auth/login', (req, res) => {
//     var userLogin = req.body;
//     //Add "real" auth here. Simulating it by returning a simple boolean.
//     res.json(true);
// });

// app.post('/api/auth/logout', (req, res) => {
//     res.json(true);
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

// redirect all others to the index (HTML5 history)
app.all('/*', function (req, res) {
    res.sendFile(__dirname + '/dist/index.html');
});

app.listen(3004);

console.log('Express listening on port 3004.');

//Open browser
var opn = require('opn');

opn('http://localhost:3004').then(() => {
    console.log('Browser closed.');
});


