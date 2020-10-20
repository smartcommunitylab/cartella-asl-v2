## Building application.

*  DEV Mode (default)
	mvn -f azienda\front-end\pom.xml clean package

*  Production mode
	mvn -f azienda\front-end\pom.xml clean package -P prod

 
## Install the Application

1. Install `Node.js 6.11` or higher. *IMPORTANT: The server uses ES2015 features AND the Angular CLI so you need Node 6.11 or higher!!!!*

1. Run `npm install` to install app dependencies

1. Run `npm install -g @angular/cli` to install angular CLI

l. run `npm install bootstrap`

1. run  'npm install ng2-date-picker --save'

1. run 'npm install @ngx-pwa/local-storage'

1. Download bootstrap.css from <https://bootswatch.com/4/materia/bootstrap.css> and paste it inside node_modules/bootstrap/dist/css/bootstrap.css
    -> link of reference <https://bootswatch.com/4/materia/>

1. Run `ng build --watch` to build and bundle the code

1. Run `npm start` in a separate terminal window to build the TypeScript, watch for changes and launch the web server

1. Go to http://localhost:3004 in your browser 






