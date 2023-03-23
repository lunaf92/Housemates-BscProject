package x.lunacode.housemates.data

data class AuthState private constructor(
    val status: Status,
    val msg: String? = null
) {
    companion object {
        val LOADED = AuthState(Status.SUCCESS)
        val IDLE = AuthState(Status.IDLE)
        val LOADING = AuthState(Status.RUNNING)
        val LOGGED_IN = AuthState(Status.LOGGED_IN)
        fun error(msg: String?) = AuthState(Status.FAILED, msg)
    }

    enum class Status {
        SUCCESS,
        IDLE,
        RUNNING,
        LOGGED_IN,
        FAILED
    }
}
