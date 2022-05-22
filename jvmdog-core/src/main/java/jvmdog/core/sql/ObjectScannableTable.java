package jvmdog.core.sql;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptTable.ToRelContext;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.TranslatableTable;

public class ObjectScannableTable extends ObjectTable implements ScannableTable , TranslatableTable {

    private static final Type TYPE = Object[].class;

    public ObjectScannableTable(Class<?> objectClass) {
        super(objectClass);

    }

    protected void registerRules(RelOptPlanner planner, Convention convention) {
        planner.addRule(new ObjectTableModificationRule(RelFactories.LOGICAL_BUILDER, tableDefinition, this.getRows()));
    }
    
    
    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        final List<Object> rows = getRows();
        return new AbstractEnumerable<Object[]>() {
            @Override
            public Enumerator<Object[]> enumerator() {
                return new ObjectEnumerator(rows);
            }
        };
    }

    @Override
    public RelNode toRel(ToRelContext context, RelOptTable relOptTable) {
        final int fieldCount = relOptTable.getRowType().getFieldCount();
        final int[] fields = identityList(fieldCount);
        return new ObjectTableScan(context.getCluster(), relOptTable, this, fields, this.getRows());
    }
    
    private static int[] identityList(int n) {
        int[] integers = new int[n];
        for ( int i = 0; i < n; i++ ) {
            integers[i] = i;
        }
        return integers;
    }

}
