
/*
* Name: William Alger
* Class: CSC 460
* Date: 4/5/2022
* */


import java.net.*;
import java.io.*;
import java.util.Random;

public class Server extends Thread {
    private Socket toclientsocket; // the Socket used to communicate with the client
    private DataInputStream instream; // stores the input stream of the socket
    private DataOutputStream outstream; // stores the output stream of the socket
    private PrintWriter out; // the printwriter will allow us to use print( ) and println( ) methods
    private BufferedReader in; // the BufferedReader will allow us to use readLine( ) method
    private Random gen; // used to select random moves
    private char[][] board; // I implemented my game board as a matrix of char to make
    private int row, col; // Obviously to hold the current row and/or column values for moves

    public Server(Socket socket) throws IOException {
        this.toclientsocket = socket; // assign our socket to the client
        gen = new Random();

        // setup two-way communication with the client
        instream = new DataInputStream(toclientsocket.getInputStream());
        outstream = new DataOutputStream(toclientsocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(instream));
        out = new PrintWriter(outstream, true);

        // initialize our 4x4 array with blank chars ''
        board = new char[4][4];
        for (int x = 0; x <= 3; x++)
            for (int y = 0; y <= 3; y++)
                board[x][y] = ' ';
        row = -1;
        col = -1;
    }

    public void run() {
        int counter = 0;
        String response = "";
        boolean gameover = false;
        boolean turn = false;

        turn = Math.random() < 0.5; // flip a coin
        if (turn) {
            out.println("CLIENT");
            System.out.println("Client goes first");
        } else {
            System.out.println("Server goes first");
        }

        while (!gameover) {
            if (turn) { // players move
                try {
                    System.out.println("Awaiting client response...");
                    response = in.readLine(); // read the players move
                    String[] data = response.split("\\s+");
                    row = Integer.parseInt(data[1]);
                    col = Integer.parseInt(data[2]);
                    board[row][col] = 'O';
                    System.out.println("Client moved row:" + row + " col:" + col);
                } catch (IOException e) {
                    System.out.println("Error in reading clients response");
                    return;
                }

                printBoard();

                counter++;

                if (checkWin() || counter == 16) {
                    gameover = true;
                    if (checkWin()) {
                        out.println("MOVE -1 -1 WIN");
                    } else {
                        out.println("MOVE -1 -1 TIE");
                    }
                }
            } // end players move
            else { // computer move code
                makeMove();
                counter++;
                board[row][col] = 'X';
                System.out.println("Server moved row:" + row + " col:" + col);
                printBoard();
                if (checkWin() || counter == 16) {
                    gameover = true;
                    if (checkWin())
                        out.println("MOVE " + row + " " + col + " LOSS");
                    else
                        out.println("MOVE " + row + " " + col + " TIE");
                } else // move did not result game over
                    out.println("MOVE " + row + " " + col);
            } // end computer move code
            turn = !turn; // swap turns
        } // end while
    } // end run

    public void makeMove() { // generates random valid row and col values between 0 and 3
        do {
            row = gen.nextInt((3 - 0) + 1);
            col = gen.nextInt((3 - 0) + 1);
        } while (board[row][col] != ' ');
    }

    public void printBoard() { // prints the contents of our board
        System.out.println("\n  0      1     2      3");
        System.out.println("      |     |     |");
        for (int i = 0; i < 4; i++) {
            System.out.println("   " + board[i][0] + "  |  " + board[i][1] + "  |  " + board[i][2] + "  |  " + board[i][3] + "       " + i);
            if (i != 3)
                System.out.println("______|_____|_____|______");
            else
                System.out.println("      |     |     |");
        }
    }

    public boolean checkWin() {
        for (int i = 0; i <= 3; i++) // check for a row-win
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] &&
                    board[i][2] == board[i][3] && board[i][0] != ' ')
                return true;

        for (int x = 0; x <= 3; x++) // check for col-win
            if (board[0][x] == board[1][x] && board[1][x] == board[2][x] &&
                    board[2][x] == board[3][x] && board[0][x] != ' ')
                return true;

        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[2][2] == board[3][3] && board[0][0] != ' ') // check diagonal win right to left
            return true;
        return (board[0][3] == board[1][2] && board[1][2] == board[2][1] && board[2][1] == board[3][0] && board[0][3] != ' '); // check diagonal win left to right
    }
}