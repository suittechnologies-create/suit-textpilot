package app.textpilot

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import app.textpilot.data.PreferencesManager
import app.textpilot.theme.CoreplyTheme
import app.textpilot.utils.GlobalPref.isAccessibilityEnabled
import kotlinx.coroutines.launch

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val page = intent.getIntExtra("page", 0)
        
        setContent {
            CoreplyTheme {
                WelcomeScreen(
                    page = page,
                    onFinish = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    page: Int,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Allow Restricted Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Click the greyed out option (important), then go to Settings > Apps > Text Pilot. 'Allow restricted settings' is somewhere in the app info screen.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                val videoUri = Uri.parse("android.resource://${ctx.packageName}/${R.raw.restricted_setting_tut}")
                                setVideoURI(videoUri)
                                setOnPreparedListener { mp -> mp.isLooping = true }
                                start()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )

                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background // AMOLED Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                val currentStep = when(page) {
                    0 -> 0
                    4 -> 1
                    5 -> 2
                    1 -> 3
                    2 -> 4
                    else -> 0
                }
                
                if (page in listOf(0, 4, 5, 1, 2)) {
                    PageIndicator(
                        pageCount = 5,
                        currentPage = currentStep,
                        modifier = Modifier.padding(top = 16.dp, bottom = 48.dp)
                    )
                }

                when (page) {
                    0 -> OnboardingContent(
                        emoji = "✨",
                        title = "Text Pilot",
                        description = "Experience the future of texting. AI that understands you and suggests perfect replies instantly.",
                        buttonText = "Get Started",
                        onNext = {
                            val intent = Intent(context, WelcomeActivity::class.java).apply { putExtra("page", 4) }
                            context.startActivity(intent)
                            onFinish()
                        }
                    )
                    4 -> OnboardingContent(
                        emoji = "📲",
                        title = "Works Everywhere",
                        description = "Whether it's WhatsApp, Instagram, or Telegram—Text Pilot works across all your favorite apps.",
                        buttonText = "Continue",
                        onNext = {
                            val intent = Intent(context, WelcomeActivity::class.java).apply { putExtra("page", 5) }
                            context.startActivity(intent)
                            onFinish()
                        }
                    )
                    5 -> OnboardingContent(
                        emoji = "🔑",
                        title = "Connect & Chat",
                        description = "Secure your conversations with Clerk authentication and enjoy a seamless AI experience.",
                        buttonText = "Setup App",
                        onNext = {
                            scope.launch {
                                PreferencesManager.getInstance(context).updateOnboardingCompleted(true)
                                val intent = Intent(context, WelcomeActivity::class.java).apply { putExtra("page", 1) }
                                context.startActivity(intent)
                                onFinish()
                            }
                        }
                    )
                    1 -> PermissionContent(
                        title = "Enable Overlay Permission",
                        description = "Text Pilot needs permission to display suggestions over other apps. This allows the app to show AI-powered text suggestions while you're typing in messaging apps.",
                        cardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        cardHorizontalAlignment = Alignment.CenterHorizontally,
                        cardContent = {
                            Text(text = "📱", style = MaterialTheme.typography.headlineLarge)
                            Text(
                                text = "This permission is safe and only used to show text suggestions",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        buttonContent = {
                            Button(
                                onClick = {
                                    context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                                    onFinish()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open Settings")
                            }
                        }
                    )
                    2 -> PermissionContent(
                        title = "Accessibility Service Disclosure",
                        description = "Text Pilot uses the Accessibility Service to read on-screen messages and locate text fields in messaging apps. This is necessary for providing inline context aware texting suggestions.",
                        cardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        cardContent = {
                            Text(
                                text = "✅ Step by Step Guide",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1. Open Accessibility Settings\n2. Select Text Pilot in the list of apps.\n3. Toggle on the switch",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        },
                        extraContent = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDialog = true }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Restricted setting? Click for help",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                        },
                        buttonContent = {
                            Button(
                                onClick = {
                                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                    onFinish()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("I Accept")
                            }
                        }
                    )
                    3 -> PermissionContent(
                        title = "Disable Accessibility Service",
                        description = "To turn off Text Pilot, you need to disable the accessibility service in your device settings.",
                        cardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        cardContent = {
                            Text(
                                text = "⚠️ Important",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Disabling the accessibility service will stop all Text Pilot features. You can re-enable it anytime from the app settings.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        },
                        buttonContent = {
                            Button(
                                onClick = {
                                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                    onFinish()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open Accessibility Settings")
                            }
                            TextButton(onClick = onFinish) {
                                Text("Cancel")
                            }
                        }
                    )
                    else -> onFinish()
                }
            }
        }
    }
}

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .width(if (isSelected) 32.dp else 8.dp)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
            )
        }
    }
}

@Composable
private fun ColumnScope.OnboardingContent(
    emoji: String,
    title: String,
    description: String,
    buttonText: String,
    onNext: () -> Unit
) {
    Spacer(modifier = Modifier.weight(0.3f))
    Surface(
        modifier = Modifier.size(160.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = emoji, style = MaterialTheme.typography.displayLarge)
        }
    }
    Spacer(modifier = Modifier.height(60.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = description,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 28.sp,
        modifier = Modifier.padding(horizontal = 24.dp)
    )
    Spacer(modifier = Modifier.weight(1f))
    Button(
        onClick = onNext,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(buttonText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ColumnScope.PermissionContent(
    title: String,
    description: String,
    cardColors: CardColors,
    cardHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    cardContent: @Composable ColumnScope.() -> Unit,
    extraContent: (@Composable ColumnScope.() -> Unit)? = null,
    buttonContent: @Composable ColumnScope.() -> Unit
) {
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 24.dp)
    )
    Spacer(modifier = Modifier.height(40.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = cardColors,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = cardHorizontalAlignment,
            content = cardContent
        )
    }
    extraContent?.invoke(this)
    Spacer(modifier = Modifier.weight(1f))
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = buttonContent
    )
}
