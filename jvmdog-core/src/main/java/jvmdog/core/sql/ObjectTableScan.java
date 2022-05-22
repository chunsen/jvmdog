package jvmdog.core.sql;

import java.util.List;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.enumerable.EnumerableRelImplementor;
import org.apache.calcite.adapter.enumerable.JavaRowFormat;
import org.apache.calcite.adapter.enumerable.PhysType;
import org.apache.calcite.adapter.enumerable.PhysTypeImpl;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.MethodCallExpression;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.linq4j.tree.Primitive;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.collect.ImmutableList;

public class ObjectTableScan extends TableScan implements EnumerableRel {
    final ObjectTable objectTable;
    final int[] fields;
    private final List rows;

    protected ObjectTableScan(RelOptCluster cluster, RelOptTable table, ObjectTable objectTable, int[] fields, List rows) {
        super(cluster, cluster.traitSetOf(EnumerableConvention.INSTANCE), ImmutableList.of(), table);
        this.objectTable = objectTable;
        this.fields = fields;
        this.rows = rows;
    }
    
    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
        assert inputs.isEmpty();
        return new ObjectTableScan(getCluster(), table, objectTable, fields, rows);
    }

    @Override
    public RelWriter explainTerms(RelWriter pw) {
        return super.explainTerms(pw).item("fields", Primitive.asList(fields));
    }

    @Override
    public RelDataType deriveRowType() {
        final List<RelDataTypeField> fieldList = table.getRowType().getFieldList();
        final RelDataTypeFactory.Builder builder = getCluster().getTypeFactory().builder();
        for ( int field : fields ) {
            builder.add(fieldList.get(field));
        }
        return builder.build();
    }

    @Override
    public void register(RelOptPlanner planner) {
        planner.addRule(new ObjectTableModificationRule(RelFactories.LOGICAL_BUILDER, objectTable.getTableDefinition(), rows));
//        planner.addRule(CsvRules.PROJECT_SCAN);
    }

    @Override
    public @Nullable RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
        // Multiply the cost by a factor that makes a scan more attractive if it
        // has significantly fewer fields than the original scan.
        //
        // The "+ 2D" on top and bottom keeps the function fairly smooth.
        //
        // For example, if table has 3 fields, project has 1 field,
        // then factor = (1 + 2) / (3 + 2) = 0.6
//        return super.computeSelfCost(planner, mq)
//                .multiplyBy(((double) fields.length + 2D) / ((double) table.getRowType().getFieldCount() + 2D));
        
        return super.computeSelfCost(planner, mq).multiplyBy(.1);
    }

    @Override
    public Result implement(EnumerableRelImplementor implementor, Prefer pref) {
//        PhysType physType = PhysTypeImpl.of(implementor.getTypeFactory(), getRowType(), pref.preferArray());
      final PhysType physType =
      PhysTypeImpl.of(
          implementor.getTypeFactory(),
          this.getRowType(),
          pref.prefer(JavaRowFormat.CUSTOM));
      
      implementor.map.put("rows", this.rows);
        
//        return implementor.result(physType,
//                Blocks.toBlock(Expressions.call(table.getExpression(ObjectScannableTable.class), "scan",
//                        implementor.getRootExpression())));

        final BlockBuilder builder = new BlockBuilder();

        final List<String> qualifiedName = this.getTable().getQualifiedName();
        Expression enumerable =
            builder.append("enumerable", call(qualifiedName.get(0), qualifiedName.get(1)));
        // return enumerable;
        builder.add(Expressions.return_(null, enumerable));

        final BlockBuilder builder0 = new BlockBuilder(false);

        final ParameterExpression e_ = Expressions.parameter(Exception.class, builder.newName("e"));
        builder0.add(
            Expressions.tryCatch(
                builder.toBlock(),
                Expressions.catch_(
                    e_, Expressions.throw_(Expressions.new_(TableExecuteException.class, e_)))));
//        implementor.map.put(ParameterIndex.CONTEXT_PARAMETER_KEY, parameterIndex);
        return implementor.result(physType, builder0.toBlock());
    }
    
    private MethodCallExpression call(
        final String schemaName, final String tableName){
        return Expressions.call(
                ObjectScanInvoker.METHOD,
                Expressions.constant(schemaName),
                Expressions.constant(tableName),
                DataContext.ROOT);
    }
}
