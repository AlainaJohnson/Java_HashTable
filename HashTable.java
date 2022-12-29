import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HashTable<T>{
    NGen<T>[] hashTable;
    public String type = "general";
    private int len;


    //TODO: Create a constructor that takes in a length and initializes the hash table array
    public HashTable(int len){
        this.len = len;
        hashTable = new NGen[len];
    }

    //TODO: Implement a simple hash function
    //hash functions were created from 1-4 with 1 being the worst and 4 being the best, hash for is used for the specific and general cases
    //we found that using odd and prime numbers makes the indexes between different tokens more unique
    public int hash1(T item) {//general way to add, not the best, dividing the length of the token by the len of the hash
        //when using the longest chain for gettysburg is 28, and 10 for keywords
        return item.toString().length() % len;
    }

    //TODO: Implement a second (different and improved) hash function
    public int hash2(T item) {//another way to add items, find the length of the first char the divide by length
        //when using the longest chain for gettysburg is 13 and 8 for the keywords
        return (item.toString().charAt(0)) % len;

    }
    private int hash3(T item){//adding first and last character together.  used as general case
        // when using the longest chain for gettysburg is 8 and 5 for keywords
        return ((item.toString().charAt(0) + item.toString().charAt(item.toString().length()-1))) % len;

    }
    //hash4 works best for the specific cases
    private int hash4(T item){//adding all characters together, and then if i is even add three, else add 7 to the sum
        //type = "specific";
        int sum = 0;
        for (int i = 0; i < item.toString().length(); i++){
            sum += item.toString().charAt(i);
            if (i % 2 == 0){
                sum += 3;
            }
            else {
                sum += 7;
            }
        }// adding three and then times by 7 and then divide by len to try to get the most unique indices possible
        return (7 * (sum + 11)) % len;//needs to be + 3 instead of - 3 to make sure no out of bounds error
    }


    //TODO: Implement the add method which adds an item to the hash table using your best performing hash function
    // Does NOT add duplicate items
    public void add(T item) {
        int idx = 0;
        if (type.equals("general")){//both the general and specific hashes will use hash4
            idx = hash3(item);
        }
        else {
            idx = hash4(item);
        }
        NGen<T> search = hashTable[idx];//gets index
        NGen<T> trailer = search;
        if (trailer == null){//if there are no elements then create a new ngen
            hashTable[idx] = new NGen<T>(item, null);
            return;
        }
        trailer = search;
        NGen<T> ptr = search.getNext();
        if (ptr == null && trailer.getData().equals(item)){//if item is already there don't add again
            return;
        }
        while (ptr != null){//looping through
            if (trailer.getData().equals(item)){//if item is already there don't add again
                return;
            }
            else {
                trailer = ptr;//getting next data
                ptr = ptr.getNext();
            }

        }
        trailer.setNext(new NGen<T>(item, null));//chaining new item
    }

    // ** Already implemented -- no need to change **
    // Adds all words from a given file to the hash table using the add(T item) method above
    @SuppressWarnings("unchecked")
    public void addWordsFromFile(String fileName) {
        Scanner fileScanner = null;
        String word;
        try {
            fileScanner = new Scanner(new File(fileName));
        }
        catch (FileNotFoundException e) {
            System.out.println("File: " + fileName + " not found.");
            System.exit(1);
        }
        while (fileScanner.hasNext()) {
            word = fileScanner.next();
            word = word.replaceAll("\\p{Punct}", ""); // removes punctuation
            this.add((T) word);
        }
    } //not my code, code from project description.



    //TODO: Implement the display method which prints the indices of the hash table and the number of words "hashed"
    // to each index. Also prints:
    // - total number of unique words
    // - number of empty indices
    // - number of nonempty indices
    // - average collision length
    // - length of longest chain
    public void display() {
        int uniqueWords = 0;
        int emptyIndices = 0;
        int nonEmptyIndices = 0;
        int longest = 0;
        int avgCollisions = 0;
        for (int i = 0; i < this.len; i++){//looping through the length of the hashtable
            uniqueWords += count(hashTable[i]);//adding up all unique words
            if (count(hashTable[i]) > longest){//finding the longest chain of words
                longest = count(hashTable[i]);
            }
            if (count(hashTable[i]) == 0){//checking for empty indices
                emptyIndices++;
            }
            else {//checking for nonempty indices
                nonEmptyIndices++;
            }
            System.out.println(i + ": " + count(hashTable[i]));//printing out the index and the amount chained
        }
        if (nonEmptyIndices != 0){//checking so no errors are produced by dividing by 0
            avgCollisions = (uniqueWords/nonEmptyIndices);
        }//printing out all information needed
        System.out.println("unique words: " + uniqueWords);
        System.out.println("empty indices: " + emptyIndices);
        System.out.println("nonempty indices: " + nonEmptyIndices);
        System.out.println("average collision: " + avgCollisions);
        System.out.println("length of longest chain: " + longest);
    }

    public int count(NGen<T> start){
        NGen<T> trailer = start;
        if (trailer == null){//if trailer is null then there are no elements
            return 0;
        }
        NGen<T> ptr = start.getNext();
        if (ptr == null){//if ptr is null then there is only 1 element
            return 1;
        }
        int count = 0;
        while (trailer != null){//counting how many elements are chained together
            count++;
            trailer = trailer.getNext();
        }
        return count;
    }

    // TODO: Create a hash table, store all words from "canterbury.txt", and display the table
    //  Create another hash table, store all words from "keywords.txt", and display the table
    public static void main(String args[]) {
        Scanner s = new Scanner("canterbury.txt");
        HashTable<String> hash = new HashTable<>(100);
        hash.type = "specific";//can be changed to run the general case
        hash.addWordsFromFile("canterbury.txt");
        hash.display();

        HashTable<String> hash2 = new HashTable<>(100);
        //no type specifics so will default to general

        //the smallest our hashtable can be before collisions start is 73
        //when the length of the table is near the number of unique keywords we ar able to keep
        //the distributions even with a longest chain length of 4
        s = new Scanner("keywords.txt");
        hash2.addWordsFromFile("keywords.txt");
        hash2.display();
    }
}
