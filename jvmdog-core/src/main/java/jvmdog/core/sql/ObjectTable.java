package jvmdog.core.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.AbstractQueryableTable;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableModify;
import org.apache.calcite.rel.logical.LogicalTableModify;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.ModifiableTable;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.util.Pair;

import jvmdog.core.sql.parse.TableDefinition;
import jvmdog.core.utils.DogUtils;

public abstract class ObjectTable extends AbstractQueryableTable 
    implements ModifiableTable{
    protected final TableDefinition tableDefinition;
    protected final Class<?> objectClass;
    
    protected ObjectTable(Class<?> objectClass){
        super(Object[].class);
        this.objectClass = objectClass;
        this.tableDefinition = new TableDefinition(objectClass);
    }

    public TableDefinition getTableDefinition() {
        return tableDefinition;
    }
    
    protected List<Object> getRows(){
        return Arrays.asList(DogUtils.getInstances(objectClass.getName()));
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        final List<RelDataType> types = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        
        Field[] fields = tableDefinition.getTableType().getDeclaredFields();
        for(Field field: fields){
            Class<?> fieldClass = field.getType();
            RelDataType javaType = null;
            if(fieldClass.isAssignableFrom(List.class)){
                javaType = typeFactory.createArrayType(typeFactory.createJavaType(Object.class), 100);
            } else {
                javaType = typeFactory.createJavaType(fieldClass);
            }
            
            RelDataType sqlType = typeFactory.createSqlType(javaType.getSqlTypeName());
            RelDataType fieldType = typeFactory.createTypeWithNullability(sqlType, true);
            
            names.add(field.getName().toUpperCase());
            types.add(fieldType);
        }
        
        return typeFactory.createStructType(Pair.zip(names, types));
    }
    
    @Override
    public <T> Queryable<T> asQueryable(
        final QueryProvider queryProvider, final SchemaPlus schema, final String tableName) {
      throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection getModifiableCollection() {
      return null;
    }
    
    @Override
    public TableModify toModificationRel(
        final RelOptCluster cluster,
        final RelOptTable table,
        final Prepare.CatalogReader catalogReader,
        final RelNode input,
        final TableModify.Operation operation,
        final List<String> updateColumnList,
        final List<RexNode> sourceExpressionList,
        final boolean flattened) {
      final LogicalTableModify logicalTableModify =
          new LogicalTableModify(
              cluster,
              cluster.traitSetOf(Convention.NONE),
              table,
              catalogReader,
              input,
              operation,
              updateColumnList,
              sourceExpressionList,
              flattened);
      registerRules(cluster.getPlanner(), logicalTableModify.getConvention());
      return logicalTableModify;
    }
    
    protected void registerRules(final RelOptPlanner planner, final Convention convention) {}

}
