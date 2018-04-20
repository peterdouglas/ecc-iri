package com.iota.iri.model;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.Security;


public class Commitment implements Serializable {
    public ECPoint commitment;
    public static ECParameterSpec curveSpec;
    public static ECPoint zero;
    public String initialValue;

    private void init() {
        Security.addProvider(new BouncyCastleProvider());
        curveSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        zero = curveSpec.getCurve().createPoint(BigInteger.ZERO, BigInteger.ZERO);
    }
    public Commitment() {
        init();
        this.commitment = curveSpec.getCurve().createPoint(BigInteger.ZERO, BigInteger.ZERO);
    }
    
    public Commitment(byte[] byteComm) {
        createCommitment( byteComm);
    }

    public Commitment(byte[] bytes, int offset, int size) {
        byte[] byteComm = new byte[size];
        System.arraycopy(bytes, offset, byteComm, 0, size - offset > 49 ? bytes.length-offset: size);
        createCommitment( byteComm);
    }

    public ECPoint getCommitment() {
        return this.commitment;
    }

    public byte[] bytes() {
        return this.initialValue.getBytes();
    }

    private void createCommitment(byte[] byteComm) {
        init();
        this.initialValue = new String(byteComm);
        byte[] comm = new byte[34];
        System.arraycopy(byteComm, 0,  comm, 0, 34);
        this.commitment = curveSpec.getCurve().decodePoint(comm);
    }
}

