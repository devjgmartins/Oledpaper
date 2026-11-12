# OLEDPaper — protótipo Android + backend

## O que funciona
- Tela Android em Kotlin/Jetpack Compose, tema preto OLED, identidade visual da marca.
- Usuário escreve um pedido livre; backend acrescenta regras fixas de fundo preto OLED.
- API de imagens da OpenAI; exibição do resultado; aplicação como wallpaper Android.
- Chave da OpenAI permanece no backend (nunca no APK).

## Executar backend (Node.js 20+)
No terminal, dentro de `backend`:

Linux/macOS:
```
export OPENAI_API_KEY='sua-chave'
node server.mjs
```
Windows PowerShell:
```
$env:OPENAI_API_KEY='sua-chave'
node server.mjs
```

A API estará em `http://localhost:3000` e `GET /health` responde com status.

## Compilar aplicativo
1. Instale Android Studio (JDK 17, SDK Android 35).
2. Abra a pasta `android` como projeto.
3. Aguarde o Gradle sincronizar (Android Studio pode baixar os componentes necessários).
4. Execute no emulador Android. O endereço `10.0.2.2:3000` no build debug aponta para o backend no computador.
5. Para testar em telefone real, troque `API_BASE_URL` em `android/app/build.gradle.kts` pelo endereço HTTPS público do seu backend; não use `localhost`.
6. Compile via **Build > Build APK(s)**; para Play Store, gere um **Android App Bundle (AAB)** assinado.

## Antes de publicar
- Hospedar o backend em HTTPS e alterar URL release.
- Implementar autenticação e cotas reais por usuário (limite por IP atual é apenas demonstrativo e não é suficiente contra abuso).
- Adicionar moderação e políticas de uso, privacidade, termos, exclusão de dados e licenças dos personagens/imagens.
- Testar em dispositivos reais, remover `usesCleartextTraffic` em produção, configurar ícone adaptativo, screenshots, assinatura, billing se necessário.
- Validar a versão/modelo e preços da API de imagens disponíveis na sua conta OpenAI.
- A API pode gerar fundo quase preto, mas não garante todos os pixels #000000; um pós-processamento de segmentação seria necessário para garantir isso sem afetar o personagem.

## Estado
Código-fonte inicial fornecido. APK/AAB não incluídos: ambiente de geração não tem Android SDK/Gradle para compilar ou validar o build.
