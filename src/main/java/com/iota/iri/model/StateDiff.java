package com.iota.iri.model;

import com.iota.iri.storage.Persistable;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paul on 5/6/17.
 */
public class StateDiff implements Persistable {
    public Map<Hash, String> state;

    public byte[] bytes() {
        return state.entrySet().parallelStream()
                .map(entry -> ArrayUtils.addAll(entry.getKey().bytes(), entry.getValue().getBytes()))
                .reduce(ArrayUtils::addAll)
                .orElse(new byte[0]);
    }
    public void read(byte[] bytes) {
        int i;
        state = new HashMap<>();
        if(bytes != null) {
            for (i = 0; i < bytes.length; i += Hash.SIZE_IN_BYTES + 371) {
                state.put(new Hash(bytes, i, Hash.SIZE_IN_BYTES), new String(Arrays.copyOfRange(bytes, i + Hash.SIZE_IN_BYTES, i + Hash.SIZE_IN_BYTES + 371)));

            }
        }
    }

    @Override
    public byte[] metadata() {
        return new byte[0];
    }

    @Override
    public void readMetadata(byte[] bytes) {
    }

    @Override
    public boolean merge() {
        return false;
    }
}
