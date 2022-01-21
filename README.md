### EDIT Backend
Realizzato con Java 1.8;
Utilizza framework Spring (Web, JPA);
Packaging con Apache Maven;
Viene eseguito come applicazione Java standalone (Spring Boot); è associato all’esecuzione del servizio ‘cartella-asl’ (/usr/lib/jvm/java-1.8.0/bin/java -Xmx4096m 
-jar /home/dev/cartella-aslv2/cartella-asl-v2/back-end/target/cartella-asl-2.0.jar).
espone API REST su HTTP per il frontend. 

Cartella di riferimento: /back-end
File dipendenze: pom.xml
File configurazioni: 
src\main\resources\application.yml
src\main\resources\logback.xml

### EDIT Frontend
Tutte le app frontend sono 
Realizzate con Angular Framework (versione 6)
Packaging con NPM
Viene utilizzata una funzionalità di geocoding, per convertire un indirizzo in una posizione geografica e viceversa, utilizzando le API esposte dal servizio 
https://os.smartcommunitylab.it/core.geocoder/location

### EDIT Scuola
Web-app di gestione alternanza per gli Istituti
Cartella di riferimento: /front-end/scuola
File dipendenze: package.json
File configurazioni: 
src/environments/environment.ts

### EDIT Ente
Web-app di gestione delle esperienze per gli enti/aziende
Cartella di riferimento: /front-end/azienda
File dipendenze: package.json
File configurazioni: 
src/environments/environment.ts

### EDIT Ruoli
Web-app di amministrazione dei ruoli e consultazione di informazioni utili all'amministrazione del sistema.
Cartella di riferimento: /front-end/ruoli
File dipendenze: package.json
File configurazioni: 
src/environments/environment.ts

### EDIT Registrazione ente
Web-app per la procedura di registrazione dei ruoli per gli enti/aziende.
Cartella di riferimento: /front-end/registrazione-ente
File dipendenze: package.json
File configurazioni: 
src/environments/environment.ts

### EDIT login
Web-app per la landing-page.
Cartella di riferimento: /front-end/login
File dipendenze: package.json
File configurazioni: 
src/environments/environment.ts
