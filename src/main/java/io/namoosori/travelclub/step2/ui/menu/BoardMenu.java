package io.namoosori.travelclub.step2.ui.menu;

import io.namoosori.travelclub.step2.ui.console.BoardConsole;
import io.namoosori.travelclub.step2.ui.console.PostingConsole;
import io.namoosori.travelclub.util.Narrator;
import io.namoosori.travelclub.util.TalkingAt;
import javafx.geometry.Pos;

import java.util.Scanner;

public class BoardMenu {
    private BoardConsole boardConsole;
    private PostingMenu postingMenu;

    private Scanner scanner;
    private Narrator narrator;

    public BoardMenu(){
        this.boardConsole = new BoardConsole();
        this.postingMenu = new PostingMenu();

        this.scanner = new Scanner(System.in);
        this.narrator = new Narrator(this, TalkingAt.Left);
    }

    public void show(){
        int inputNumber = 0;

        while (true){
            displayMenu();
            inputNumber = selectMenu();

            switch (inputNumber){
                case 1:
                    boardConsole.register();
                    break;
                case 2:
                    boardConsole.findByName();
                    break;
                case 3:
                    boardConsole.modify();
                    break;
                case 4:
                    boardConsole.remove();
                    break;
                case 5:
                    postingMenu.show();
                    break;
                case 0:
                    return;
                default:
                    narrator.sayln("Choose again!");
                }
            }
        }

    private int selectMenu() {
        System.out.println("Select: ");
        int menuNumber = scanner.nextInt();

        if(menuNumber >= 0 && menuNumber <= 5){
            scanner.nextLine();
            return menuNumber;
        } else{
            narrator.sayln("It's a invalid number --> " + menuNumber);
            return -1;
        }
    }

    private void displayMenu() {
        narrator.sayln("");
        narrator.sayln("..............................");
        narrator.sayln(" Board menu ");
        narrator.sayln("..............................");
        narrator.sayln(" 1. Register a board");
        narrator.sayln(" 2. Find boards by name");
        narrator.sayln(" 3. Modify a board");
        narrator.sayln(" 4. Remove a board");
        narrator.sayln("..............................");
        narrator.sayln(" 5. Posting Menu");
        narrator.sayln("..............................");
        narrator.sayln(" 0. Previous");
        narrator.sayln("..............................");
    }

}

