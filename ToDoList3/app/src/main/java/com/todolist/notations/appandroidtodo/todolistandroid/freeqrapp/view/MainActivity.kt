package com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.todolist.notations.appandroidtodo.todolistandroid.freeqrapp.R

import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model.DataBaseInstance
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model.ActionListEntity
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.repository.ActionRepository
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.theme.ToDoListTheme
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.viewmodel.ActionViewModel
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.viewmodel.ActionViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(actionViewModel: ActionViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "first_screen") {
        composable("first_screen") {
            ToDoListScreen(
                actionViewModel = actionViewModel,
                navController = navController
            )
        }
        composable("add_action") {
            ScreenAddAction(actionViewModel = actionViewModel, navController = navController)
        }
        composable(
            "edit_screen/{id}/{title}/{description}/{dateCreate}/{lastReview}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
                navArgument("title") { type = NavType.StringType; },
                navArgument("description") { type = NavType.StringType; },
                navArgument("dateCreate") { type = NavType.StringType; },
                navArgument("lastReview") { type = NavType.StringType; }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val title = backStackEntry.arguments?.getString("title") ?: "Untitled"
            val description = backStackEntry.arguments?.getString("description") ?: "No description"
            val dateCreate = backStackEntry.arguments?.getString("dateCreate") ?: "No dateCreate"
            val lastReview = backStackEntry.arguments?.getString("lastReview") ?: "No lastReview"
            val action = ActionListEntity(id, title, description, dateCreate, lastReview, false)

            ScreenEditAction(
                actionViewModel = actionViewModel,
                navController = navController,
                action = action
            )
        }
    }
}

@Composable
fun ToDoListScreen(
    actionViewModel: ActionViewModel,
    navController: NavController
) {
    val allActions by actionViewModel.allActions.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFFECEFF1))
    ) {
        items(allActions.reversed()) { action ->
            ToDoListItem(
                actionViewModel = actionViewModel,
                action = action,
                onClick = { navController.navigate("edit_screen/${action.id}/${action.titleAction}/${action.descriptionAction}/${action.dateCreate}/${action.lastReview}") }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.mipmap.ic_add_circle_outline),
            contentDescription = "Button for add action",
            modifier = Modifier
                .fillMaxWidth(0.28f)
                .aspectRatio(1f)
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .clickable {
                    navController.navigate("add_action")
                }
        )
    }
}

@Composable
fun ToDoListItem(
    actionViewModel: ActionViewModel,
    action: ActionListEntity,
    onClick: () -> Unit
) {
    val isChecked = action.isCompleted
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 7.dp, end = 7.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isChecked) Color.Gray else Color(0xFFCB8C4E))
            .border(2.dp, Color.White, RoundedCornerShape(10.dp))
            .shadow(1.dp, RoundedCornerShape(3.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 25.dp, top = 20.dp)
        ) {
            Text(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                text = action.titleAction,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(15.dp),
            horizontalAlignment = Alignment.End
        ) {
            Checkbox(
                checked = action.isCompleted,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch(Dispatchers.IO) {
                        actionViewModel.delete(action)
                    }
                }
            )
            Image(
                painter = painterResource(id = R.drawable.ic_basket_rubbish),
                contentDescription = "Button for delete action",
                modifier = Modifier
                    .padding(end = 11.dp)
                    .size(25.dp)
                    .clickable {
                        showDialog = true
                    }
            )
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirm deletion") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        Thread {
                            actionViewModel.delete(action)
                        }.start()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
