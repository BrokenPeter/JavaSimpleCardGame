import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 *  In this assignment, a simple card game is implemented. The computer will
 * simulate the dealer. When the game starts, 52 playing cards will be shuffled and the
 * player is given $100. At each round of the game, both the player and the dealer will be
 * given 3 cards (they are drawn from the top of the deck). Player will place his/her bet
 * (the amount must be integer). Then before the dealer opens the cards, the player has a
 * chance to draw AT MOST two cards from the top of the deck to replace any two of the
 * cards on hand and each card on hand can only be replaced ONCE. Player will lose the
 * bet if the dealer got a better hand (see below for explanation), otherwise the player wins
 * the same amount of money as his/her bet.
 *
 * @author  Leung Kwing Wang
 * @version 1.0
 * @since   2021-04-25
 */
public class Assignment3 {

    // Game parameters
    private int betCount = 0;
    private int money = 100;
    private int replaced = 0;
    private int[] pHand = new int[3];
    private int[] dHand = new int[3];
    private ArrayList<Integer> deck = new ArrayList<Integer>();

    // JFrame
    private JFrame frame;

    //6 JLabel components for holding 6 ImageIcon components
    private JLabel[] cardHolder = new JLabel[6];

    // JButton components for the player to replace card 1, card 2 and card 3
    private JButton b1 = new JButton("Replace Card 1");
    private JButton b2 = new JButton("Replace Card 2");
    private JButton b3 = new JButton("Replace Card 3");

    // 1 JLabel component showing the string “Bet: $”
    private JLabel l1= new JLabel("Bet: $");

    // 1 JTextField component for the player to input his/her bet
    private JTextField bet = new JTextField(8);

    // 2 JButton components for the player to start the game and to evaluate the result
    private JButton bStart = new JButton("Start");
    private JButton bResult = new JButton("Result");

    // 2 JLabel components for displaying important messages and the remaining budget that the player has
    private JLabel msg1 = new JLabel("Please place your bet!");
    private JLabel msg2 = new JLabel("Amount of money you have: $"+money);

    //-1 JmenuBar that consists 1 JMenu “Control” which contains 1 JMenuItem to quit the game ; 1 JMenu “Help” contains 1 JMenuItem for displaying the rule of the game
    private JMenuBar mb = new JMenuBar();
    private JMenu menu1 = new JMenu("Control");
    private JMenu menu2 = new JMenu("Help");
    private JMenuItem menuItemExit = new JMenuItem("Exit");
    private JMenuItem menuItemRule = new JMenuItem("Instruction");

    //other GUI components
    private ImageIcon[] cardImageArray = new ImageIcon[52];
    private ImageIcon cardImageBack;

