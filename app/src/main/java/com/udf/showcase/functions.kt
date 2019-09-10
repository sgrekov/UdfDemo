package com.udf.showcase

import com.factorymarket.rxelm.msg.Idle
import com.factorymarket.rxelm.msg.Msg
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

inline fun inView(crossinline operations: () -> Unit): Single<Msg> {
    return Single.fromCallable {
        operations()
    }.subscribeOn(AndroidSchedulers.mainThread()).map { Idle }
}

fun noEffect(): Single<Msg> = Single.just(Idle)