package com.oledpaper.app

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private val Purple = Color(0xFF9B42FF)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var prompt by remember { mutableStateOf("") }
            var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
            var status by remember { mutableStateOf("Descreva o anime, personagem e pose.") }
            var busy by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            MaterialTheme(colorScheme = darkColorScheme(primary = Purple, background = Color.Black, surface = Color(0xFF101010))) {
                Column(Modifier.fillMaxSize().background(Color.Black).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("OLEDPaper", style = MaterialTheme.typography.headlineLarge, color = Purple)
                    Text("Anime. Preto. Perfeito.", color = Color.LightGray)
                    Image(painterResource(R.drawable.brand), contentDescription = "Marca OLEDPaper", modifier = Modifier.fillMaxWidth().height(170.dp), contentScale = ContentScale.Fit)
                    OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text("Qual wallpaper você quer?") }, placeholder = { Text("Ex.: guerreiro anime de cabelo branco flutuando, aura roxa") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    Button(onClick = {
                        busy = true; status = "Gerando imagem..."
                        scope.launch {
                            try {
                                imageBytes = withContext(Dispatchers.IO) { generate(prompt) }
                                status = "Wallpaper pronto!"
                            } catch (e: Exception) { status = "Erro: ${e.message}" }
                            finally { busy = false }
                        }
                    }, enabled = prompt.isNotBlank() && !busy, modifier = Modifier.fillMaxWidth()) { Text(if (busy) "Gerando..." else "Gerar wallpaper OLED") }
                    Text(status, color = Color.LightGray)
                    imageBytes?.let { bytes ->
                        val bitmap = remember(bytes) { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }
                        if (bitmap != null) {
                            Image(bitmap.asImageBitmap(), contentDescription = "Wallpaper gerado", modifier = Modifier.fillMaxWidth().height(440.dp), contentScale = ContentScale.Fit)
                            Button(onClick = {
                                try { WallpaperManager.getInstance(this@MainActivity).setBitmap(bitmap); status = "Wallpaper aplicado!" }
                                catch (e: Exception) { status = "Não foi possível aplicar: ${e.message}" }
                            }, modifier = Modifier.fillMaxWidth()) { Text("Aplicar como papel de parede") }
                        }
                    }
                    Text("Sempre preto OLED • IA gera cada imagem sob demanda", color = Color.Gray)
                }
            }
        }
    }
}

private fun generate(prompt: String): ByteArray {
    val connection = (URL("${BuildConfig.API_BASE_URL}/generate").openConnection() as HttpURLConnection)
    connection.requestMethod = "POST"
    connection.connectTimeout = 30000
    connection.readTimeout = 180000
    connection.doOutput = true
    connection.setRequestProperty("Content-Type", "application/json")
    connection.outputStream.use { it.write(JSONObject().put("prompt", prompt).toString().toByteArray(Charsets.UTF_8)) }
    val status = connection.responseCode
    val body = (if (status in 200..299) connection.inputStream else connection.errorStream).bufferedReader().use { it.readText() }
    connection.disconnect()
    if (status !in 200..299) throw Exception(JSONObject(body).optString("error", "HTTP $status"))
    return Base64.decode(JSONObject(body).getString("image_base64"), Base64.DEFAULT)
}
