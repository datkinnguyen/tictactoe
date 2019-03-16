package com.flinders.nguy1025.tictactoe

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main.*

// For now it's ok, if we want to add Name to player, should probably refactor to normal class instead of enum
enum class Player(val symbol: Char) {
    X('X'),
    O('O');

    fun symbolString(): String {
        return symbol.toString()
    }
}

class MainActivity : AppCompatActivity() {

    val EMPTY_CELL: Char = ' '

    // Define variables
    val gameSize: Int = 3
    var gameBoard: Array<CharArray> = Array(gameSize) { CharArray(gameSize) }
    var name: String = "Brett"
    var currentPlayer: Player = Player.X
        set(value) {
            field = value
            // auto trigger text change if we change player
            updateTurnText()
        }

    // Views
    var tableLayout: TableLayout? = null
    var turnTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        turnTextView = findViewById(R.id.turnTextView)
        tableLayout = findViewById(R.id.table_layout)
        tableLayout = findViewById(R.id.table_layout)

        fab.setOnClickListener { view ->

            startNewGame(false)
        }

        startNewGame(true)
    }

    private fun startNewGame(setClickListener: Boolean) {

        // reset all game states
        // default to playerX first
        currentPlayer = Player.X

        // reset gameboard
        for (i in 0 until gameBoard.size) {
            for (j in 0 until gameBoard[i].size) {
                gameBoard[i][j] = EMPTY_CELL

                // grab cell at position
                var cell = textViewAt(i, j)

                // set click listener
                if (setClickListener) {
                    cell.setOnClickListener { cellClickListener(i, j) }
                }

                // reset displaying text, ready for new game
                cell.text = EMPTY_CELL.toString()
            }
        }
    }

    private fun cellClickListener(row: Int, column: Int) {
        // check if this cell is already played
        var cell = textViewAt(row, column)

        if (cell.text != EMPTY_CELL.toString()) {
            return
        }

        // assign current symbol to gameboard
        gameBoard[row][column] = currentPlayer.symbol

        // display clicked cell on UI
        cell.text = gameBoard[row][column].toString()

        // change turn to another player
        currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X

        checkGameStatus()
    }


    /**
     * Check if the game is full or not (no more empty cells)
     */
    private fun isBoardFull(gameBoard: Array<CharArray>): Boolean {

        for (i in 0 until gameBoard.size) {
            for (j in 0 until gameBoard[i].size) {
                var cell = textViewAt(i, j)
                if (cell.text == EMPTY_CELL.toString()) {
                    // has one empty cell, game not full yet
                    return false
                }

            }
        }
        return true
    }

    /**
     * Check if we have a winner or not
     */
    private fun isWinner(gameBoard: Array<CharArray>, player: Player): Boolean {

        // TODO: refactor to be more generic, remove hardcoded
        // first check diagonal
        var cellD0 = textViewAt(0, 0)
        var cellD1 = textViewAt(1, 1)
        var cellD2 = textViewAt(2, 2)
        var cellD3 = textViewAt(0, 2)
        var cellD4 = textViewAt(2, 0)

        if (cellD0.text == cellD1.text && cellD1.text == cellD2.text && cellD2.text == player.symbolString()) {
            return true
        }

        if (cellD3.text == cellD4.text && cellD4.text == cellD1.text && cellD1.text == player.symbolString()) {
            return true
        }

        for (i in 0 until gameSize) {

            // check if each row has same values
            var rowSameValue = true
            for (colIndex in 0 until gameSize) {
                var cellRow = textViewAt(i, colIndex)
                if (cellRow.text != player.symbolString()) {
                    rowSameValue = false
                    break
                }
            }

            if (rowSameValue) {
                return true
            }

            // check if each row has same values
            var colSameValue = true
            for (rowIndex in 0 until gameSize) {
                var cellColumn = textViewAt(rowIndex, i)
                if (cellColumn.text != player.symbolString()) {
                    colSameValue = false
                    break
                }
            }

            if (colSameValue) {
                return true
            }
        }
        return false
    }

    /**
     * check Game status and update UI when it's over
     */
    private fun checkGameStatus() {
        var state: String? = null
        if (isWinner(gameBoard, Player.X)) {
            state = String.format(resources.getString(R.string.winner), Player.X.symbolString())
        } else if (isWinner(gameBoard, Player.O)) {
            state = String.format(resources.getString(R.string.winner), Player.O.symbolString())
        } else if (isBoardFull(gameBoard)) {
            state = resources.getString(R.string.draw)
        }

        // we have a winner or draw, game over
        if (state != null) {
            turnTextView?.text = state

            // show some UI for winner
            var builder = AlertDialog.Builder(this)
            builder.setMessage(state)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                startNewGame(false)
            }
            // create dialog and show
            builder.create().show()
        } else {

        }

    }

    /**
     * Find textView at row and column of tablelayout
     */
    private fun textViewAt(row: Int, column: Int): TextView {
        return (tableLayout?.getChildAt(row) as TableRow).getChildAt(column) as TextView
    }

    /**
     * update header title X's turn. this is auto triggered whenever current player is changed
     */
    private fun updateTurnText() {
        turnTextView?.text =
            String.format(resources.getString(R.string.turn), currentPlayer.symbolString())
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
