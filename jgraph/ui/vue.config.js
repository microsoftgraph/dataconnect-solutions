module.exports = {
  devServer: {
    proxy: {
      '/gdc': {
        target: 'http://localhost:7655/',
        logLevel: 'debug'
      },
      '/.auth/me': {
        target: 'https://gdc-jgraph.azurewebsites.net/',
        logLevel: 'debug'
      },
      '/ws': {
        target: 'http://localhost:7655/',
        ws: true,
        changeOrigin: true,
        logLevel: 'debug',
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
