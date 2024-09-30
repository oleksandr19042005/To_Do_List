package com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model.ActionListEntity
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model.DataBaseInstance
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.repository.ActionRepository
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.theme.ToDoListTheme
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.viewmodel.ActionViewModel
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.viewmodel.ActionViewModelFactory

import androidx.navigation.NavController
import com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class EditAction : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DataBaseInstance.getDatabase(applicationContext)
        val actionDao = db.ActionDao()

        val actionRepository = ActionRepository(actionDao)

        val actionViewModel: ActionViewModel by viewModels { ActionViewModelFactory(actionRepository) }

        setContent {
            ToDoListTheme {
                AppNavigation(actionViewModel = actionViewModel)
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ScreenEditAction(
    actionViewModel: ActionViewModel,
    navController: NavController,
    action: ActionListEntity
) {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = currentDateTime.format(formatter)

    var title by remember { mutableStateOf(action.titleAction) }
    var description by remember { mutableStateOf(action.descriptionAction) }
    var dateCreate by remember { mutableStateOf(action.dateCreate) }
    var lastRw by remember { mutableStateOf(action.lastReview) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val maxTopicLength = 50
    val maxDescriptionLength = 150

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF1))
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Text(
            text = "Last review: $lastRw",
            color = Color(0xFF546E7A),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Date created: $dateCreate",
            color = Color(0xFF546E7A),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back button",
                modifier = Modifier
                    .size(50.dp)
                    .padding(top = 10.dp)
                    .clickable {
                        navController.navigate("first_screen")
                    }
            )
            TextField(
                value = title,
                onValueChange = { newText ->
                    if (newText.length <= maxTopicLength) {
                        title = newText
                    }
                },
                label = { Text(fontSize = 20.sp, text = "Title") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFCB8C4E),
                    focusedIndicatorColor = Color(0xFF1E88E5),
                    unfocusedIndicatorColor = Color(0xFFB0BEC5),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(12.dp)

            )
        }


        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = description,
            onValueChange = { newText ->
                if (newText.length <= maxDescriptionLength) {
                    description = newText
                }
            },
            label = { Text(fontSize = 20.sp, text = "Description") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFCB8C4E),
                focusedIndicatorColor = Color(0xFF1E88E5),
                unfocusedIndicatorColor = Color(0xFFB0BEC5),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        coroutineScope.launch(Dispatchers.IO) {
                            actionViewModel.update(
                                action.copy(
                                    titleAction = title,
                                    descriptionAction = if (description.isEmpty()) " " else description,
                                    lastReview = formattedDateTime
                                )
                            )
                            withContext(Dispatchers.Main) {
                                navController.navigate("first_screen")
                            }
                        }
                    } else {
                        Toast.makeText(context, "Title is required", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5),
                    contentColor = Color.White
                )
            ) {
                Text("SAVE")
            }
        }
    }
}


