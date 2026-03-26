package app.textpilot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.textpilot.theme.CoreplyTheme
import app.textpilot.data.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoreplyTheme {
                SplashScreen {
                    lifecycleScope.launch {
                        val prefs = PreferencesManager.getInstance(this@SplashActivity)
                        prefs.loadPreferences()
                        
                        if (prefs.onboardingCompletedState.value) {
                            startActivity(Intent(this@SplashActivity, SettingsActivity::class.java))
                        } else {
                            val intent = Intent(this@SplashActivity, WelcomeActivity::class.java)
                            intent.putExtra("page", 0)
                            startActivity(intent)
                        }
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Simpler implementation for robust animation
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(1000)
            )
        }
        
        delay(2500) // Splash duration
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00A3FF), // Electric Blue from logo
                        Color(0xFF7D26CD)  // Vibrant Purple from logo
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Text Pilot",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.alpha(alpha.value)
            )
            
            Text(
                text = "Magic in every reply",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}
