package com.udf.showcase.main.model

import dev.teapot.cmd.Cmd
import dev.teapot.contract.State
import dev.teapot.msg.Msg
import org.eclipse.egit.github.core.Repository

data class MainState(
    val isLoading: Boolean = true,
    val userName: String,
    val reposList: List<Repository> = listOf()
) : State()


data class LoadReposCmd(val userName: String) : Cmd()

data class ReposLoadedMsg(val reposList: List<Repository>) : Msg()
object CancelMsg: Msg()
object RefreshMsg: Msg()