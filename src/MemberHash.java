import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Panagiotis Stathopoulos (19064087)
 *
 */

public class MemberHash implements IMemberDB {
    /* ################################################################################################# */

                                                /** Settings **/

    //Enable logging actions to a file for Member addition, Member retrieval, and Member removal
    //Logs to "HashtableLog.txt"
    final static boolean logActions = true;

    /* ################################################################################################# */

    int HTS = 11; //Hash table capacity
    private int size = 0; // 0 <= size <= HTS;
    public ArrayList<Integer> bucket_visits;
    Member[] hashtable;
    boolean[] inactivityTable;

    /**
     * Default constructor
     */
    public MemberHash() {
        System.out.println("Hash Table");
        hashtable = new Member[HTS];
        inactivityTable = new boolean[HTS];
    }

    /**
     * Constructor with a custom HTS
     * Must be HTS > 0
     *
     * @param custom_HTS table size
     */
    public MemberHash(int custom_HTS) {
        assert custom_HTS > 0;

        System.out.println("Hash Table");
        hashtable = new Member[custom_HTS];
        inactivityTable = new boolean[custom_HTS];
        this.HTS = custom_HTS;
    }

    /**
     * Empties the database.
     *
     * @pre true
     */
    @Override
    public void clearDB() {
        assert !isEmpty();
        hashtable = new Member[HTS];
        inactivityTable = new boolean[HTS];
        size = 0;
    }

    /**
     * Determines whether a member's name exists as a key inside the database
     *
     * @param name the member name (key) to locate
     * @return true if the name exists as a key in the database
     * @pre name is not null and not empty string
     */
    @Override
    public boolean containsName(String name) {
        assert name != null && !name.equals("");
        return get(name) != null;
    }

