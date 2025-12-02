package com.example.inspirationv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.inspirationv2.ui.theme.Inspirationv2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Inspirationv2Theme {
                InspirationApp()
            }
        }
    }
}

@Composable
fun InspirationApp() {
    var currentList: ActivityList? by remember { mutableStateOf(null)}

    if (currentList == null) {
        Listselector(onListSelected = { currentList = it })
    }else{
        ShowList(currentList!!, closeList = { currentList = null })
    }
}

@Composable
fun Listselector(onListSelected: (ActivityList) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        // HEADER
        Text(
            text = "Inspiration",
            modifier = Modifier.padding(bottom = 32.dp, top = 16.dp),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )

        // LISTS
        for (list in MyApp.lists) {
            Button(
                onClick = { onListSelected(list) }
            ) {
                Text(list.list_name)
            }
        }

        // Add-new button
        Button(
            onClick = { showDialog = true }
        ) {
            Text("Add new")
        }
        if (showDialog) {
            AddNewListPopup(
                onDismiss = { showDialog = false },
                onSave = { showDialog = false }
            )
        }
    }

}

@Composable
fun ShowList(list: ActivityList, closeList: () -> Unit) {
    var showList by remember { mutableStateOf(false) }
    var choice by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListHeader(list.list_name)

        Row {
            Button(
                onClick = {
                    choice = list.get()
                },
                enabled = list.size() > 0
            ) {
                Text("Choose")
            }
            Spacer(Modifier.width(width = 10.dp))
            Button(
                onClick = { showList = !showList }
            ) {
                Text(if (showList) "Hide List" else "Show List")
            }
            Spacer(Modifier.width(width = 10.dp))
            Button(
                onClick = { showDialog = true }
            ) {
                Text("Add new")
            }
        }

        if (showDialog) {
            AddNewItemPopup(
                onDismiss = { showDialog = false },
                onSave = { showDialog = false },
                list = list
            )
        }

        if (choice != "") {
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "You should consider: $choice",
                )

                TextButton(
                    onClick = {
                        list.remove(choice)
                        choice = ""
                    }
                ) {
                    Text("remove", color = Color.Red)
                }
            }
        }

        if (showList) {
            Spacer(Modifier.height(8.dp))
            Column {
                if (list.size() > 0) {
                    for (item in list.all()) {
                        Text(text = item, style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    Text(text = "<No items>", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }


        Spacer(Modifier.weight(1f))

        Button(
            onClick = closeList,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// TODO: combine AddNewListPopup and AddNewItemPopup functions
@Composable
fun AddNewListPopup(
    onDismiss: () -> Unit,
    onSave: () -> Unit
){
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Automatically request focus when dialog opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                MyApp.lists.add(ActivityList(text, context))
                onSave()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add New List") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester),
                placeholder = { Text("Enter name of new List") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        MyApp.lists.add(ActivityList(text, context))
                        onSave()
                        focusManager.clearFocus()
                    }
                )
            )
        }
    )
}
@Composable
fun AddNewItemPopup(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    list: ActivityList
){
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Automatically request focus when dialog opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                list.add(text)
                onSave()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add New Item") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester),
                placeholder = { Text("Enter text") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        list.add(text)
                        onSave()
                        focusManager.clearFocus()
                    }
                )
            )
        }
    )
}
@Composable
fun ListHeader(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Get inspiration for",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}


// #################
// # Template code #
// #################

@PreviewScreenSizes
@Composable
fun Inspirationv2App() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Greeting(
                name = currentDestination.label,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Inspirationv2Theme {
        Greeting("Android")
    }
}
