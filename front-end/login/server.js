"use strict";

var express     = require('express'),
    bodyParser  = require('body-parser'),
    fs          = require('fs'),
    app         = express()
    //attivitaTipologie   = JSON.parse(fs.readFileSync('data/attivitaTipologie.json', 'utf-8'))
    
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

//The dist folder has our static resources (index.html, css, images)
app.use(express.static(__dirname + '/dist'));
/*
app.get('/api/tipologie', (req, res) => {
    res.json(attivitaTipologie);
});*/

// redirect all others to the index (HTML5 history)
app.all('/*', function (req, res) {
    res.sendFile(__dirname + '/dist/index.html');
});

app.listen(3006);

console.log('Express listening on port 3006.');

//Open browser
var opn = require('opn');

opn('http://localhost:3006').then(() => {
    console.log('Browser closed.');
});


