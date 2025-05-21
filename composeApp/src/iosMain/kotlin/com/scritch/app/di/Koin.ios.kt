package com.scritch.app.di

import com.scritch.app.MainViewController
import com.scritch.app.util.EmailLauncher
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.UIKit.UIViewController
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

actual object Koin {
    actual fun modules():List<Module>{
        return listOf(appModule, iosControllerModule, iosLauncherModule)
    }

    private val iosControllerModule = module {
        single<UIViewControllerHolder> { UIViewControllerHolder() }
    }

    private val iosLauncherModule = module {
        factory { EmailLauncher(get()) }
    }
}

// https://github.com/InsertKoinIO/koin/issues/1492
@OptIn(ExperimentalNativeApi::class)
class UIViewControllerHolder {
    private var viewControllerRef: WeakReference<UIViewController>? = null
    val viewController: UIViewController? get() = viewControllerRef?.get()

    fun createViewController(): UIViewController {
        MainViewController().apply {
            viewControllerRef = WeakReference(this)
            return@createViewController this
        }
    }

    fun cleanupViewController() {
        viewControllerRef?.clear()
        viewControllerRef = null
    }
}

object IosClassFactory : KoinComponent {
    fun getViewControllerHolder() = getKoin().get<UIViewControllerHolder>()
}