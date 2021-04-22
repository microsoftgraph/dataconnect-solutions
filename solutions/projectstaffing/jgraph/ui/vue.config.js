module.exports = {
  devServer: {
    proxy: {
      '/gdc': {
        target: 'http://localhost:7655/',
        logLevel: 'debug'
      },
      // Change target field for local development environment. Leaving it empty will result in missing logged in user name
      // '/.auth/me': {
      // target: '',
      // logLevel: 'debug'
      // },
      '/ws': {
        target: 'http://localhost:7655/',
        ws: true,
        changeOrigin: true,
        logLevel: 'debug'
      }
    }
  },
  outputDir: 'target/dist',
  assetsDir: 'static',
  css: {
    loaderOptions: {
      scss: {
        additionalData: `@import "@/assets/css/variables.scss";`
      }
    }
  }
};
