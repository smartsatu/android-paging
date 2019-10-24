package com.smartsatu.android.paging.util

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins.onError
import io.reactivex.schedulers.Schedulers

private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = { onError(OnErrorNotImplementedException(it)) }
private val onCompleteStub: () -> Unit = {}

fun <T : Any> Observable<T>.uiSubscribe(
        onNext: (T) -> Unit = onNextStub,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
): Disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(onNext, onError, onComplete)

fun Completable.uiSubscribe(
        onComplete: () -> Unit = onCompleteStub,
        onError: (Throwable) -> Unit = onErrorStub
): Disposable = subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(onComplete, onError)

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}