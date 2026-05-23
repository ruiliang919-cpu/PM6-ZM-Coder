const isDev = process.env.NODE_ENV === 'development'
export default {
  log: (...args) => isDev && console.log(...args),
  warn: (...args) => isDev && console.warn(...args),
  error: (...args) => console.error(...args)  // errors always log
}
