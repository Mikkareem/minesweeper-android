package com.techullurgy.minesweeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techullurgy.minesweeper.domain.CellData
import com.techullurgy.minesweeper.domain.MinesweeperGame

@Preview(showSystemUi = true)
@Composable
fun GameBoard(
    modifier: Modifier = Modifier
) {
    val game = remember {
        MinesweeperGame()
    }

    val flattenedBoard = game.board.flatten()

    Column {
        LazyVerticalGrid(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            columns = GridCells.Fixed(MinesweeperGame.COLS)
        ) {
            itemsIndexed(flattenedBoard) { index, it ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .border(width = 1.dp, color = Color.Black)
                        .then(
                            if (it.value.revealed) {
                                Modifier.background(
                                    when (it.value.data) {
                                        CellData.Bee -> Color.Green
                                        is CellData.BeeNeighbour,
                                        CellData.Empty -> Color.Magenta
                                    }
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if(it.value.revealed) {
                        when(it.value.data) {
                            is CellData.BeeNeighbour -> {
                                Text(
                                    text = (it.value.data as CellData.BeeNeighbour).count.toString(),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            else -> {
                                Spacer(Modifier)
                            }
                        }
                    } else {
                        Spacer(
                            modifier = Modifier
                                .background(Color.White)
                                .clickable { game.revealIndex(index) }
                        )
                    }
                }
            }
        }
        if(game.gameOver.value) {
            Text(text = "Game Over")
            TextButton(onClick = { game.startNewGame() }) {
                Text("New Game")
            }
        }
    }
}