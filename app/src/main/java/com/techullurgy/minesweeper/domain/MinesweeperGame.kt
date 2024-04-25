package com.techullurgy.minesweeper.domain

import androidx.compose.runtime.mutableStateOf
import kotlin.random.Random

sealed interface CellData {
    data object Bee: CellData
    data object Empty: CellData
    data class BeeNeighbour(val count: Int): CellData
}

data class Cell(
    val revealed: Boolean = false,
    val data: CellData
)

class MinesweeperGame {
    var board = startFresh()
        private set

    val gameOver = mutableStateOf(false)

    init {
        fillBees()
    }

    fun revealIndex(index: Int) {
        val row = index / COLS
        val col = index % COLS

        if(board[row][col].value.data is CellData.Bee) {
            revealAllAndGameOver()
        } else {
            floodFill(row, col)
        }
    }

    fun startNewGame() {
        board = startFresh()
        fillBees()
        gameOver.value = false
    }

    private fun startFresh() = List(ROWS) {
        List(COLS) {
            mutableStateOf(
                Cell(
                    data = CellData.Empty
                )
            )
        }
    }

    private fun revealAllAndGameOver() {
        for(i in 0 until ROWS) {
            for(j in 0 until COLS) {
                if(!board[i][j].value.revealed) {
                    board[i][j].value = board[i][j].value.copy(revealed = true)
                }
            }
        }
        gameOver.value = true
    }

    private fun floodFill(row: Int, col: Int) {
        if(row < 0 || row >= ROWS || col < 0 || col >= COLS) return

        val current = board[row][col].value

        if(!current.revealed) {
            board[row][col].value = current.copy(revealed = true)
        } else return

        if(current.data is CellData.Empty) {
            floodFill(row+1, col)
            floodFill(row-1, col)
            floodFill(row, col+1)
            floodFill(row, col-1)
        }
    }

    private fun fillBees() {
        generateBeeCells()
            .map {
                val current = board[it.first][it.second].value
                board[it.first][it.second].value = current.copy(data = CellData.Bee)
                it
            }
            .forEach {
                for(i in (it.first - 1)..(it.first + 1)) {
                    if(i < 0 || i >= ROWS) continue
                    for(j in (it.second - 1)..(it.second + 1)) {
                        if(j < 0 || j >= COLS) continue

                        if(i == it.first && j == it.second) continue

                        val current = board[i][j].value

                        board[i][j].value = when(current.data) {
                            CellData.Empty -> current.copy(data = CellData.BeeNeighbour(1))
                            is CellData.BeeNeighbour -> current.copy(data = current.data.copy(count = current.data.count + 1))
                            else -> current
                        }
                    }
                }
            }
    }

    private fun generateBeeCells(count: Int = 25): List<Pair<Int, Int>> = List(100) {
        Pair(Random.nextInt(ROWS), Random.nextInt(COLS))
    }.toSet().take(count)

    companion object {
        const val ROWS = 20
        const val COLS = 20
    }
}

fun main() {
    val game = MinesweeperGame()

    game.board.map { li ->
        li.map {
            when(it.value.data) {
                CellData.Bee -> "X"
                is CellData.BeeNeighbour -> "${(it.value.data as CellData.BeeNeighbour).count}"
                CellData.Empty -> "-"
            }
        }
    }.also {
        println(it)
    }
}