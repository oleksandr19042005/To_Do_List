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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.R
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model.ActionListEntity
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model.DataBaseInstance
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.repository.ActionRepository
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.theme.ToDoListTheme
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.viewmodel.ActionViewModel
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.viewmodel.ActionViewModelFactory

import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class AddAction : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DataBaseInstance.getDatabase(applicationContext)
        val actionDao = db.ActionDao()

        val actionRepository = ActionRepository(actionDao)

        val actionViewModel: ActionViewModel by viewModels { ActionViewModelFactory(actionRepository) }
        AndroidThreeTen.init(this)


        setContent {
            ToDoListTheme {
                AppNavigation(actionViewModel = actionViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAddAction(
    actionViewModel: ActionViewModel,
    navController: NavController,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val maxTopicLength = 100
    val maxDescriptionLength = 350
    val context = LocalContext.current

    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val formattedDateTime = currentDateTime.format(formatter)



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF1))
            .padding(16.dp)
            .statusBarsPadding()
    ) {

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

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { newText ->
                if (newText.length <= maxDescriptionLength) {
                    description = newText
                }
            },
            label = { Text(fontSize = 20.sp, text = "Description (optional)") },
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
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5),
                    contentColor = Color.White
                ),
                onClick = {
                    val action = ActionListEntity(
                        titleAction = title,
                        descriptionAction = if (description.isEmpty()) " " else description,
                        dateCreate = formattedDateTime,
                        lastReview = formattedDateTime,
                        isCompleted = false
                    )
                    if (action.titleAction.isEmpty()) {
                        Toast.makeText(context, "Topic is empty", Toast.LENGTH_SHORT).show()
                    } else {
                        coroutineScope.launch(Dispatchers.IO) {
                            actionViewModel.addTask(action)
                            withContext(Dispatchers.Main) {
                                navController.navigate("first_screen")
                            }
                        }
                    }
                }
            ) {
                Text(text = "ADD ACTION")
            }
        }
    }
}






