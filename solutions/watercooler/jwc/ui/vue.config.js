module.exports = {
  outputDir: 'target/dist',
  assetsDir: 'static',
  devServer: {
    proxy: {
      '/jwc': {
        target: 'http://localhost:8080',
        logLevel: 'debug'
      },
    }
  },
  css: {
    loaderOptions: {
      scss: {
        prependData: `@import "@/assets/css/variables.scss";`
      }
    }
  }
};
