package jvmdog.core.sql;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.tree.Types;

public class ObjectScanInvoker {
    
    public static final Method METHOD = Types.lookupMethod(ObjectScanInvoker.class, "execute", String.class, String.class, DataContext.class);

    public static Enumerable<?> execute(
            final String schemaName, final String tableName, final DataContext context) {
        List rows = (List)context.get("rows");
        System.out.println("scan: tableName:" + tableName + ";" + rows);
        
        return new AbstractEnumerable<Object[]>() {
            @Override
            public Enumerator<Object[]> enumerator() {
                return new ObjectEnumerator(rows);
            }
        };
    }
}
