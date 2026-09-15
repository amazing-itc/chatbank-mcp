/**
 * Tour do ChatBank MCP Kit (laboratório de uso).
 * Uso: node record-demos.mjs
 * Opcional: $env:DEMO_APP_URL = "file:///.../lab.html"
 */
import { chromium } from 'playwright'
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath, pathToFileURL } from 'node:url'

const OUT = path.dirname(fileURLToPath(import.meta.url))
const BASE = process.env.DEMO_APP_URL ?? pathToFileURL(path.join(OUT, 'lab.html')).href

async function sleep(ms) {
  await new Promise((r) => setTimeout(r, ms))
}

async function recordClip(name, navigateFn) {
  const videoDir = path.join(OUT, 'raw', name)
  fs.mkdirSync(videoDir, { recursive: true })
  const browser = await chromium.launch({ headless: true, args: ['--disable-dev-shm-usage'] })
  const context = await browser.newContext({
    viewport: { width: 1440, height: 900 },
    recordVideo: { dir: videoDir, size: { width: 1440, height: 900 } },
    locale: 'pt-BR',
  })
  const page = await context.newPage()
  try {
    await page.goto(BASE, { waitUntil: 'domcontentloaded' })
    await sleep(1800)
    await navigateFn(page)
  } finally {
    await context.close()
    await browser.close()
  }
  const webm = fs.readdirSync(videoDir).find((f) => f.endsWith('.webm'))
  if (!webm) throw new Error(`Sem video: ${name}`)
  fs.copyFileSync(path.join(videoDir, webm), path.join(OUT, `${name}.webm`))
  console.log(`ok ${name}.webm`)
}

async function demoConectar(page) {
  await page.getByRole('heading', { name: 'Conectar o host MCP' }).waitFor()
  await sleep(2200)
  await page.getByRole('button', { name: 'Connect' }).click()
  await page.getByText('chatbank-mcp 0.1.0').waitFor()
  await sleep(3200)
}

async function demoSaldo(page) {
  await page.getByRole('button', { name: '2. Consultar saldo' }).click()
  await sleep(2200)
  await page.getByRole('button', { name: 'Run tool' }).click()
  await page.getByText('Ana Souza').waitFor()
  await sleep(3600)
}

async function demoExtrato(page) {
  await page.getByRole('button', { name: '3. Consultar extrato' }).click()
  await sleep(2200)
  await page.getByRole('button', { name: 'Run tool' }).click()
  await page.getByText('car-018').waitFor()
  await sleep(4000)
}

async function demoErro(page) {
  await page.getByRole('button', { name: '4. Conta inválida' }).click()
  await sleep(2000)
  await page.getByRole('button', { name: 'Run tool' }).click()
  await page.getByText('Conta não encontrada').waitFor()
  await sleep(3200)
}

async function demoAdapter(page) {
  await page.getByRole('button', { name: '5. Plugar o banco' }).click()
  await page.getByRole('heading', { name: 'Plugar o seu banco' }).waitFor()
  await sleep(4200)
}

async function demoTour(page) {
  await demoConectar(page)
  await demoSaldo(page)
  await demoExtrato(page)
  await demoErro(page)
  await demoAdapter(page)
}

const clips = [
  ['00-tour-completo', demoTour],
  ['01-conectar', demoConectar],
  ['02-consultar-saldo', demoSaldo],
  ['03-consultar-extrato', demoExtrato],
  ['04-conta-invalida', demoErro],
  ['05-plugar-banco', demoAdapter],
]

for (const [name, fn] of clips) {
  await recordClip(name, fn)
}
