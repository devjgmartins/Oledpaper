# Gerar APK do OLEDPaper pelo celular usando GitHub

## Opção mais fácil (upload pelo navegador)
1. Crie um repositório **privado** em https://github.com/new com nome `OLEDPaper`.
2. No navegador, abra o repositório e escolha **Add file > Upload files**. Para enviar pastas preservando a estrutura, use um gerenciador Git no celular ou a opção de upload de pasta, se disponível no navegador. **Não envie apenas o ZIP sem descompactar**: o GitHub Actions precisa dos arquivos na raiz do repositório.
3. Confirme que na raiz estão `settings.gradle.kts`, `build.gradle.kts`, `app/` e `.github/workflows/android-apk.yml`.
4. Vá em **Actions > Compilar OLEDPaper APK > Run workflow** e confirme.
5. Ao terminar, abra a execução e baixe o artefato **OLEDPaper-debug-APK**. Extraia o ZIP baixado e instale `app-debug.apk` no Android.

## Alternativa mais confiável no celular
Use um aplicativo Git que permita criar commit e enviar toda a pasta extraída para o repositório, incluindo a pasta oculta `.github`. Outra opção é usar Git no Termux.

## Atenção
- Este APK é **debug**, para testes. Não é o AAB assinado para Play Store.
- A compilação não configura o backend da IA. No celular real, a URL padrão `http://10.0.2.2:3000` aponta para o emulador e **não funcionará**. Configure um servidor HTTPS e altere `API_BASE_URL` em `app/build.gradle.kts`, inclusive no tipo `debug`, antes de compilar.
- Nunca inclua a chave OpenAI no app ou no repositório.
- Se a compilação falhar, abra o log da etapa vermelha no Actions e compartilhe o erro.
