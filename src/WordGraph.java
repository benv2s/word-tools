import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class WordGraph {
    private static final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    private String startWord;
    private String endWord;

    private HashSet<String> validWords;
    private HashMap<Node, HashSet<Node>> adjList;

    private void loadWords(ArrayList<String> rmWords) {
        HashSet<String> validWords = new HashSet<String>();

        File file = new File("C:/Users/benbs/Desktop/Programs/word/words-58k.txt");
        Scanner lineScanner = null;
        try {
            lineScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.exit(1);
        }
        while (lineScanner.hasNextLine()) {
            validWords.add(lineScanner.nextLine());
        }

        for (String rmWord : rmWords) {
            validWords.remove(rmWord);
        }

        lineScanner.close();

        this.validWords = validWords;
    }

    private static ArrayList<Character> toWordList(String word) {
        ArrayList<Character> wordList = new ArrayList<Character>();
        for (int i = 0; i < word.length(); i++) {
            wordList.add(word.charAt(i));
        }
        return wordList;
    }

    private static String wlToString(ArrayList<Character> wordList) {
        String string = "";
        for (Character letter : wordList) {
            string += letter.toString();
        }
        return string;
    }

    private HashSet<Node> getValidOptions(String word) {
        ArrayList<Character> wordList = toWordList(word); // arraylist of this word's letters - searching more than
                                                          // inserting so arraylist is better
        HashSet<String> wordOptions = new HashSet<String>(); // set we will put options into

        for (int letter_i = 0; letter_i < wordList.size(); letter_i++) {
            // iterate thru letters to remove each
            ArrayList<Character> rmWordList = new ArrayList<Character>(wordList); // deep copy
            rmWordList.remove(letter_i);
            wordOptions.add(wlToString(rmWordList));
            // iterate thru letters to change each to all possible letters
            for (char letter : alphabet) {
                ArrayList<Character> setWordList = new ArrayList<Character>(wordList);
                setWordList.set(letter_i, letter);
                wordOptions.add(wlToString(setWordList));
            }
        }
        // iterate thru positions to insert all possible letters at each index
        for (int pos_i = 0; pos_i < wordList.size() + 1; pos_i++) {
            for (char letter : alphabet) {
                ArrayList<Character> addWordList = new ArrayList<Character>(wordList);
                addWordList.add(pos_i, letter);
                wordOptions.add(wlToString(addWordList));
            }
        }

        HashSet<Node> validOptions = new HashSet<Node>();
        for (String optWord : wordOptions) {
            if (validWords.contains(optWord)) {
                validOptions.add(new Node(optWord));
            }
        }

        validOptions.remove(new Node(word)); // remove original from list of options

        return validOptions;
    }

    private LinkedList<String> BFS() {
        // this is already fast enough - bidirectional is unnecessary
        Queue<Node> q = new LinkedList<Node>(); // instantiating linkedlist with queue interface

        Node startNode = new Node(startWord);
        q.add(startNode);
        startNode.visit();

        if (startNode.data().equals(endWord)) { // distance 0
            return backtrace(startNode);
        }

        while (true) {
            Node curNode = q.poll();

            if (curNode == null) { // if queue empty, no path found
                return null;
            }

            for (Node adjNode : adjList.get(curNode)) { // gets adjacent nodes to the node of
                                                        // current value
                if (adjNode.data().equals(endWord)) {
                    adjNode.setParent(curNode); // also set parent here
                    return backtrace(adjNode);
                }

                if (!adjNode.isVisited()) {
                    adjNode.setParent(curNode); // only set parent if adding to the queue, otherwise will create loops
                    q.add(adjNode);
                    adjNode.visit();
                }

            }

        }

    }

    private static LinkedList<String> backtrace(Node finalNode) {
        LinkedList<String> trace = new LinkedList<>(); // linkedlist is faster here bc adding to head

        Node curNode = finalNode;
        while (curNode != null) {
            trace.add(0, curNode.data());
            curNode = curNode.parent();
        }
        return trace;
    }

    private static void printTrace(LinkedList<String> trace) {
        String endWord = trace.removeLast();
        for (String step : trace) {
            System.out.println(step + " -->");
        }

        System.out.println(endWord + " (" + trace.size() + ")");
    }

    public static void main(String[] args) {
        WordGraph w = new WordGraph();

        // get input args
        if (args.length < 2) {
            System.out.println("Usage: WordGraph <startWord> <endWord> [exclWord1] [exclWord2] ...");
            System.exit(1);
        }
        w.startWord = args[0];
        w.endWord = args[1];

        ArrayList<String> rmWords = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            rmWords.add(args[i]);
        }

        long loadTimeStart = System.currentTimeMillis();
        System.out.print("Loading words ");
        w.loadWords(rmWords);

        long loadTimeElapsed = System.currentTimeMillis() - loadTimeStart;
        System.out.println(loadTimeElapsed + " ms");

        // check startWord, endWord validity
        if (!w.validWords.contains(w.startWord)) {
            System.out.println("Invalid startWord: " + w.startWord);
            System.exit(1);
        }

        if (!w.validWords.contains(w.endWord)) {
            System.out.println("Invalid endWord: " + w.endWord);
            System.exit(1);
        }

        long buildTimeStart = System.currentTimeMillis();
        System.out.print("Building adjList ");
        HashMap<Node, HashSet<Node>> adjList = new HashMap<Node, HashSet<Node>>(); // new hashmap adjList, connecting
                                                                                   // nodes to node arrays
        for (String vWord : w.validWords) {
            adjList.put(new Node(vWord), w.getValidOptions(vWord)); // this takes a while because has to get
                                                                    // all valid options
        }
        long buildTimeElapsed = System.currentTimeMillis() - buildTimeStart;
        System.out.println(buildTimeElapsed + " ms");

        long searchTimeStart = System.currentTimeMillis();
        System.out.print("Finding path ");
        LinkedList<String> trace = w.BFS();
        long searchTimeElapsed = System.currentTimeMillis() - searchTimeStart;
        System.out.println(searchTimeElapsed + " ms\n");

        if (trace == null) {
            System.out.println("No path found.");
            System.exit(1);
        }
        printTrace(trace);

        long totalTimeElapsed = loadTimeElapsed + buildTimeElapsed + searchTimeElapsed;
        System.out.println("\nTotal " + totalTimeElapsed + " ms");

    }
}