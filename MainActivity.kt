package com.radmehrtrading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin

private val Bg = Color(0xFF060B12)
private val CardBg = Color(0xFF0E1722)
private val Card2 = Color(0xFF111E2B)
private val Green = Color(0xFF36D879)
private val Red = Color(0xFFFF5C68)
private val Gold = Color(0xFFF5C451)
private val Cyan = Color(0xFF42D9FF)
private val Purple = Color(0xFFA875FF)
private val Muted = Color(0xFF8FA1B5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RadmehrTradingApp() }
    }
}

@Composable
fun RadmehrTradingApp() {
    var tab by remember { mutableIntStateOf(0) }
    var symbol by remember { mutableStateOf("BTC/USDT") }
    var timeframe by remember { mutableStateOf("1H") }
    var scanning by remember { mutableStateOf(false) }
    var showAll by remember { mutableStateOf(false) }

    LaunchedEffect(scanning) {
        if (scanning) {
            delay(1800)
            scanning = false
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = CardBg, primary = Green)) {
            Column(Modifier.fillMaxSize().background(Bg)) {
                TopBar(tab)
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    when (tab) {
                        0 -> HomeScreen(symbol, timeframe, scanning, { symbol = it }, { timeframe = it }, { scanning = true })
                        1 -> MarketScreen(symbol, { symbol = it })
                        2 -> SignalsScreen(showAll, { showAll = !showAll })
                        3 -> NewsScreen()
                        else -> SettingsScreen()
                    }
                }
                BottomBar(tab) { tab = it }
            }
        }
    }
}

@Composable
private fun TopBar(tab: Int) {
    val titles = listOf("خانه", "بازار", "سیگنال‌ها", "اخبار", "تنظیمات")
    Row(
        Modifier.fillMaxWidth().background(Color(0xFF09111A)).padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("RadmehrTrading", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(titles[tab], fontSize = 12.sp, color = Muted)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            BadgeDot("●", Green)
            Text("بازار آنلاین", fontSize = 12.sp, color = Green)
        }
    }
}

@Composable
private fun HomeScreen(symbol: String, timeframe: String, scanning: Boolean, onSymbol: (String) -> Unit, onTf: (String) -> Unit, onScan: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(symbol, { onSymbol(it.uppercase()) }, Modifier.weight(1f), singleLine = true, label = { Text("نماد") })
                    Column(horizontalAlignment = Alignment.End) {
                        Text("قیمت مرجع", color = Muted, fontSize = 11.sp)
                        Text("66,247.89", color = Green, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("+1.92%", color = Green, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("1M", "5M", "15M", "1H", "4H", "1D").forEach { tf ->
                        FilterChip(selected = timeframe == tf, onClick = { onTf(tf) }, label = { Text(tf) })
                    }
                }
                Spacer(Modifier.height(10.dp))
                MarketChart()
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("قدرت سیگنال", "93%", Green, Modifier.weight(1f))
            MetricCard("ریسک/بازده", "1 : 2.8", Gold, Modifier.weight(1f))
            MetricCard("روند", "صعودی", Cyan, Modifier.weight(1f))
        }

        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF092117)), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("سیگنال پیشنهادی", color = Muted, fontSize = 12.sp)
                        Text("خرید قوی", color = Green, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("BUY", color = Green, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                SignalRow("ناحیه ورود", "65,200 – 65,600")
                SignalRow("حد ضرر", "64,100")
                SignalRow("هدف اول", "66,000")
                SignalRow("هدف دوم", "67,200")
                SignalRow("هدف سوم", "68,500")
                Spacer(Modifier.height(12.dp))
                Text("تحلیل ترکیبی: ساختار بازار + مومنتوم + حمایت/مقاومت + مدیریت ریسک", color = Color.LightGray, fontSize = 12.sp)
            }
        }

        Button(onClick = onScan, Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp)) {
            Text(if (scanning) "در حال اسکن بازار…" else "⚡ اسکن و تحلیل فوری", fontSize = 16.sp)
        }

        Text("آخرین تحلیل‌ها", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        AnalysisItem("BTC/USDT", "خرید قوی", "+1.92%", Green)
        AnalysisItem("XAU/USD", "خرید", "+0.65%", Green)
        AnalysisItem("ETH/USDT", "مثبت", "+2.35%", Green)
    }
}

