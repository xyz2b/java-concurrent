package org.xyz.concurrent.concurrent.resultcache;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ExpensiveFunction implements Computable<String, BigInteger> {

    @Override
    public BigInteger compute(String arg) throws InterruptedException {
        return new BigInteger(arg);
    }
}
