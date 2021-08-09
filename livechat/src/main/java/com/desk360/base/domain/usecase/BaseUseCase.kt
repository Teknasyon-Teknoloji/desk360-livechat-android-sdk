package com.desk360.base.domain.usecase

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BaseUseCase<T> {
    private var lastDisposable: Disposable? = null
    val compositeDisposable = CompositeDisposable()

    internal abstract fun buildUseCaseObservable(): Observable<T>?

    fun execute(
        onSuccess: ((t: T) -> Unit),
        onError: ((t: Throwable) -> Unit),
        onFinished: () -> Unit = {}
    ) {
        dispose()

        lastDisposable = buildUseCaseObservable()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doAfterTerminate(onFinished)
            ?.doOnError(onError)
            ?.subscribe(onSuccess, onError)

        lastDisposable?.let { disposable ->
            compositeDisposable.add(disposable)
        }
    }

    private fun dispose() {
        lastDisposable?.let { disposable ->
            if (disposable.isDisposed)
                disposable.dispose()
        }
    }
}