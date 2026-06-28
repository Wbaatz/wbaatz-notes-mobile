package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.PdfNote
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NoteDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NotesViewModel
import android.util.Log
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer() {
    val notesViewModel: NotesViewModel = viewModel()
    val noteProgressList by notesViewModel.noteProgressList.collectAsStateWithLifecycle()
    val notesList by notesViewModel.notesList.collectAsStateWithLifecycle()
    val isSyncing by notesViewModel.isSyncing.collectAsStateWithLifecycle()
    val syncError by notesViewModel.syncError.collectAsStateWithLifecycle()

    var currentTab by remember { mutableIntStateOf(0) }
    var selectedNote by remember { mutableStateOf<PdfNote?>(null) }
    var localPdfFile by remember { mutableStateOf<File?>(null) }

    if (selectedNote != null) {
        val activeNote = selectedNote!!
        Log.d("MainActivity", "Selected Note: ${activeNote.title} (ID: ${activeNote.id}, isLiveApi: ${activeNote.isLiveApi})")
        
        // Handle Token & Download Logic for API Notes
        if (activeNote.isLiveApi && localPdfFile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Fetching Secure PDF...", fontWeight = FontWeight.Bold)
                    if (syncError != null) {
                        Text(syncError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { selectedNote = null }) { Text("Go Back") }
                    }
                }
            }
            
            LaunchedEffect(activeNote.id) {
                notesViewModel.loadNoteWithToken(activeNote.id) { file ->
                    localPdfFile = file
                }
            }
        } else {
            val noteProgress = noteProgressList.find { it.noteId == activeNote.id }
            NoteDetailScreen(
                note = if (localPdfFile != null) activeNote.copy(localPath = localPdfFile!!.absolutePath) else activeNote,
                progress = noteProgress,
                onBack = { 
                    selectedNote = null
                    localPdfFile = null
                },
                onSaveProgress = { notesViewModel.saveProgress(it) }
            )
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("app_navigation_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Study Notes") },
                        label = { Text("Study Notes") },
                        modifier = Modifier.testTag("nav_home"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Default.Info, contentDescription = "About Founder") },
                        label = { Text("About Founder") },
                        modifier = Modifier.testTag("nav_about"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            val screenModifier = Modifier.padding(innerPadding)
            when (currentTab) {
                0 -> HomeScreen(
                    notesList = notesList,
                    noteProgressList = noteProgressList,
                    isSyncing = isSyncing,
                    syncError = syncError,
                    onRetrySync = { notesViewModel.fetchNotesFromBackend() },
                    onToggleBookmark = { id, bookmarked ->
                        notesViewModel.toggleBookmark(id, bookmarked)
                    },
                    onNoteClick = { selectedNote = it },
                    modifier = screenModifier
                )
                1 -> AboutScreen(
                    modifier = screenModifier
                )
            }
        }
    }
}
