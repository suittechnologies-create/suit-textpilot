package app.textpilot.ui.compose

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import app.textpilot.AppSelectorActivity
import app.textpilot.WelcomeActivity
import app.textpilot.data.SuggestionPresentationType
import app.textpilot.ui.viewmodel.SettingsViewModel
import app.textpilot.utils.GlobalPref

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var expandMenu by remember { mutableStateOf(false) }
    val uiState = viewModel.uiState

    val suggestionPresentationTypeStrings =
        listOf("Bubble below text field only", "Inline only", "Bubble and inline")

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updateMasterSwitchState(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Service Section
        Text(
            text = "SERVICE",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.extraLarge, // Rounded iOS style
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Enable Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "AI Assistant", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Toggle the magic assistant",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.masterSwitchEnabled,
                        onCheckedChange = { enabled ->
                            val intent = Intent(context, WelcomeActivity::class.java)
                            intent.putExtra("page", if (enabled) 1 else 3)
                            context.startActivity(intent)
                        }
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                
                // Select Apps
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(context, AppSelectorActivity::class.java)
                            context.startActivity(intent)
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Target Apps", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Apps where AI is active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

                // Rewatch Intro
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(context, WelcomeActivity::class.java)
                            intent.putExtra("page", 0)
                            context.startActivity(intent)
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Rewatch Setup", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "See how Text Pilot works",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // 2. Preferences Section
        Text(
            text = "PREFERENCES",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Suggestion Mode
                Box(modifier = Modifier.padding(vertical = 12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = expandMenu,
                        onExpandedChange = { expandMenu = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = suggestionPresentationTypeStrings[uiState.suggestionPresentationType.ordinal],
                            readOnly = true,
                            onValueChange = {},
                            label = { Text("Visual Mode") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandMenu) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable, true),
                            shape = MaterialTheme.shapes.large
                        )
                        ExposedDropdownMenu(
                            expanded = expandMenu,
                            onDismissRequest = { expandMenu = false },
                        ) {
                            suggestionPresentationTypeStrings.forEachIndexed { index, selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        viewModel.updateSuggestionPresentationType(SuggestionPresentationType.fromInt(index))
                                        expandMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                
                // Show Errors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateShowErrors(!uiState.showErrors) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Debug Errors", style = MaterialTheme.typography.titleMedium)
                    Checkbox(
                        checked = uiState.showErrors,
                        onCheckedChange = { viewModel.updateShowErrors(it) }
                    )
                }
            }
        }



        // API Type Selection

        // API Configuration hidden from end-users



        // Quick Start Guide
        Column(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚀 Quick Start Guide",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Enable 'AI Assistant' above.\n2. Open any supported app (WhatsApp, etc.).\n3. Start typing - AI suggestions will appear magically!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }


        // About Section
        // About Section moved to separate page (impl coming next)



    }
}

@Composable
fun CustomApiSettingsSection(viewModel: SettingsViewModel) {
    val uiState = viewModel.uiState
    var showApiKey by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Custom API Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // API URL
        OutlinedTextField(

            value = uiState.customApiUrl,
            onValueChange = viewModel::updateCustomApiUrl,
            label = { Text("Base URL") },
            supportingText = { Text("OpenAI compatible API endpoint") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)

        )

        // API Key
        OutlinedTextField(
            value = uiState.customApiKey, onValueChange = viewModel::updateCustomApiKey,
            label = { Text("API Key") },
            supportingText = { Text("Your API authentication key") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            trailingIcon = {
                IconButton(onClick = {
                    showApiKey = !showApiKey
                }) {
                    Icon(
                        imageVector = if (showApiKey) Icons.Default.Lock else Icons.Default.Info,
                        contentDescription = if (showApiKey) "Hide API Key" else "Show API Key"
                    )
                }
            },
            visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation()
        )

        // Model Name
        OutlinedTextField(
            value = uiState.customModelName,
            onValueChange = viewModel::updateCustomModelName,
            label = { Text("Model Name") },
            supportingText = { Text("Model name of the LLM") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // System Prompt
        OutlinedTextField(
            value = uiState.customSystemPrompt,
            onValueChange = viewModel::updateCustomSystemPrompt,
            label = { Text("System Prompt") },
            supportingText = { Text("Instructions for the AI assistant") },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Temperature Slider
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            Text(
                text = "Temperature: ${String.format("%.1f", uiState.temperature)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = uiState.temperature,
                onValueChange = viewModel::updateTemperature,
                valueRange = 0f..1f,
                steps = 9,

                )
            Text(
                text = "Controls randomness in responses",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Top-P Slider
        Column {
            Text(
                text = "Top-P: ${String.format("%.1f", uiState.topP)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = uiState.topP,
                onValueChange = viewModel::updateTopP,
                valueRange = 0f..1f,
                steps = 9
            )
            Text(
                text = "Controls diversity of token selection",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun HostedApiSettingsSection(viewModel: SettingsViewModel) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    var showApiKey by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Neural Cloud",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // API Key
        OutlinedTextField(
            value = uiState.hostedApiKey, onValueChange = viewModel::updateHostedApiKey,
            label = { Text("Neural Cloud Access Key") },
            placeholder = { Text("↓ Tap button below to get key ↓") },
            supportingText = { Text("Starts with 'ey...'") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            trailingIcon = {
                IconButton(onClick = {
                    showApiKey = !showApiKey
                }) {
                    Icon(
                        imageVector = if (showApiKey) Icons.Default.Lock else Icons.Default.Info,
                        contentDescription = if (showApiKey) "Hide API Key" else "Show API Key"
                    )
                }
            },
            visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation()
        )
        Card (
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "\uD83D\uDE80 Get Started",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Get started easily with Neural Cloud. After subscribing, copy the access key and paste it above.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Button(
                    onClick = {
                        val uri = "https://coreply.up.nadles.com/".toUri()
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Text(if (uiState.hostedApiKey.isNotEmpty()) "Manage Access Keys" else "Get Your Access Key")
                }

            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSection() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "About & Support",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // GitHub Link
        Surface (
            onClick = {
                val uri = Uri.parse("https://github.com/coreply/coreply")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🐱",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "View on GitHub",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Instagram Link
        Surface(
            onClick = {
                val uri = Uri.parse("https://instagram.com/_u/coreply.app")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.instagram.android")
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://instagram.com/coreply.app")
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📷",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Follow on Instagram",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Telegram Discussion
        Surface(
            onClick = {
                val uri = Uri.parse("https://t.me/coreplyappgroup")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💬",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Telegram Discussion",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Discord Server
        Surface(
            onClick = {
                val uri = Uri.parse("https://discord.gg/zCsQKmTFTk")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎮",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Discord Server",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Email
        Surface(
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:support@replymagic.ai")
                }
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📧",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Email: support@replymagic.ai",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Official Website
        Surface(
            onClick = {
                val uri = Uri.parse("https://coreply.app")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌐",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Official Website",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
