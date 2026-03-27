package com.example.genaitraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.genaitraining.domain.model.Lift
import com.example.genaitraining.domain.model.LiftStatus
import com.example.genaitraining.domain.model.Trail
import com.example.genaitraining.domain.model.TrailStatus
import com.example.genaitraining.ui.SnowtoothIntent
import com.example.genaitraining.ui.SnowtoothViewModel
import com.example.genaitraining.ui.theme.GenAITrainingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GenAITrainingTheme {
                SnowtoothScreen()
            }
        }
    }
}

@Composable
fun SnowtoothScreen(viewModel: SnowtoothViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.onIntent(SnowtoothIntent.LoadData)
        viewModel.onIntent(SnowtoothIntent.ObserveStatusChanges)
    }

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text(text = "Lifts", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text(text = "Trails", modifier = Modifier.padding(16.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            state.error?.let {
                Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
            }

            when (selectedTab) {
                0 -> LiftList(state.lifts) { id, status ->
                    viewModel.onIntent(SnowtoothIntent.UpdateLiftStatus(id, status))
                }
                1 -> TrailList(state.trails) { id, status ->
                    viewModel.onIntent(SnowtoothIntent.UpdateTrailStatus(id, status))
                }
            }
        }
    }
}

@Composable
fun LiftList(lifts: List<Lift>, onStatusChange: (String, LiftStatus) -> Unit) {
    LazyColumn {
        items(lifts) { lift ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = lift.name, style = MaterialTheme.typography.headlineSmall)
                    Text(text = "Status: ${lift.status}")
                    Row {
                        Button(onClick = { onStatusChange(lift.id, LiftStatus.OPEN) }) {
                            Text("Open")
                        }
                        Button(
                            onClick = { onStatusChange(lift.id, LiftStatus.CLOSED) },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrailList(trails: List<Trail>, onStatusChange: (String, TrailStatus) -> Unit) {
    LazyColumn {
        items(trails) { trail ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = trail.name, style = MaterialTheme.typography.headlineSmall)
                    Text(text = "Status: ${trail.status}")
                    Text(text = "Difficulty: ${trail.difficulty}")
                    Row {
                        Button(onClick = { onStatusChange(trail.id, TrailStatus.OPEN) }) {
                            Text("Open")
                        }
                        Button(
                            onClick = { onStatusChange(trail.id, TrailStatus.CLOSED) },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}
