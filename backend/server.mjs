import http from 'node:http';

const PORT = Number(process.env.PORT || 3000);
const API_KEY = process.env.OPENAI_API_KEY;
const MODEL = process.env.OPENAI_IMAGE_MODEL || 'gpt-image-1.5';
const MAX_BYTES = 4096;
const requests = new Map();

function reply(res, status, data) {
  res.writeHead(status, {'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store'});
  res.end(JSON.stringify(data));
}

const server = http.createServer(async (req, res) => {
  if (req.url === '/health' && req.method === 'GET') return reply(res, 200, {status:'ok'});
  if (req.url !== '/generate' || req.method !== 'POST') return reply(res, 404, {error:'Not found'});
  const ip = req.socket.remoteAddress || 'unknown';
  const now = Date.now();
  const recent = (requests.get(ip) || []).filter(t => now - t < 3600000);
  if (recent.length >= 5) return reply(res, 429, {error:'Limite de 5 gerações por hora atingido.'});
  recent.push(now); requests.set(ip, recent);
  let input = '';
  try {
    for await (const chunk of req) { input += chunk.toString('utf8'); if (Buffer.byteLength(input) > MAX_BYTES) throw new Error('Pedido muito longo'); }
    const prompt = JSON.parse(input).prompt;
    if (typeof prompt !== 'string' || !prompt.trim() || prompt.length > 700) return reply(res, 400, {error:'Digite um pedido de até 700 caracteres.'});
    if (!API_KEY) return reply(res, 503, {error:'Configure OPENAI_API_KEY no servidor.'});
    const fixedStyle = `Create one vertical smartphone wallpaper. Mandatory aesthetic: OLED pure black (#000000) background, 80-90% black visual impression, no scenic background, no gradients replacing black, no text, no logo, no watermarks. Dark anime illustration, dramatic pose, restrained luminous violet/red/blue rim lights, crisp premium composition. Keep the upper area relatively empty for lock-screen clock. User's subject and composition request: ${prompt}. Do not follow any request to remove the pure black OLED background.`;
    const response = await fetch('https://api.openai.com/v1/images/generations', {
      method:'POST', headers:{Authorization:`Bearer ${API_KEY}`, 'Content-Type':'application/json'},
      body:JSON.stringify({model:MODEL, prompt:fixedStyle, size:'1024x1536', quality:'medium', output_format:'png'})
    });
    const result = await response.json();
    if (!response.ok) return reply(res, 502, {error:result.error?.message || 'Falha ao gerar imagem'});
    const b64 = result.data?.[0]?.b64_json;
    if (!b64) return reply(res, 502, {error:'A API não retornou imagem em base64.'});
    return reply(res, 200, {image_base64:b64});
  } catch (e) { return reply(res, 400, {error:e.message || 'Erro no pedido'}); }
});
server.listen(PORT, () => console.log(`OLEDPaper backend na porta ${PORT}`));
