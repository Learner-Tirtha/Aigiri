package com.example.aigiri.navigation

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aigiri.dao.LiveLocationDao
import com.example.aigiri.network.*
import com.example.aigiri.ui.screens.*
import com.example.aigiri.viewmodel.*
import com.example.aigiri.repository.*



@Composable
fun AppNavigation(startDestination: String, tokenManager: TokenManager) {
    val navController = rememberNavController()
    val context= LocalContext.current
// ViewModels
    val signupViewModel = remember {
        SignupViewModel(OtpRepository(), userRepository = UserRepository())
    }

    val verifyOtpViewModel = remember {
        VerifyOtpViewModel(OtpRepository(), userRepository = UserRepository())
    }

    val loginViewModel = remember {
        LoginViewModel(tokenManager, EmergencyContactsRepository(), context)
    }

    val dashboardViewModel = remember {
        DashboardViewModel(tokenManager)
    }

    val emergencyContactsViewModel = remember {
        EmergencyContactsViewModel(EmergencyContactsRepository(), tokenManager)
    }

    val permissionViewModel: PermissionViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )
    val NationalHelplineViewModel= remember {
        NationalHelplineViewModel()
    }
    val ForgotPasswordViewModel= remember {
        ForgotPasswordViewModel(otpRepository = OtpRepository(), userRepository = UserRepository())
    }
    val forgotPasswordVerificationViewModel= remember {
        ForgotPasswordVerificationViewModel(otpRepository = OtpRepository())
    }
    val resetPasswordViewModel= remember {
        ResetPasswordViewModel(userRepository = UserRepository())
    }
    val settingResetPasswordViewModel= remember {
        SettingResetPasswordViewModel(repository = UserRepository(), tokenManager = TokenManager(context))
    }
//    val fakecallViewModel= remember {
//        FakeCallViewModel()
//    }

    val reportViewModel= remember {ReportViewModel(ReportRepository())}
    val liveStreamViewModel=remember{LiveStreamViewModel(tokenManager = TokenManager(context =context))}
    val settingsViewModel= remember { SettingsViewModel(tokenManager = TokenManager(context)) }
    val ChatViewModel= remember { ChatViewModel(ChatRepository()) }
    val SOSViewModel=remember{SOSViewModel(repository = SOSRepository(context =context), emergencyRepository = EmergencyContactsRepository(), userRepository = UserRepository(), tokenManager = TokenManager(context),context=context)}
    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            SplashScreen(onSplashComplete = {
                navController.navigate("welcome") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("add_contacts") {
            EmergencyContactsScreen(navController = navController, viewModel = emergencyContactsViewModel)
        }
        composable("login") {
            LoginScreen(navController = navController, viewModel = loginViewModel, tokenManager = tokenManager)
        }
        composable("welcome") {
            WelcomeScreen(navController)
        }
        composable("signup")
        {
            SignUpScreen(navController,signupViewModel)
        }



        composable(
            route = "verify_otp/{phoneNumber}/{verificationId}",
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("verificationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
            VerifyOtpScreen(
                navController = navController,
                phoneNumber = phoneNumber,
                verificationId = verificationId,
                viewModel = verifyOtpViewModel,
                signupViewModel = signupViewModel
            )
        }

        composable("dashboard") {
            DashboardScreen(
                viewModel = dashboardViewModel,
                navController = navController,
                tokenManager = tokenManager,
                liveStreamViewModel = liveStreamViewModel,
                SOSViewModel = SOSViewModel,
                Context = context
            )
        }

        composable("history") {
            HistoryScreen()
        }

        composable("add_contacts") {
            EmergencyContactsScreen(navController, emergencyContactsViewModel)
        }


        composable("permission")
        {
            GrantPermissionScreen(viewModel = permissionViewModel,navController=navController)
        }
        composable("livecall") {
            LiveStreamScreen(liveStreamViewModel, navController = navController)
        }
        composable("setting") {
            SettingsScreen(
                navController = navController,
                onBackClick = {
                    navController.navigate("dashboard") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                viewModel = settingsViewModel
            )

        }
        composable("chatbot") {
            ChatScreen(ChatViewModel,navController)
        }
        composable("report") {
            ReportFormScreen(
                onBackClick = { navController.popBackStack() },
                onDraftReady = { navController.navigate("report_preview") },
                viewModel = reportViewModel

            )

        }
        composable("report_preview") {
            ReportPreviewScreen(
                onBackClick = { navController.popBackStack() },
                onConfirmSuccess = { id, pdfUrl ->
                    navController.navigate("report_success/$id?pdfUrl=${pdfUrl ?: ""}")
                },
                viewModel = reportViewModel
            )
        }

        composable(
            route = "report_success/{complaintId}?pdfUrl={pdfUrl}",
            arguments = listOf(
                navArgument("complaintId") { type = NavType.StringType },
                navArgument("pdfUrl") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId").orEmpty()
            val pdfUrl = backStackEntry.arguments?.getString("pdfUrl").orEmpty()
            ReportSuccessScreen(
                complaintId = complaintId,
                pdfUrl = pdfUrl.ifBlank { null },
                onDone = {
                    // go back to dashboard or start over form
                    navController.popBackStack("dashboard", inclusive = false)
                }
            )
        }



        composable("history") {
            ReportHistoryScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("national_helpline"){
            NationalHelpLineScreen(navController=navController, viewModel =NationalHelplineViewModel)
        }
        composable("enter_phoneno") {
            forgotPasswordPhonenoScreen(navController = navController, verifyOtpViewModel = ForgotPasswordViewModel)
        }
        composable(
            route = "forgot_password_verify_otp/{phoneNumber}/{verificationId}",
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("verificationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""

            VerifyOtpForgotPassword(
                navController = navController,
                phoneNumber = phoneNumber,
                verificationId = verificationId,
                viewModel = forgotPasswordVerificationViewModel,
                verifyOtpViewModel = ForgotPasswordViewModel
            )
        }
        composable(
            route = "reset_password/{phoneNumber}",
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""

            ResetPasswordScreen(
                navController = navController,
                phoneNumber = phoneNumber,
                viewModel = resetPasswordViewModel
            )
        }
        composable("forgot_password_update_success") {
            PasswordUpdateSuccessScreen(onFinish = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
                )

        }
        composable("reset_password_update_success") {
            PasswordUpdateSuccessScreen(onFinish = {
                navController.navigate("dashboard") {
                    popUpTo("dashboard") { inclusive = true }
                }
            }
            )

        }
        composable("setting_reset_password")
        {
            SettingResetPasswordScreen(navController, viewModel = settingResetPasswordViewModel)
        }
//        composable("fakecall")
//        {
//            FakeCallScreen(onBackClick =
//            {
//                navController.popBackStack()
//            }, viewModel = fakecallViewModel)
//        }
        composable("livetracking") {
            val context = LocalContext.current
            val tokenManager = TokenManager(context)
            val userId by tokenManager.userIdFlow.collectAsState(initial = null)

            userId?.let { uid ->
                LiveLocationTracking(
                    userId = uid,
                    liveLocationDao = LiveLocationDao()
                )
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading user info...")
                }
            }
        }
    }
}
