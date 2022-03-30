package com.desk360.base.domain.usecase

abstract class AutoLoginTasksImpl<T> {

    internal abstract fun execute(param: T)
    abstract fun onStartTask()
    abstract fun onError(t: Throwable)
    abstract fun onComplete(id: String)
    abstract fun onSession(state: Boolean)


    enum class LoginProgressDialogState(val state: Int) {
        DEFAULT(0),
        START(1),
        STOP(2);
    }

}