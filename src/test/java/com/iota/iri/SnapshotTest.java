package com.iota.iri;

import com.iota.iri.model.Hash;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by paul on 4/12/17.
 */
public class SnapshotTest {
    @Test
    public void getState() throws Exception {
        Snapshot latestSnapshot = Snapshot.initialSnapshot.clone();
        Assert.assertTrue(latestSnapshot.state.equals(Snapshot.initialState));
    }

    @Test
    public void isConsistent() throws Exception {
        Assert.assertTrue("Initial confirmed should be consistent", Snapshot.isConsistent(Snapshot.initialState));
    }

    @Test
    public void patch() throws Exception {
        Map.Entry<Hash, String> firstOne = Snapshot.initialState.entrySet().iterator().next();
        Hash someHash = new Hash("PSRQPWWIECDGDDZXHGJNMEVJNSVOSMECPPVRPEVRZFVIZYNNXZNTOTJOZNGCZNQVSPXBXTYUJUOXYASLS");
        Map<Hash, String> diff = new HashMap<>();
        diff.put(firstOne.getKey(), "99999");
        diff.put(someHash, firstOne.getValue());

        Assert.assertNotEquals(0, diff.size());
        Assert.assertTrue("The ledger should be consistent", Snapshot.isConsistent(Snapshot.initialSnapshot.patchedDiff(diff)));
    }

    @Test
    public void applyShouldFail() throws Exception {
        //Snapshot latestSnapshot = Snapshot.initialSnapshot.clone();
        //Map<Hash, String> badMap = new HashMap<>();
        //badMap.put(new Hash("PSRQPWWIECDGDDZEHGJNMEVJNSVOSMECPPVRPEVRZFVIZYNNXZNTOTJOZNGCZNQVSPXBXTYUJUOXYASLS"), 100L);
        //badMap.put(new Hash("ESRQPWWIECDGDDZEHGJNMEVJNSVOSMECPPVRPEVRZFVIZYNNXZNTOTJOZNGCZNQVSPXBXTYUJUOXYASLS"), -100L);
//        Map<Hash, String> patch = latestSnapshot.patchedDiff(badMap);
        //assertFalse("should be inconsistent", Snapshot.isConsistent(latestSnapshot.patchedDiff(badMap)));
    }

    private Map<Hash, Long> getModifiedMap() {
        Hash someHash = new Hash("PSRQPWWIECDGDDZXHGJNMEVJNSVOSMECPPVRPEVRZFVIZYNNXZNTOTJOZNGCZNQVSPXBXTYUJUOXYASLS");
        Map<Hash, Long> newMap;
        newMap = new HashMap<>();
        Iterator<Map.Entry<Hash, Long>> iterator = newMap.entrySet().iterator();
        Map.Entry<Hash, Long> entry;
        if(iterator.hasNext()) {
            entry = iterator.next();
            Long value = entry.getValue();
            Hash hash = entry.getKey();
            newMap.put(hash, 0L);
            newMap.put(someHash, value);
        }
        return newMap;
    }
}