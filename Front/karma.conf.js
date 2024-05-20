module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    client: {
      jasmine: {},
      clearContext: false
    },
    jasmineHtmlReporter: {
      suppressAll: true
    },
    coverageReporter: {
      type: 'lcovonly',
      dir: 'coverage/',
      subdir: '.',
      file: 'lcov.info',
      exclude: ['src/app/model/**', 'src/app/open-api/**', 'src/app/environments/**', '**/*.html', '**/*.scss', 'src/app/layout/**', 'src/app/assets/**','src/assets/**'
      ,'src/environments/**','src/open-api/**','src/**.ts','src/**.json']
    },
    reporters: ['progress', 'coverage', 'kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    singleRun: false,
    restartOnFileChange: true
  });
};
