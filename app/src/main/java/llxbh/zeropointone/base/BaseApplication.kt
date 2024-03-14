package llxbh.zeropointone.base

import android.app.Application

class BaseApplication: Application() {

    companion object {
        lateinit var instance: Application
    }

    init {
        instance = this
    }

}