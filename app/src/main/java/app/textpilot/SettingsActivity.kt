package app.textpilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text as ComposeText
import app.textpilot.ui.compose.ModernSettingsScreen
import app.textpilot.theme.CoreplyTheme
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.IconButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Created on 12/24/16.
 * Updated to use Jetpack Compose
 */
class SettingsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            CoreplyTheme {
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            scrollBehavior = scrollBehavior,
                            navigationIcon = {
                                Icon(
                                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                                    contentDescription = "App Icon",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            title = {
                                ComposeText("Text Pilot", maxLines = 1,fontWeight = FontWeight.Black,)
                            },
                            actions = {
                                IconButton(onClick = {
                                    val intent = Intent(this@SettingsActivity, AboutActivity::class.java)
                                    startActivity(intent)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "About & Support"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ModernSettingsScreen()
                    }
                }
            }
        }
    }
}
