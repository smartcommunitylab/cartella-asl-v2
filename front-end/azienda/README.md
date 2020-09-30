## Building application.

*  DEV Mode (default)
	mvn -f azienda\pom.xml clean package

*  Production mode
	mvn -f azienda\pom.xml clean package -P prod

## Running the Application
1. Install `Node.js 12.13.1` or higher. *IMPORTANT: The server uses ES2015 features AND the Angular CLI so you need Node 6.11 or higher!!!!*

1. Run `mvn clean package` from project root to install app dependencies

2. Run `ng serve` in a separate terminal window to build the TypeScript, watch for changes and launch the web server

1. Go to http://localhost:4200 in your browser 