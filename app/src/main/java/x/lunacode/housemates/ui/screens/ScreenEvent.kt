package x.lunacode.housemates.ui.screens

sealed class ScreenEvent {

    data class OnSuccessLogin(val id: String?) : ScreenEvent()
    data class OnUserSubscribed(val id: String?) : ScreenEvent()
    data class SidePanelNav(val route: String, val id: String?) : ScreenEvent()

}
