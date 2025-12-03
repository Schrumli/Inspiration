package com.schrumli.inspiration

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.unit.dp
import com.schrumli.inspiration.ui.theme.Blue50
import com.schrumli.inspiration.ui.theme.InspirationTheme

// TODO: combine AddNewListPopup and AddNewItemPopup functions
// TODO: put composables into different files to improve readability
// TODO: Add darkmode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InspirationTheme {
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
    var showAddDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<ActivityList?>(null) }
    var showDeleteDialog by remember { mutableStateOf<ActivityList?>(null) }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { onListSelected(list) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(list.list_name)
                }
                IconButton(
                    onClick = { showRenameDialog = list },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Blue50,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Rename list")
                }
                IconButton(
                    onClick = { showDeleteDialog = list },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete list")
                }
            }
        }

        // Add-new button
        Button(
            onClick = { showAddDialog = true }
        ) {
            Text("Add new")
        }
        if (showAddDialog) {
            AddNewListPopup(
                onDismiss = { showAddDialog = false },
                onSave = { showAddDialog = false }
            )
        }
        if (showDeleteDialog != null) {
            DeleteConfirmationDialog(
                list = showDeleteDialog!!,
                onConfirm = { 
                    it.deleteFile()
                    MyApp.lists.remove(it)
                    showDeleteDialog = null
                 },
                onDismiss = { showDeleteDialog = null }
            )
        }
        if (showRenameDialog != null) {
            RenameListPopup(
                list = showRenameDialog!!,
                onDismiss = { showRenameDialog = null },
                onSave = { list, newName ->
                    list.rename(newName)
                    showRenameDialog = null
                }
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(list: ActivityList, onConfirm: (ActivityList) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete List") },
        text = { Text("Are you sure you want to delete the list \"${list.list_name}\"?") },
        confirmButton = {
            Button(
                onClick = { onConfirm(list) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RenameListPopup(
    list: ActivityList,
    onDismiss: () -> Unit,
    onSave: (ActivityList, String) -> Unit
){
    var text by remember { mutableStateOf(list.list_name) }
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
                onSave(list, text)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Rename List") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester),
                placeholder = { Text("Enter new name") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSave(list, text)
                        focusManager.clearFocus()
                    }
                )
            )
        }
    )
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
                    choice = list.get(choice)
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
        Spacer(modifier = Modifier.height(48.dp))
    }
}

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
        modifier = modifier.padding(bottom = 16.dp, top=24.dp),
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