@Composable
private fun MarketChart() {
    val values = remember { List(52) { i -> 0.42f + i * 0.008f + sin(i * 0.48f) * 0.07f + if (i > 35) (i - 35) * 0.006f else 0f } }
    Canvas(Modifier.fillMaxWidth().height(230.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF08131C))) {
        val min = values.minOrNull() ?: 0f
        val max = values.maxOrNull() ?: 1f
        val path = Path()
        values.forEachIndexed { i, v ->
            val x = i * size.width / (values.size - 1)
            val y = size.height - ((v - min) / (max - min)) * (size.height * .82f) - size.height * .06f
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        repeat(5) { i -> drawLine(Color(0xFF132331), Offset(0f, i * size.height / 4), Offset(size.width, i * size.height / 4), 1f) }
        drawPath(path, Green, style = Stroke(width = 4f, cap = StrokeCap.Round))
        drawCircle(Green, 6f, Offset(size.width - 2f, size.height - ((values.last() - min) / (max - min)) * (size.height * .82f) - size.height * .06f))
    }
}

@Composable
private fun MarketScreen(symbol: String, onSymbol: (String) -> Unit) {
    val rows = listOf("BTC/USDT" to "+1.92%", "ETH/USDT" to "+2.35%", "BNB/USDT" to "+1.18%", "XAU/USD" to "+0.65%", "EUR/USD" to "-0.12%", "GBP/USD" to "-0.18%", "USD/JPY" to "+0.08%", "ADA/USDT" to "+3.25%")
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(symbol, { onSymbol(it.uppercase()) }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("جستجوی نماد") })
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) { listOf("شاخص‌ها", "کالاها", "فارکس", "کریپتو").forEach { AssistChip(onClick = {}, label = { Text(it) }) } }
        rows.forEachIndexed { i, (name, change) ->
            val up = !change.startsWith("-")
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CardBg).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(listOf("₿", "Ξ", "◉", "◈", "€", "£", "$", "₳")[i], fontSize = 25.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) { Text(name, fontWeight = FontWeight.Bold); Text(listOf("Bitcoin", "Ethereum", "BNB", "Gold", "Euro / Dollar", "Pound / Dollar", "Dollar / Yen", "Cardano")[i], color = Muted, fontSize = 11.sp) }
                Column(horizontalAlignment = Alignment.End) { Text(listOf("66,247.89", "3,215.76", "596.48", "2,343.65", "1.0887", "1.2736", "156.72", "0.4687")[i]); Text(change, color = if (up) Green else Red, fontSize = 12.sp) }
            }
        }
    }
}

@Composable
private fun SignalsScreen(showAll: Boolean, toggle: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("سیگنال‌های هوشمند", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        SignalCard("BTC/USDT", "خرید قوی", "93%", "65,200–65,600", "64,100", "66,000 / 67,200 / 68,500", Green)
        SignalCard("XAU/USD", "خرید", "87%", "2,335–2,342", "2,322", "2,350 / 2,360", Gold)
        if (showAll) SignalCard("ETH/USDT", "خرید", "81%", "3,180–3,205", "3,130", "3,240 / 3,290", Cyan)
        OutlinedButton(onClick = toggle, Modifier.fillMaxWidth()) { Text(if (showAll) "نمایش کمتر" else "نمایش همه سیگنال‌ها") }
    }
}

