package dev.datlag.tolgee.kormatter.utils

sealed interface PartAction {
    data object CUSTOM : PartAction
    data object STANDARD : PartAction
    data object FORBIDDEN : PartAction
}