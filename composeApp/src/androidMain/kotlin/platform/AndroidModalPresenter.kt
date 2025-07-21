package platform

import domain.model.AppTheme

class AndroidModalPresenter : ModalPresenter {

    override fun presentAddEvent(appTheme: AppTheme, onCompletion: () -> Unit) {
        //navController.navigate(Screen.CreateEvent.route)


//        composable(
//            route = Screen.CreateEvent.route,
//            enterTransition = enterPush,
//            exitTransition = exitPush
//        ) {
//            AddEventScreen(onCancel = { navController.navigateUp() },
//                onSave = { navController.navigateUp() })
//        }


//        NavHost(navController = navController,
//            startDestination = Screen.Agenda.route,
//            enterTransition = { EnterTransition.None },
//            exitTransition = { ExitTransition.None }) {
//            composable(route = Screen.Agenda.route, exitTransition = stayPush) {
//                CalendarScreen(
//                    appThemeMode = theme,
//                    reloadEvents = state.shouldReloadEvents,
//                    onAddEvent = {
//                        viewModel.setEvent(AppContract.Event.NewEvent)
//                    },
//                    onEditEvent = { viewModel.setEvent(AppContract.Event.EditExternal(it)) },
//                    notEligible = {
//                        viewModel.setEvent(AppContract.Event.NotEligible)
//                    })
//            }
//        }
    }




}