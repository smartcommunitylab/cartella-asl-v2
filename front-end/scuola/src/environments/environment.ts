// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  globalSpinner : true,
  modificationFlag : false,
  serverAPIURL: 'http://localhost:4040/cartella-asl/api',
  defaultPosition: { 'latitude': 46.1025748, 'longitude': 10.927261 },
  aacClientId: "c_37b06a4d-9ca5-47bb-8630-283249916cdd",
  redirectUrl: "http://localhost:4200/",
  logout_redirect: 'asl-login/',
  scope: "profile openid email profile.accountprofile.me profile.adc.me",
  aacUrl: "https://aac.platform.smartcommunitylab.it"
};
