package com.iota.iri.model;

import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.storage.Persistable;
import com.iota.iri.utils.Converter;
import com.iota.iri.utils.Serializer;

import java.nio.ByteBuffer;

/**
 * Created by paul on 3/2/17 for iri.
 */
public class Transaction implements Persistable {
    public static final int SIZE = 6182;

    public byte[] bytes;

    public Hash address;
    public Hash bundle;
    public Hash trunk;
    public Hash branch;
    public Hash obsoleteTag;
    public String value;
    public String rangeProof;
    public Commitment vectorP;
    public long currentIndex;
    public long lastIndex;
    public long timestamp;
    public Boolean isNull = false;

    public Hash tag;
    public long attachmentTimestamp;
    public long attachmentTimestampLowerBound;
    public long attachmentTimestampUpperBound;

    public int validity = 0;
    public int type = TransactionViewModel.PREFILLED_SLOT;
    public long arrivalTime = 0;

    //public boolean confirmed = false;
    public boolean parsed = false;
    public boolean solid = false;
    public long height = 0;
    public String sender = "";
    public int snapshot;

    public byte[] bytes() {
        return bytes;
    }

    public void read(byte[] bytes) {
        if(bytes != null) {
            this.bytes = new byte[SIZE];
            System.arraycopy(bytes, 0, this.bytes, 0, SIZE);
            this.type = TransactionViewModel.FILLED_SLOT;
        }
    }

    @Override
    public byte[] metadata() {
        int allocateSize =
                Hash.SIZE_IN_BYTES * 6 + //address,bundle,trunk,branch,obsoleteTag,tag
                        Long.BYTES * 8+ //currentIndex,lastIndex,timestamp,attachmentTimestampLowerBound,attachmentTimestampUpperBound,arrivalTime,height
                        Integer.BYTES * 3 + //validity,type,snapshot
                        371 + 55 + 4171 + // value, vectorP and rangeproof
                        1 + //solid
                        sender.getBytes().length; //sender
        ByteBuffer buffer = ByteBuffer.allocate(allocateSize);
        buffer.put(address.bytes());
        byte[] tempByte = new byte[55];
        Converter.bytes(Converter.allocatingTritsFromTrytes(vectorP.initialValue), tempByte);
        buffer.put(tempByte);
        tempByte = new byte[371];
        Converter.bytes(Converter.allocatingTritsFromTrytes(value), tempByte);
        buffer.put(tempByte);
        tempByte = new byte[4171];
        Converter.bytes(Converter.allocatingTritsFromTrytes(rangeProof), tempByte);
        
        buffer.put(tempByte);
        buffer.put(bundle.bytes());
        buffer.put(trunk.bytes());
        buffer.put(branch.bytes());
        buffer.put(obsoleteTag.bytes());
        buffer.put(Serializer.serialize(currentIndex));
        buffer.put(Serializer.serialize(lastIndex));
        buffer.put(Serializer.serialize(timestamp));

        buffer.put(tag.bytes());
        buffer.put(Serializer.serialize(attachmentTimestamp));
        buffer.put(Serializer.serialize(attachmentTimestampLowerBound));
        buffer.put(Serializer.serialize(attachmentTimestampUpperBound));

        buffer.put(Serializer.serialize(validity));
        buffer.put(Serializer.serialize(type));
        buffer.put(Serializer.serialize(arrivalTime));
        buffer.put(Serializer.serialize(height));
        //buffer.put((byte) (confirmed ? 1:0));
        // TODO fix this
        byte isSolid;
        if (solid) {
            isSolid = 1;
        } else {
            isSolid = 0;
        }

        buffer.put(isSolid);
        //buffer.put((byte) 1);
        buffer.put(Serializer.serialize(snapshot));
        buffer.put(sender.getBytes());
        return buffer.array();
    }

    @Override
    public void readMetadata(byte[] bytes) {
        int i = 0;
        if(bytes != null) {
            address = new Hash(bytes, i, Hash.SIZE_IN_BYTES);
            
            i += Hash.SIZE_IN_BYTES;
            vectorP = new Commitment(bytes, i, 55);
            i += 55;
            byte[] blindByte = new byte[371];
            System.arraycopy(bytes, i, blindByte, 0, 371);
            int[] trits = new int[TransactionViewModel.VALUE_TRINARY_SIZE];
            Converter.getTrits(blindByte, trits);
            value = Converter.trytes(trits);
            i += 371;
            byte[] rangeByte = new byte[4171];
            System.arraycopy(bytes, i, rangeByte, 0, 4171);
            trits = new int[TransactionViewModel.RANGEPROOF_TRINARY_SIZE];
            Converter.getTrits(rangeByte, trits);
            rangeProof = Converter.trytes(trits);
            i += 4171;
            bundle = new Hash(bytes, i, Hash.SIZE_IN_BYTES);
            i += Hash.SIZE_IN_BYTES;
            trunk = new Hash(bytes, i, Hash.SIZE_IN_BYTES);
            i += Hash.SIZE_IN_BYTES;
            branch = new Hash(bytes, i, Hash.SIZE_IN_BYTES);
            i += Hash.SIZE_IN_BYTES;
            obsoleteTag = new Hash(bytes, i, Hash.SIZE_IN_BYTES);
            i += Hash.SIZE_IN_BYTES;
            currentIndex = Serializer.getLong(bytes, i);
            i += Long.BYTES;
            lastIndex = Serializer.getLong(bytes, i);
            i += Long.BYTES;
            timestamp = Serializer.getLong(bytes, i);
            i += Long.BYTES;

            tag = new Hash(bytes, i, Hash.SIZE_IN_BYTES);
            i += Hash.SIZE_IN_BYTES;
            attachmentTimestamp = Serializer.getLong(bytes, i);
            i += Long.BYTES;
            attachmentTimestampLowerBound = Serializer.getLong(bytes, i);
            i += Long.BYTES;
            attachmentTimestampUpperBound = Serializer.getLong(bytes, i);
            i += Long.BYTES;

            validity = Serializer.getInteger(bytes, i);
            i += Integer.BYTES;
            type = Serializer.getInteger(bytes, i);
            i += Integer.BYTES;
            arrivalTime = Serializer.getLong(bytes, i);
            i += Long.BYTES;
            height = Serializer.getLong(bytes, i);
            i += Long.BYTES;
            /*
            confirmed = bytes[i] == 1;
            i++;
            */
            if (bytes[i] == 1) {
                solid = true;
            } else {
                solid = false;
            }
            i++;
            snapshot = Serializer.getInteger(bytes, i);
            i += Integer.BYTES;
            byte[] senderBytes = new byte[bytes.length - i];
            if (senderBytes.length != 0) {
                System.arraycopy(bytes, i, senderBytes, 0, senderBytes.length);
            }
            sender = new String(senderBytes);
            parsed = true;
        }
    }

    @Override
    public boolean merge() {
        return false;
    }
}