    // Constructor method
    /** This is constructor method, init the gui and parameters
     *
     * @return Nothing
     */
    Assignment3() {
        //init ImageIcons
        loadCardImage();
        //init card image JLabel
        for (int i = 0; i < 6; i++){
            cardHolder[i] = new JLabel(cardImageBack);
        }
        //init game parameters
        reset();

        // frame setting
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("A Simple Card Game");
        frame.setResizable(true);

        // JMenu Setting

        menuItemExit.addActionListener(new ExitListener());
        menuItemRule.addActionListener(new RuleListener());
        menu1.add(menuItemExit);
        menu2.add(menuItemRule);
        mb.add(menu1);
        mb.add(menu2);
        frame.setJMenuBar(mb);


        // Create a panel as outter panal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        p1.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        p2.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        p3.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        p4.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        p1.setBackground(Color.green);
        p2.setBackground(Color.green);
        p1.setLayout(new GridLayout(2,3));


        for (int i = 0; i < 6; i++){
            cardHolder[i].setIcon(cardImageBack);
            cardHolder[i].setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
            p1.add(cardHolder[i],BorderLayout.SOUTH);
        }


        p2.add(b1);
        p2.add(b2);
        p2.add(b3);

        b1.addActionListener(new B1ReplaceListener());
        b2.addActionListener(new B2ReplaceListener());
        b3.addActionListener(new B3ReplaceListener());
        bStart.addActionListener(new StartListener());
        bResult.addActionListener(new ResultListener());

        p3.add(l1);
        p3.add(bet,BorderLayout.CENTER);
        p3.add(bStart,BorderLayout.EAST);
        p3.add(bResult,BorderLayout.EAST);

        p4.add(msg1);
        p4.add(msg2);


        panel.add(p1,BorderLayout.NORTH);
        panel.add(p2,BorderLayout.CENTER);
        panel.add(p3,BorderLayout.SOUTH);
        panel.add(p4,BorderLayout.SOUTH);

        frame.add(panel,BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void reset() {
        if (money <= 0){
            gameOver();
        }
        System.out.println("\n------");
        initDeck();
        replaced = 0;
        msg1.setText("Please place your bet!");
        msg2.setText("Amount of money you have: $"+money);
        for (int i = 0; i < 6; i++){
            cardHolder[i].setIcon(cardImageBack);
        }
        b1.setEnabled(false);
        b2.setEnabled(false);
        b3.setEnabled(false);
        bStart.setEnabled(true);
        bResult.setEnabled(false);

    }

    private void gameOver(){
        JOptionPane.showMessageDialog(frame,"<html>Game over!<br />You have no more money!<br />Please start a new game!","Message", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private void winDialog() {
        System.out.println("You win.");
        JOptionPane.showMessageDialog(frame,"Congratulation! You win this round!","Message", JOptionPane.INFORMATION_MESSAGE);
        money += betCount;
        reset();
    }

    private void loseDialog() {
        System.out.println("You lose.");
        JOptionPane.showMessageDialog(frame,"Sorry! The Dealer wins this round!","Message", JOptionPane.INFORMATION_MESSAGE);
        money -= betCount;
        reset();
    }

    /**
     * This is a listener for exit menuItem
     */
    static class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * This is a listener for rule menuItem
     */
    class RuleListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "<html>Rules to determine who has better cards:<br />" +
                    "J, Q, K are regarded as special cards.<br />" +
                    "Rule 1: The one with more special cards wins.<br />" +
                    "Rule 2: If both have the same number of special cards, add the face values of the other card(s) and take the remainder after dividing the sum by 10. The one with a bigger remainder wins. (Note: Ace = 1).<br />" +
                    "Rule 3: The dealer wins if both rule 1 and rule 2 cannot distinguish the winner.</html",
                    "Message",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * This is a listener for start button
     */
    class StartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            int foo;
            String input = bet.getText();
            try {
                foo = Integer.parseInt(input);
            }
            catch (NumberFormatException ne){
                JOptionPane.showMessageDialog(frame,"WARNING: The bet you place must be a positive integer!","Message", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (foo <= 0){
                JOptionPane.showMessageDialog(frame,"WARNING: The bet you place must be a positive integer!","Message", JOptionPane.INFORMATION_MESSAGE);
            } else if (foo > money){
                JOptionPane.showMessageDialog(frame,"WARNING: You dont have that much money. Please input another bet.","Message", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                betCount = foo;
                startGame();
            }
        }
    }

    /**
     * This is a listener for button of replace card 1
     */
    class B1ReplaceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (replaced >= 2){
                return;
            }
            replaceCard(1);
            b1.setEnabled(false);
        }
    }

    /**
     * This is a listener for button of replace card 2
     */
    class B2ReplaceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (replaced >= 2){
                return;
            }
            replaceCard(2);
            b2.setEnabled(false);
        }
    }

    /**
     * This is a listener for button of replace card 3
     */
    class B3ReplaceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (replaced >= 2){
                return;
            }
            replaceCard(3);
            b3.setEnabled(false);
        }
    }

    /**
     * This is a listener for result button
     */
    class ResultListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            evaluateResult();
        }
    }

    private void startGame () {
        bStart.setEnabled(false);
        b1.setEnabled(true);
        b2.setEnabled(true);
        b3.setEnabled(true);
        bResult.setEnabled(true);
        msg1.setText("Your Current bet is: $"+betCount);

        System.out.print("Dealer draw: ");
        for (int i = 0; i < 3; i++) {
            dHand[i] = drawCard();
            System.out.print(dHand[i]+" ("+((dHand[i]+1) % 13)+"), ");
        }
        System.out.println();

        System.out.print("Player draw: ");
        for (int i = 0; i < 3; i++) {
            pHand[i] = drawCard();
            System.out.print(pHand[i]+" ("+(pHand[i]+1) % 13+"), ");
            printCard(pHand[i], 3+i);
        }
        System.out.println();
    }

    private int drawCard () {
        int cardDrawn = deck.get(0);
        deck.remove(0);
        return cardDrawn;
    }

    private void printCard (int cardIndex, int cardPosition){
        cardHolder[cardPosition].setIcon(cardImageArray[cardIndex]);
    }

    private void replaceCard (int pHandIndex){
        replaced += 1;
        if (replaced>1) {
            b1.setEnabled(false);
            b2.setEnabled(false);
            b3.setEnabled(false);
        }
        pHand[pHandIndex-1] = drawCard();
        System.out.println("Replace pHand["+pHandIndex+"]: "+ pHand[pHandIndex-1]);
        printCard(pHand[pHandIndex-1], 2+pHandIndex);
    }

    private void evaluateResult() {
        System.out.println("Getting Result:");
        int dSpecial = 0;
        int pSpecial = 0;
        int dRemain = 0;
        int pRemain = 0;

        System.out.print("Dealer's hand: ");
        for (int i = 0; i < 3; i++){
            printCard(dHand[i], i);
            int value = (dHand[i]+1) % 13;
            if (value == 0) {
                value = 13;
            }
            System.out.print(value+", ");
            if (value > 10){
                dSpecial += 1;
            } else {
                dRemain += value;
            }
        }
        System.out.println();

        System.out.print("Player's hand: ");
        for (int i = 0; i < 3; i++){
            int value = (pHand[i]+1) % 13;
            if (value == 0) {
                value = 13;
            }
            System.out.print(value+", ");
            if (value > 10){
                pSpecial += 1;
            } else {
                pRemain += value;
            }
        }
        System.out.println();

        if (pSpecial > dSpecial){
            winDialog();
        } else if (pSpecial < dSpecial){
            loseDialog();
        } else {
            if ( (pRemain % 10) > (dRemain % 10) ){
                winDialog();
            } else {
                loseDialog();
            }
        }


    }

    public void initDeck() {
        if (!deck.isEmpty()){
            deck.clear();
        }
        for (int i = 0; i < 52; i++){
            deck.add(i);
        }
        Collections.shuffle(deck);
        System.out.println("Deck: "+deck);
    }

    private void loadCardImage() {
        //load images
        cardImageBack = new ImageIcon(this.getClass().getResource("/images/card_back.gif"));
        for (int i = 1; i <= 4; i++){
            for (int j = 1; j <=13; j++){
                int index = (i-1)*13+(j-1);
                cardImageArray[index] = new ImageIcon(this.getClass().getResource("/images/card_"+i+j+".gif"));
//                System.out.println("load card_"+i+j+".gif to array["+index+"]");
            }
        }
    }

    // Driver code
    /**
     * This is the main method of the game
     * @param args Unused.
     * @return Nothing.
     */
    public static void main(String[] args) {
        new Assignment3();
//        GameFrame gframe = new GameFrame();
    }
}
