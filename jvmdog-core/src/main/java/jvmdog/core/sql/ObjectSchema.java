package jvmdog.core.sql;

import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.SchemaVersion;
import org.apache.calcite.schema.Schemas;
import org.apache.calcite.schema.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectSchema implements Schema {
    private static final Logger logger = LoggerFactory.getLogger(ObjectSchema.class);
    
    private final Instrumentation inst;
    private final Map<String, Class<?>> classNameMap = new HashMap<>();
    
    public ObjectSchema(Instrumentation inst) {
        this.inst = inst;
    }
    
//    private Map<String, Table> createTableMap() {
//        Map<String, Table> result = new HashMap<>();
//        
//        Class<?>[] classes = inst.getAllLoadedClasses();
//        
//        for(Class<?> clazz: classes){
//            String className = clazz.getName();
//            if(className.startsWith("java")) {
//                continue;
//            }
//            
//            Table table = new ObjectScannableTable(clazz);
//            result.put(className.replace('.', '_').toUpperCase(), table);
//        }
//        
//        return result;
//    }
    
    private String classToTable(Class<?> clazz){
        String table = clazz.getName().replace('.', '_').toUpperCase();
        classNameMap.put(table, clazz);
        return table;
    }

    @Override
    public Table getTable(String name) {
        Class<?> clazz = classNameMap.get(name);
        if(clazz == null){
            Class<?>[] classes = inst.getAllLoadedClasses();
            
            for(Class<?> cls: classes){
                String tableName = classToTable(cls);
                if(tableName.equals(name)){
                    clazz = cls;
                    break;
                }
            }
        }
        if(clazz == null){
            logger.error("class not found for table {}", name);
        }
        
        return new ObjectScannableTable(clazz);
    }

    @Override
    public Set<String> getTableNames() {
        Class<?>[] classes = inst.getAllLoadedClasses();
        Set<String> names = new HashSet<>();
        
        for(Class<?> clazz: classes){
            names.add(classToTable(clazz));
        }
        
        return names;
    }

    @Override
    public RelProtoDataType getType(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getTypeNames() {
        return new HashSet<>();
    }

    @Override
    public Collection<Function> getFunctions(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getFunctionNames() {
        return new HashSet<>();
    }

    @Override
    public Schema getSubSchema(String name) {
        return null;
    }

    @Override
    public Set<String> getSubSchemaNames() {
        return new HashSet<>();
    }

    @Override
    public Expression getExpression(SchemaPlus parentSchema, String name) {
        return Schemas.subSchemaExpression(parentSchema, name, getClass());
    }

    @Override
    public boolean isMutable() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Schema snapshot(SchemaVersion version) {
        // TODO Auto-generated method stub
        return this;
    }
}