    /**
     * Returns a Member object mapped to the supplied name.
     *
     * @param name The Member name (key) to locate
     * @return the Member object mapped to the key name if the name
     * exists as key in the database, otherwise null
     * @pre name not null and not empty string
     */
    @Override
    public Member get(String name) {
        assert name != null && !name.equals("");
        bucket_visits = new ArrayList<>();

        int position = hash(name);
        position = quadraticProbing(name, position);
        if (logActions) {
            try {
                Log.saveToFile("(>) An attempt to retrieve [" + name + "] was made with a hash value of [" + hash(name) + "] with visited buckets of " + bucket_visits + "\n");
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return hashtable[position];
    }

    /**
     * Returns the number of members in the database
     *
     * @return number of members in the database.
     * @pre true
     */
    @Override
    public int size() {
        assert size >= 0;
        return size;
    }

    /**
     * Determines if the database is empty or not.
     *
     * @return true if the database is empty
     * @pre true
     */
    @Override
    public boolean isEmpty() {
        return loadfactor(false) == 0;
    }

    /**
     * Inserts a Member object into the database, with the key of the supplied
     * member's name.
     * Note: If the name already exists as a key, then the original entry
     * is overwritten.
     * This method must return the previous associated value
     * if one exists, otherwise null
     *
     * @param member new entry
     * @pre member not null and member name not empty string
     * @return old entry if it overwrites it, null if bucket was empty
     */
    @Override
    public Member put(Member member) {
        assert member != null && !member.fullname.equals("");
        int position;
        Member oldMember = null;
        bucket_visits = new ArrayList<>();

        //find a position
        position = hash(member.fullname);
        //check if the found position is occupied, if so find a new position
        if (hashtable[position] != null)
            position = quadraticProbing(member.fullname, position);
        //check if new/old position is occupied (means a member with same key exists at position)
        if (hashtable[position] != null) {
            oldMember = hashtable[position];
            hashtable[position] = member;
        }
        //else the bucket is empty so place new element there
        else {
            hashtable[position] = member;
            size++;
        }
        System.out.println("Load factor is at %" + loadfactor(true));
        /* Make a new array when load factor is more than 50% */
        if (loadfactor(false) > 0.5)
            rehash();

        if (logActions) {
            try {
                Log.saveToFile("(+) Member [" + member.fullname + "] was added to the database with a hash value of ["
                        + hash(member.fullname) + "] and visited these buckets "
                        + bucket_visits + "\n    Load factor is at [%"
                        + loadfactor(true) + "] with a table size of ["
                        + size + "]\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return oldMember;
    }

    /**
     * Hashes values using the (hash_value % HTS) formula to find table position
     *
     * @param key member name
     * @return final table position
     */
    public int hash(String key) {
        assert key != null && !key.equals("");
        int value = 0;
        for(int i = 0; i < key.length(); i++) {
            if (key.charAt(i) != ',' && key.charAt(i) != ' ')
                value += (int)key.charAt(i);
        }
        value %= HTS;

        return value;
    }

    /**
     * Uses quadratic probing as a collision resolution strategy
     *
     * @param name key
     * @param position table address
     * @return empty position bucket or a position bucket with the same key
     */
    private int quadraticProbing(String name, int position) {
        assert name != null && !name.equals("") && position >= 0;
        int i = 1;

        while ((hashtable[position] != null && !hashtable[position].fullname.equals(name)) || inactivityTable[position]) {
            position = (position + i*i) % HTS;
            i++;
            bucket_visits.add(position);
        }

        return position;
    }

    /**
     * Calculates the current load factor
     *
     * @return load factor value
     */
    private float loadfactor(boolean returnAsPercentage) {
        assert size >= 0 && HTS != 0;
        if (returnAsPercentage)
            return ((float)size / (float)HTS) * 100;
        else
            return (float)size / (float)HTS;
    }

    /**
     * Rehash the entire table into a double-sized new one when load factor is > 50%
     *
     * @pre loadfactor > 0.5
     */
    private void rehash() {
        assert loadfactor(false) > 0.5;

        size = 0;
        Member[] hashtableClone = new Member[HTS];
        boolean[] inactivityTableClone = new boolean[HTS];
        System.arraycopy(hashtable, 0, hashtableClone, 0, HTS);
        System.arraycopy(inactivityTable, 0, inactivityTableClone, 0, HTS);
        HTS *= 2;
        hashtable = new Member[HTS];
        inactivityTable = new boolean[HTS];

        if (logActions) {
            try {
                Log.saveToFile("\n >>> TABLE REHASH <<< \n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < hashtableClone.length; i++) {
            if (hashtableClone[i] != null && !inactivityTable[i]) {
                put(hashtableClone[i]);
            }
        }
    }

    /**
     * Removes and returns a member from the database, with the key
     * the supplied name.
     *
     * @param name The name (key) to remove.
     * @return the removed member object mapped to the name, or null if
     * the name does not exist.
     * @pre name not null and name not empty string
     */
    @Override
    public Member remove(String name) {
        assert name != null && !name.equals("");

        if (!containsName(name)) return null;
        if (logActions) {
            try {
                Log.saveToFile("(-) An attempt to remove [" + name + "] was made with a hash value of [" + hash(name) + "] with visited buckets of " + bucket_visits + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int position = hash(name);
        position = quadraticProbing(name, position);
        if (hashtable[position].fullname.equals(name)) {
            inactivityTable[position] = true;
            size--;
            System.out.println("Load factor is at %" + loadfactor(true));
        }

        return hashtable[position];
    }

    /**
     * Quicksort algorithm able to sort member's names by alphabetical order
     *
     * @param array the array to sort through
     * @param low array starting position
     * @param high array length
     */
    private void quicksort(String[] array, int low, int high) {
        assert array != null;
        String temp;
        int i = low, j = high;
        String pivot = array[(low + high) / 2];
        while (i <= j) {
            while (array[i].charAt(0) < pivot.charAt(0)) i++;
            while (array[j].charAt(0) > pivot.charAt(0)) j--;
            if (i <= j) {
                temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
        if (low < j)
            quicksort(array, low, j);
        if (i < high)
            quicksort(array, i, high);
    }

    /**
     * Makes an array clone
     *
     * @param members array to be cloned
     * @return clone
     */
    private String[] copyover(Member[] members) {
        assert members != null;
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < members.length; i++) //get all the members names and add them to a list
            if (members[i] != null && !inactivityTable[i]) {
                list.add(members[i].fullname);
            }

        return list.toArray(new String[0]);
    }

    /**
     * Prints the names and affiliations of all the members in the database in
     * alphabetic order.
     *
     * @pre true
     */
    @Override
    public void displayDB() {
        System.out.println();
        System.out.println("############################################");
        System.out.println("                 DATABASE                   ");
        System.out.println("############################################");
        if (isEmpty())
            System.out.println("No entries...");
        else {
            String[] sorted = copyover(hashtable);
            quicksort(sorted, 0, sorted.length - 1);
            for (int i = 0; i < sorted.length; i++) {
                if (sorted[i] != null && !inactivityTable[i])
                    System.out.println(sorted[i]);
            }
        }
        System.out.println("############################################");
        System.out.println("Load factor is at %" + loadfactor(true));
        System.out.println("Size " + size());
        System.out.println("############################################");
        System.out.println();
    }
}
