package jvmdog.core.sql.parse;

import java.util.List;

public class ObjectCondition {
    private final List<Object> operands;
    private final String operator;
    
    public ObjectCondition(List<Object> operands, String operator) {
        this.operands = operands;
        this.operator = operator;
    }

    public List<Object> getOperands() {
        return operands;
    }

    public String getOperator() {
        return operator;
    }
    
}
