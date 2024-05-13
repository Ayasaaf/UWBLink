


/*
@Composable

fun NavigationGraph() {
    val navController = rememberNavController()


    val viewModel = hiltViewModel<BluetoothViewModel>()
    val state by viewModel.state.collectAsState()
    NavHost(navController = navController, startDestination = "Splash_Screen") {


        composable("Splash_Screen") {

            SplashScreen(navController = navController, context = MainActivity())


        }

        composable("On_Boarding_Screen") {
            OnboardingScreen(navController = navController )
        }

        composable("Device_Screen") {
            DeviceScreen(state = state,
                onStartScan = viewModel::StartScan,
                onStopScan = viewModel::StopScan,
                onDeviceClick = viewModel::connectToDevice,
                onStartServer = viewModel::waitForIncomingConnections,
                navController = navController,
                )
        }

        composable("Chat_Screen") {
            when {
                state.isConnecting -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(text = "Connecting ...")
                    }
                }

                state.isConnected -> {
                    ChatScreen(state = state,
                        OnDisconnect = viewModel::disconnectFromDevice,
                        OnSendMessage = viewModel::sendMessage,
                        navController = navController,
                        navigationtoDeviceScreen = { navController.navigate("Device_Screen") })
                }
            }
        }
    }
}
*/