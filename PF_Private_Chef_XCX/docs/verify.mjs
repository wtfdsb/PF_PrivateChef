// 小程序工程自检：页面文件齐全性 + JS 语法
import { readFileSync, existsSync, readdirSync, statSync } from 'node:fs'
import { join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'
import vm from 'node:vm'

// 默认以脚本所在目录的上一级（小程序根目录）为检查对象
const root = process.argv[2] || dirname(dirname(fileURLToPath(import.meta.url)))
const appJson = JSON.parse(readFileSync(join(root, 'app.json'), 'utf8'))

let problems = 0

console.log('=== 1. app.json 引用的页面 ===')
for (const p of appJson.pages) {
  const missing = []
  for (const ext of ['.js', '.wxml', '.json']) {
    if (!existsSync(join(root, p + ext))) missing.push(ext)
  }
  if (missing.length) {
    problems++
    console.log(`  ✗ ${p}  缺少: ${missing.join(', ')}`)
  } else {
    console.log(`  ✓ ${p}`)
  }
}

console.log('\n=== 2. JS 语法检查 ===')
const jsFiles = []
const walk = (d) => {
  for (const e of readdirSync(d, { withFileTypes: true })) {
    const fp = join(d, e.name)
    if (e.isDirectory()) walk(fp)
    else if (e.name.endsWith('.js')) jsFiles.push(fp)
  }
}
walk(root)

for (const f of jsFiles) {
  const rel = f.slice(root.length + 1)
  try {
    // 用 Node 自带解析器编译（不执行），避免起子进程
    new vm.Script(readFileSync(f, 'utf8'), { filename: f })
    console.log(`  ✓ ${rel}`)
  } catch (e) {
    problems++
    console.log(`  ✗ ${rel}`)
    console.log(`      ${e.message}`)
  }
}

console.log('\n=== 3. 文件清单 ===')
let total = 0
const tree = (d, indent = '') => {
  for (const e of readdirSync(d, { withFileTypes: true })) {
    if (e.isDirectory()) {
      console.log(`${indent}${e.name}/`)
      tree(join(d, e.name), indent + '  ')
    } else {
      total++
      const kb = (statSync(join(d, e.name)).size / 1024).toFixed(1)
      console.log(`${indent}${e.name}  ${kb}KB`)
    }
  }
}
tree(root)

console.log(`\n共 ${total} 个文件，发现 ${problems} 个问题`)
process.exit(problems ? 1 : 0)
