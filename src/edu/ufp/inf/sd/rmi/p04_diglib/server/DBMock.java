package edu.ufp.inf.sd.rmi.p04_diglib.server;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class simulates a DBMockup for managing users and books.
 *
 * @author rmoreira
 *
 */
public class DBMock {

    private final ArrayList<Book> books;// = new ArrayList();
    private final ArrayList<User> users;// = new ArrayList();
    private final HashMap<String, DigLibSessionRI> sessions;// = new HashMap();

    /**
     * This constructor creates and inits the database with some books and users.
     */
    public DBMock() {
        books = new ArrayList();
        users = new ArrayList();
        sessions = new HashMap<>();
        //Add 3 books for testing purposes only...
        books.add(new Book("Distributed Systems: principles and paradigms", "Tanenbaum"));
        books.add(new Book("Distributed Systems: concepts and design", "Colouris"));
        books.add(new Book("Distributed Computing Networks", "Tanenbaum"));
        //Add one user for testing purposes only...
        users.add(new User("guest", "ufp"));
    }

    /**
     * Registers a new user.
     * 
     * @param u username
     * @param p passwd
     */
    public void register(String u, String p) {
        if (!exists(u, p)) {
            users.add(new User(u, p));
        }
    }

    /**
     * Checks the credentials of user.
     * 
     * @param u username
     * @param p passwd
     * @return
     */
    public boolean exists(String u, String p) {
        for (User usr : this.users) {
            if (usr.getUname().compareTo(u) == 0 && usr.getPword().compareTo(p) == 0) {
                return true;
            }
        }
        return false;
        //return ((u.equalsIgnoreCase("guest") && p.equalsIgnoreCase("ufp")) ? true : false);
    }

    /**
     * Inserts a new book into the DigLib.
     * 
     * @param t title
     * @param a authors
     */
    public void insert(String t, String a) {
        books.add(new Book(t, a));
    }

    /**
     * Looks up for books with given title and author keywords.
     * 
     * @param t title keyword
     * @param a author keyword
     * @return
     */
    public Book[] select(String t, String a) {
        Book[] abooks = null;
        ArrayList<Book> vbooks = new ArrayList();
        // Find books that match
        for (int i = 0; i < books.size(); i++) {
            Book book = (Book) books.get(i);
            System.out.println("DB - select(): book[" + i + "] = " + book.getTitle() + ", " + book.getAuthor());
            if (book.getTitle().toLowerCase().contains(t.toLowerCase()) && book.getAuthor().toLowerCase().contains(a.toLowerCase())) {
                System.out.println("DB - select(): add book[" + i + "] = " + book.getTitle() + ", " + book.getAuthor());
                vbooks.add(book);
            }
        }
        // Copy Vector->Array
        abooks = new Book[vbooks.size()];
        for (int i = 0; i < vbooks.size(); i++) {
            abooks[i] = (Book) vbooks.get(i);
        }
        return abooks;
    }

    /**
     * Get session from hashmap of sessions
     *
     * @param username
     * @return
     */
    protected DigLibSessionRI getDigLibSession(String username) {
        return this.sessions.get(username);
    }

    /**
     * Put session into hashmap of sessions
     *
     * @param username
     * @param session
     * @return
     */
    protected DigLibSessionRI putDigLibSession(String username, DigLibSessionRI session) {
        //this.pool.remove((Runnable) session);
        return this.sessions.put(username, session);
    }
    /**
     * Remove session from hashmap of sessions
     *
     * @param username
     * @param session
     * @return
     */
    protected boolean removeDigLibSession(String username, DigLibSessionRI session) {
        //this.pool.remove((Runnable) session);
        return this.sessions.remove(username, session);
    }
}
