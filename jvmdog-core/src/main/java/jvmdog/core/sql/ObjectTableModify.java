package jvmdog.core.sql;

import java.util.List;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.enumerable.EnumerableCalc;
import org.apache.calcite.adapter.enumerable.EnumerableRel;
import org.apache.calcite.adapter.enumerable.EnumerableRelImplementor;
import org.apache.calcite.adapter.enumerable.EnumerableValues;
import org.apache.calcite.adapter.enumerable.JavaRowFormat;
import org.apache.calcite.adapter.enumerable.PhysType;
import org.apache.calcite.adapter.enumerable.PhysTypeImpl;
import org.apache.calcite.linq4j.tree.BlockBuilder;
import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.linq4j.tree.MethodCallExpression;
import org.apache.calcite.linq4j.tree.ParameterExpression;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableModify;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexProgram;
import org.apache.calcite.sql.SqlUpdate;

import com.google.common.collect.ImmutableList;

//import com.google.common.collect.ImmutableList;

import jvmdog.core.sql.parse.ObjectUpdate;
import jvmdog.core.sql.parse.Parser;
import jvmdog.core.sql.parse.TableDefinition;

public class ObjectTableModify extends TableModify implements EnumerableRel {
    private final TableDefinition tableDefinition;
    protected final List rows;
    
    public ObjectTableModify(final RelOptCluster cluster, final RelTraitSet traitSet,
            final RelOptTable table, final Prepare.CatalogReader catalogReader, final RelNode input,
            final Operation operation, final List<String> updateColumnList, final List<RexNode> sourceExpressionList,
            final boolean flattened, TableDefinition tableDefinition, List rows) {
        super(cluster, traitSet, table, catalogReader, input, operation, updateColumnList, sourceExpressionList,
                flattened);
        this.tableDefinition = tableDefinition;
        this.rows = rows;
    }
    
    public TableDefinition getTableDefinition() {
        return tableDefinition;
    }

    @Override
    public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery mq) {
      return super.computeSelfCost(planner, mq).multiplyBy(.1);
    }
    
    @Override
    public RelNode copy(RelTraitSet traitSet, List<RelNode> inputs) {
      return new ObjectTableModify(
          getCluster(),
          traitSet,
          getTable(),
          getCatalogReader(),
          sole(inputs),
          getOperation(),
          getUpdateColumnList(),
          getSourceExpressionList(),
          isFlattened(),
          tableDefinition,
          rows);
    }

    @Override
    public Result implement(EnumerableRelImplementor implementor, Prefer pref) {
        RelDataType rowType = this.input.getRowType();
        for(RelDataTypeField typeField: rowType.getFieldList()){
            System.out.println(typeField);
        }
        if(input instanceof EnumerableValues){
            ImmutableList<ImmutableList<RexLiteral>> tuples = ((EnumerableValues)input).tuples;
            System.out.print("(");
            for(ImmutableList<RexLiteral> list: tuples){
                for(RexLiteral item: list){
                    System.out.print(item + ",");
                }
            }
            System.out.println(")");
        } else if(input instanceof EnumerableCalc){
            RexProgram program = ((EnumerableCalc)input).getProgram();
            List<RexNode> nodes = program.getExprList();
            System.out.print("(");
            for(RexNode node: nodes){
                System.out.print(node + ",");
            }
            System.out.println(")");
        }
      final BlockBuilder builder = new BlockBuilder();
      final PhysType physType =
          PhysTypeImpl.of(
              implementor.getTypeFactory(),
              this.getRowType(),
              pref.prefer(JavaRowFormat.CUSTOM));

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
//      implementor.map.put(ParameterIndex.CONTEXT_PARAMETER_KEY, parameterIndex);
      
      if(this.getOperation() == Operation.UPDATE){
          SqlUpdate update= (SqlUpdate)Parser.getStatement(this, implementor.getTypeFactory());
          ObjectUpdate objectUpdate = Parser.parseUpdate(update, tableDefinition);
          implementor.map.put("objectUpdate", objectUpdate);
      }
      
      implementor.map.put("rows", this.rows);
      
      return implementor.result(physType, builder0.toBlock());
    }

    private MethodCallExpression call(
        final String schemaName, final String tableName){
        Operation operation = this.getOperation();
        if(operation == Operation.INSERT){
            return Expressions.call(
                    ObjectInsertInvoker.METHOD,
                    Expressions.constant(schemaName),
                    Expressions.constant(tableName),
                    DataContext.ROOT);
        } else if(operation == Operation.UPDATE){
            return Expressions.call(
                    ObjectUpdateInvoker.METHOD,
                    Expressions.constant(schemaName),
                    Expressions.constant(tableName),
                    DataContext.ROOT);
        } else {
            throw new UnsupportedOperationException();
        }
        
    }
}
