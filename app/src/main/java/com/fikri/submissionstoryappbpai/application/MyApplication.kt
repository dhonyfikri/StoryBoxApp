package com.fikri.submissionstoryappbpai.application

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
}