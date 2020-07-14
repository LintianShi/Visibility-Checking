package datatype;

import trace.Invocation;

import java.util.HashMap;
import java.util.HashSet;

public class SimpleSet extends AbstractDataType {
    private HashSet<String> data = new HashSet<>();

    public String add(Invocation invocation) {
        data.add((String)invocation.getArguments().get(0));
        return invocation.getRetValue();
    }

    public String remove(Invocation invocation) {
        data.remove((String)invocation.getArguments().get(0));
        return invocation.getRetValue();
    }

    public String read(Invocation invocation) {
        return data.toString();
    }

    @Override
    public void reset() {
        data = new HashSet<>();
    }

    @Override
    public void print() {
        System.out.println(data.toString());
    }
}