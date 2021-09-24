// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
    production: false,
    globalSpinner : true,
    profileFlag : false,
    serverAPIURL: 'https://cartella-asl-demo.platform.smartcommunitylab.it/cartella-asl/api',
    defaultPosition: { 'latitude': 46.1025748, 'longitude': 10.927261 },
    aacClientId: "c_37b06a4d-9ca5-47bb-8630-283249916cdd",
    redirectUrl: "https://cartella-asl-demo.platform.smartcommunitylab.it/asl-registrazione-ente-v2/",
    logout_redirect: 'asl-login/',
    scope: "profile openid email",
    aacUrl: "https://aac-adc.platform.smartcommunitylab.it"
  };
  