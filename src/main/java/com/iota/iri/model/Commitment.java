package com.iota.iri.model;

import com.iota.iri.controllers.TransactionViewModel;
import com.iota.iri.utils.Converter;
import com.iota.iri.utils.TrytesConverter;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.math.ec.ECPoint;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.Security;


public class Commitment implements Serializable  {
    public ECPoint commitment;
    public static ECPoint zero;
    public String initialValue;
    public Boolean nullValue = false;

    private void init() {
        Security.addProvider(new BouncyCastleProvider());
        zero = ECKey.CURVE.getCurve().createPoint(BigInteger.ZERO, BigInteger.ZERO);
    }
    public Commitment() {
        init();
        this.commitment = zero;
    }
    
    public Commitment(String strVal) {
        this.initialValue = strVal;
        createCommitment( strVal);
    }

    public Commitment(byte[] bytes, int offset, int size) {
        byte[] byteComm = new byte[size];
        System.arraycopy(bytes, offset, byteComm, 0, size - offset > 55 ? bytes.length-offset: size);
       
        int[] tritAdd = new int[TransactionViewModel.VECTORP_TRINARY_SIZE];
        Converter.getTrits(byteComm, tritAdd);
        this.initialValue = Converter.trytes(tritAdd);
        createCommitment( this.initialValue);
    }

    public ECPoint getCommitment() {
        return this.commitment;
    }

    public byte[] bytes() {
        return this.initialValue.getBytes();
    }

    private void createCommitment(String convStr) {
        init();
        int removeChar = 0;
        for (int i = convStr.length()-1; i >= 0 ; i--) {

            if (convStr.charAt(i) == '9') {
                removeChar++;
            } else {
                break;
            }
        }
        if (removeChar != convStr.length()) {
            try {
                String ascKey = TrytesConverter.toString(convStr.substring(0, convStr.length() - removeChar));
                byte[] byteKey = Base58.decode(ascKey);

                //byte[] comm = new byte[33];
                //System.arraycopy(byteKey, 0, comm, 0, 33);

                this.commitment = ECKey.CURVE.getCurve().decodePoint(byteKey);
                //tritAdd = new int[comm.length*3];
                //Converter.getTrits(comm, tritAdd);
                //String tempCom = Converter.trytes(tritAdd);
                //this.commitment = curveSpec.getCurve().createPoint(tempKey.getPubKeyPoint().getXCoord().toBigInteger(), tempKey.getPubKeyPoint().getYCoord().toBigInteger());
            }catch (Exception e) {
                this.commitment = zero;
            }
        } else {
            this.commitment = zero;
        }


    }
}

