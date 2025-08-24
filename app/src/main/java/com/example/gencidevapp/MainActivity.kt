package com.example.gencidevapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gencidevapp.data.database.AppDatabase
import com.example.gencidevapp.network.ApiClient
import com.example.gencidevapp.repository.UserRepository
import com.example.gencidevapp.ui.screen.UserDetailScreen
import com.example.gencidevapp.ui.screen.UserListScreen
import com.example.gencidevapp.ui.theme.GencidevAppTheme
import com.example.gencidevapp.ui.viewmodel.UserViewModel
import com.example.gencidevapp.ui.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GencidevAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val database = AppDatabase.getDatabase(this)
                    val repository = UserRepository(database.userDao(), ApiClient.apiService)
                    val viewModel: UserViewModel = viewModel(
                        factory = UserViewModelFactory(repository)
                    )

                    GencidevApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GencidevApp(viewModel: UserViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "user_list"
    ) {
        composable("user_list") {
            UserListScreen(
                viewModel = viewModel,
                onUserClick = { userId ->
                    navController.navigate("user_detail/$userId")
                }
            )
        }
        composable("user_detail/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
            UserDetailScreen(
                viewModel = viewModel,
                userId = userId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}