package jvmdog.core.sql;

import java.lang.instrument.Instrumentation;
import java.util.Map;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

public class ObjectSchemaFactory implements SchemaFactory{
    /** Public singleton, per factory contract. */
    public static final ObjectSchemaFactory INSTANCE = new ObjectSchemaFactory();
    
    private Instrumentation inst;

    private ObjectSchemaFactory() {
    }
    
    public void setInstrumentation(Instrumentation inst) {
        this.inst = inst;
    }

    @Override
    public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
        return new ObjectSchema(inst);
    }

}
