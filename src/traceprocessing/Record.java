package traceprocessing;

import datatype.AbstractDataType;
import history.Invocation;

import java.util.ArrayList;
import java.util.List;

public class Record implements Comparable<Record> {
    private long startTime;
    private long endTime;
    private String operationName;
    private List<String> arguments = new ArrayList<>();
    private String retValue;

    public Record(String line) throws Exception {
        String[] cols = line.split(",");
        if (cols.length < 4) {
            throw new Exception();
        }
        this.startTime = Long.parseLong(cols[0]);
        this.endTime = Long.parseLong(cols[1]);
        this.operationName = "rwfz" + cols[2];
        this.retValue = cols[cols.length - 1];
        for (int i = 3; i < cols.length - 1; i++) {
            arguments.add(cols[i]);
        }
    }

    public Invocation generateInvocation(AbstractDataType adt) {
        if (adt == null) {
            System.out.println("ADT is null");
            return null;
        }
        return adt.generateInvocation(this);
    }

    public String getOperationName() {
        return operationName;
    }

    public String getRetValue() {
        return retValue;
    }

    public String getArgument(int index) {
        return arguments.get(index);
    }

    @Override
    public int compareTo(Record o) {
        if (o.startTime > this.endTime) {
            return -1;
        } else if (o.endTime < this.startTime) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return Long.toString(startTime) + "," + Long.toString(endTime) + "," + operationName;
    }
}
