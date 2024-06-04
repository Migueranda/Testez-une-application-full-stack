import { defineConfig } from 'cypress'

export default defineConfig({
  videosFolder: 'cypress/videos',
  screenshotsFolder: 'cypress/screenshots',
  fixturesFolder: 'cypress/fixtures',
  video: false,
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    setupNodeEvents(on, config) {
      return require('./cypress/plugins/index.ts').default(on, config)
    },
    baseUrl: 'http://localhost:4200',
  },
})


// import { defineConfig } from 'cypress'

// export default defineConfig({
//   // setupNodeEvents can be defined in either
//   // the e2e or component configuration
//     videosFolder: 'cypress/videos',
//     screenshotsFolder: 'cypress/screenshots',
//     fixturesFolder: 'cypress/fixtures',
//     video: false,
//   e2e: {
//     setupNodeEvents(on, config) {
//       require('@cypress/code-coverage/task')(on, config)
//       // include any other plugin code...

//       // It's IMPORTANT to return the config object
//       // with any changed environment variables
//       return config
//     },
//   },
// })