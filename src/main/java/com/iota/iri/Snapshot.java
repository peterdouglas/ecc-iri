package com.iota.iri;
import com.iota.iri.model.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


public class Snapshot {
    private static final Logger log = LoggerFactory.getLogger(Snapshot.class);
    public static String SNAPSHOT_PUBKEY = "TTXJUGKTNPOOEXSTQVVACENJOQUROXYKDRCVK9LHUXILCLABLGJTIPNF9REWHOIMEUKWQLUOKD9CZUYAC";
    public static int SNAPSHOT_PUBKEY_DEPTH = 6;
    public static int SNAPSHOT_INDEX = 2;
    public static int SPENT_ADDRESSES_INDEX = 3;

    public static final Map<Hash, String> initialState = new HashMap<Hash, String>();
    public static final Snapshot initialSnapshot;
    public final ReadWriteLock rwlock = new ReentrantReadWriteLock();

    static {
	/* commenting out the signature validator -- TODO - add new sig
        if (!SignedFiles.isFileSignatureValid("/Snapshot.txt", "/Snapshot.sig", SNAPSHOT_PUBKEY, SNAPSHOT_PUBKEY_DEPTH, SNAPSHOT_INDEX)) {
            throw new RuntimeException("Snapshot signature failed.");
        }
	*/

        InputStream in = Snapshot.class.getResourceAsStream("/Snapshot.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length >= 2)
                {
                    String key = parts[0];
                    String value = parts[1];
                    initialState.put(new Hash(key),value);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load snapshot.");
            System.exit(-1);
        }

        initialSnapshot = new Snapshot(initialState, 0);
        /*long stateValue = initialState.values().stream().reduce(Math::addExact).orElse(Long.MAX_VALUE);
        if(stateValue != TransactionViewModel.SUPPLY) {
            log.error("Transaction resolves to incorrect ledger balance: {}", TransactionViewModel.SUPPLY - stateValue);
            System.exit(-1);
        }*/

        if(!isConsistent(initialState)) {
            System.out.println("Initial Snapshot inconsistent.");
            System.exit(-1);
        }
    }

    protected final Map<Hash, String> state;
    private int index;

    public int index() {
        int i;
        rwlock.readLock().lock();
        i = index;
        rwlock.readLock().unlock();
        return i;
    }

    private Snapshot(Map<Hash, String> initialState, int index) {
        state = new HashMap<>(initialState);
        this.index = index;
    }

    public Snapshot clone() {
        return new Snapshot(state, index);
    }

    public String getBalance(Hash hash) {
        String l;
        rwlock.readLock().lock();
        l = state.get(hash);
        rwlock.readLock().unlock();
        return l;
    }

    public Map<Hash, String> patchedDiff(Map<Hash, String> diff) {
        Map<Hash, String> patch;
        rwlock.readLock().lock();
        patch = diff.entrySet().stream().map(hashLongEntry ->
            new HashMap.SimpleEntry<>(hashLongEntry.getKey(), hashLongEntry.getValue())
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        rwlock.readLock().unlock();
        return patch;
    }

    void apply(Map<Hash, String> patch, int newIndex) {
        /*if (!patch.entrySet().stream().map(Map.Entry::getValue)) {
            throw new RuntimeException("Diff is not consistent.");
        }*/
        rwlock.writeLock().lock();
        patch.entrySet().stream().forEach(hashLongEntry -> {
            //if (state.computeIfPresent(hashLongEntry.getKey(), (hash, aLong) -> hashLongEntry.getValue()) == null) {
                state.put(hashLongEntry.getKey(), hashLongEntry.getValue());
           // }
        });
        index = newIndex;
        rwlock.writeLock().unlock();
    }

    public static boolean isConsistent(Map<Hash, String> state) {
        final Iterator<Map.Entry<Hash, String>> stateIterator = state.entrySet().iterator();
        while (stateIterator.hasNext()) {

          final Map.Entry<Hash, String> entry = stateIterator.next();
           if (entry.getValue().startsWith("99999999999")) {
                System.out.println("Address has a negative value" + entry.getKey());
            }
            //////////// --Coo only--
                /*
                 * if (entry.getValue() > 0) {
                 *
                 * System.out.ln("initialState.put(new Hash(\"" + entry.getKey()
                 * + "\"), " + entry.getValue() + "L);"); }
                 */
            ////////////
        }
        return true;
    }
}
