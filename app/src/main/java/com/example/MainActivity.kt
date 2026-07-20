package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppViewModel
import com.example.ui.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: AppViewModel = viewModel()
      val isDarkModeState by viewModel.isDarkMode.collectAsState()
      val darkTheme = isDarkModeState ?: androidx.compose.foundation.isSystemInDarkTheme()

      MyApplicationTheme(darkTheme = darkTheme) {
        val currentScreen = viewModel.screenStack.lastOrNull() ?: Screen.SIGN_IN

        // Native hardware Back handler Integration
        val canNavigateBack = viewModel.screenStack.size > 1
        androidx.activity.compose.BackHandler(enabled = canNavigateBack) {
          viewModel.navigateBack()
        }

        AnimatedContent(
          targetState = currentScreen,
          transitionSpec = {
            slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
          },
          label = "ScreenTransition"
        ) { screen ->
          when (screen) {
            Screen.SPLASH -> SplashScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.SIGN_IN -> SignInScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.OTP_VERIFY -> OtpVerifyScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.LOGO_ANIMATION -> LogoAnimationScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.HOME -> HomeScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.ADD_LOCATION -> AddLocationScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.ADD_TAGS -> AddTagsScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.ADD_DETAILS -> AddDetailsScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.ASSIGN_CONTACTS -> AssignContactsScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.PHONE_CONTACTS_PICKER -> PhoneContactsPickerScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.ISSUE_REVIEW -> IssueReviewScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.ISSUE_SUCCESS -> IssueSuccessScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.EQC_CHECKLIST -> EqcChecklistScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.EQC_SELECT_TEAM -> EqcSelectTeamScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            Screen.EQC_SUCCESS -> EqcSuccessScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
          }
        }
      }
    }
  }
}
