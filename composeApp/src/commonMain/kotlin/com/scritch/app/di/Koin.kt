package com.scritch.app.di

import org.koin.core.module.Module

expect object Koin {
    fun modules(): List<Module>
}