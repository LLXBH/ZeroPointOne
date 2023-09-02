package llxbh.zeropointone.app

import android.app.Application

class MyApplication: Application() {

    companion object {
        lateinit var instance: Application
    }

    init {
        instance = this
    }

}