@Composable
private fun SignalCard(name: String, action: String, confidence: String, entry: String, sl: String, tp: String, accent: Color) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(15.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text(action, color = accent, fontWeight = FontWeight.Bold) }
                Text(confidence, color = accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            SignalRow("Entry", entry); SignalRow("Stop", sl); SignalRow("TP", tp)
            Text("نسبت ریسک به بازده: 1 : 2.8", color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun NewsScreen() {
    val news = listOf(
        "تصمیم جدید فدرال رزرو می‌تواند بر بازار اثر بگذارد.",
        "افزایش تنش‌های ژئوپلیتیک بازارهای جهانی را تحت تأثیر قرار داد.",
        "حجم معاملات بیت‌کوین در ۲۴ ساعت اخیر افزایش یافت.",
        "دلار آمریکا در برابر چند ارز اصلی نوسان داشت.",
        "بانک مرکزی ژاپن نرخ بهره را بدون تغییر نگه داشت."
    )
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("اخبار و رویدادها", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        news.forEachIndexed { i, text ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(CardBg).padding(15.dp), verticalAlignment = Alignment.Top) {
                Text(if (i == 0) "⚡" else "▣", fontSize = 22.sp)
                Spacer(Modifier.width(10.dp))
                Column { Text("${i + 1}  |  ${listOf("۲ دقیقه", "۱۵ دقیقه", "۲۸ دقیقه", "۴۵ دقیقه", "۱ ساعت")[i]} پیش", color = Muted, fontSize = 11.sp); Text(text, fontWeight = FontWeight.Medium); Text(if (i < 2) "اقتصادی / فوری" else "بازار", color = Cyan, fontSize = 11.sp) }
            }
        }
    }
}

@Composable
private fun SettingsScreen() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("تنظیمات", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        SettingRow("🔔", "هشدار تغییر روند", "فعال")
        SettingRow("🛡", "مدیریت ریسک", "حرفه‌ای")
        SettingRow("🤖", "موتور تحلیل هوشمند", "فعال")
        SettingRow("🌐", "منبع داده بازار", "قابل اتصال")
        SettingRow("📊", "تایم‌فریم پیش‌فرض", "1H")
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF15120B))) {
            Column(Modifier.padding(15.dp)) {
                Text("نسخه برنامه", color = Gold, fontWeight = FontWeight.Bold)
                Text("RadmehrTrading 1.0.0", fontSize = 18.sp)
                Text("نسخه حرفه‌ای اولیه – آماده توسعه اتصال API و داده زنده", color = Muted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun BottomBar(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf("خانه" to "⌂", "بازار" to "▥", "سیگنال‌ها" to "ϟ", "اخبار" to "▤", "بیشتر" to "•••")
    Row(Modifier.fillMaxWidth().background(Color(0xFF09111A)).padding(vertical = 7.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        items.forEachIndexed { i, (label, icon) ->
            Column(Modifier.clip(RoundedCornerShape(12.dp)).clickable { onSelect(i) }.padding(horizontal = 11.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(icon, color = if (selected == i) Green else Muted, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(label, color = if (selected == i) Green else Muted, fontSize = 10.sp)
            }
        }
    }
}

@Composable private fun BadgeDot(text: String, color: Color) { Text(text, color = color, fontSize = 10.sp) }

@Composable private fun MetricCard(title: String, value: String, accent: Color, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(title, color = Muted, fontSize = 9.sp); Text(value, color = accent, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
    }
}

@Composable private fun SignalRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = Muted, fontSize = 12.sp); Text(value, fontWeight = FontWeight.Medium, fontSize = 12.sp) }
}

@Composable private fun AnalysisItem(name: String, action: String, change: String, color: Color) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CardBg).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(name, fontWeight = FontWeight.Bold, Modifier.weight(1f)); Text(action, color = color, fontSize = 12.sp); Spacer(Modifier.width(10.dp)); Text(change, color = color, fontSize = 12.sp)
    }
}

@Composable private fun SettingRow(icon: String, title: String, value: String) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(CardBg).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 21.sp); Spacer(Modifier.width(12.dp)); Text(title, Modifier.weight(1f), fontWeight = FontWeight.Medium); Text(value, color = Green, fontSize = 12.sp)
    }
}
