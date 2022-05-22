package jvmdog.core.sql;

import java.util.List;
import java.util.function.Predicate;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.TableModify;
import org.apache.calcite.schema.ModifiableTable;
import org.apache.calcite.tools.RelBuilderFactory;

import jvmdog.core.sql.parse.TableDefinition;

public class ObjectTableModificationRule extends ConverterRule{
    private final TableDefinition tableDefinition;
    protected final List rows;
    
    public ObjectTableModificationRule(
            final RelBuilderFactory relBuilderFactory,
            TableDefinition tableDefinition,
            List rows) {
          super(
              TableModify.class,
              (Predicate<RelNode>) r -> true,
              EnumerableConvention.INSTANCE,
              EnumerableConvention.INSTANCE,
              relBuilderFactory,
              "SqlToHttpPostConverterRule");
          this.tableDefinition = tableDefinition;
          this.rows = rows;
        }

    @Override
    public RelNode convert(RelNode rel) {
        final TableModify modify = (TableModify) rel;
        if (modify instanceof ObjectTableModify) {
          return modify;
        }
        final ModifiableTable modifiableTable = modify.getTable().unwrap(ModifiableTable.class);
        if (modifiableTable == null) {
          return null;
        }
        
        final RelTraitSet traitSet = modify.getTraitSet().replace(EnumerableConvention.INSTANCE);
        return new ObjectTableModify(
            modify.getCluster(),
            traitSet,
            modify.getTable(),
            modify.getCatalogReader(),
            convert(modify.getInput(), traitSet),
            modify.getOperation(),
            modify.getUpdateColumnList(),
            modify.getSourceExpressionList(),
            modify.isFlattened(),
            tableDefinition,
            rows);
    }
}
