package jvmdog.core.sql.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.interpreter.Bindables;
import org.apache.calcite.sql.SqlAbstractDateTimeLiteral;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlCharStringLiteral;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNumericLiteral;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlUpdate;
import org.apache.calcite.sql.dialect.MysqlSqlDialect;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import jvmdog.core.sql.ObjectTableModify;
import jvmdog.core.sql.ParameterIndex;
import jvmdog.core.sql.SqlScanNode;
import jvmdog.core.sql.TableInsertSqlConverter;

public class Parser {
    public static SqlNode getStatement(ObjectTableModify tableModify, JavaTypeFactory typeFactory){
        final TableInsertSqlConverter sqlConverter = new TableInsertSqlConverter(MysqlSqlDialect.DEFAULT, typeFactory);
        final TableInsertSqlConverter.Result result = sqlConverter.visitRoot(tableModify);
        final SqlNode fullSqlStatement = result.asStatement();
        return fullSqlStatement;
    }
    
    public static ObjectUpdate parseUpdate(SqlUpdate update, TableDefinition tableDefinition){
        int size = update.getSourceExpressionList().size();
        List<ObjectParameter> parameters = new ArrayList<>(size);
        for(int i=0; i< size; i++){
            SqlNode valueNode = update.getSourceExpressionList().get(i);
            SqlNode columnNode = update.getTargetColumnList().get(i);
            Pair<Object, Integer>  pair = getSqlNodeValue(valueNode);
            
            ColumnDefinition column = tableDefinition.getColumn(columnNode.toString());
            ObjectParameter param = new ObjectParameter(column, pair.getLeft());
            parameters.add(param);
        }
        
        SqlCall conditionNode = (SqlCall)update.getCondition();
        ObjectCondition condition = parseCondition(conditionNode, tableDefinition);
        
        return new ObjectUpdate(tableDefinition, parameters, condition);
    }
    
    private static ObjectCondition parseCondition(SqlCall conditionNode, TableDefinition tableDefinition){
        String operator = conditionNode.getOperator().toString();
        List<Object> operands = new ArrayList<>();
        for(SqlNode node: conditionNode.getOperandList()){
            operands.add(parseOperand(node, tableDefinition));
        }
        
        return new ObjectCondition(operands, operator);
    }
    
    private static Object parseOperand(SqlNode node, TableDefinition tableDefinition){
        if(node instanceof SqlCall){
            return parseCondition((SqlCall)node, tableDefinition);
        }else if( node instanceof SqlCharStringLiteral ) {
            return ((SqlCharStringLiteral)node).getStringValue();
        } else if( node instanceof SqlNumericLiteral ) {
            return ((SqlNumericLiteral)node).bigDecimalValue();
        } else if( node instanceof SqlAbstractDateTimeLiteral ) {
            return ((SqlAbstractDateTimeLiteral) node).toFormattedString();
        } else {
            String columnName = node.toString();
            return tableDefinition.getColumn(columnName);
        }
    }
    
    public static List<ParameterIndex> getParameter(final ObjectTableModify tableModify,
            final JavaTypeFactory typeFactory) {
        final SqlNode fullSqlStatement = getStatement(tableModify, typeFactory);
        if( fullSqlStatement.getKind() == SqlKind.INSERT ) {
            SqlInsert insert = (SqlInsert) fullSqlStatement;
            List<ParameterIndex> parameterIndices = doBuildParameterIndex(tableModify, insert);
            return parameterIndices;
        } else if(fullSqlStatement.getKind() == SqlKind.UPDATE){
            SqlUpdate update = (SqlUpdate) fullSqlStatement;
            SqlNode source = update.getCondition();
            ParameterIndex parameterIndex = new ParameterIndex();
            int size = update.getSourceExpressionList().size();
            for(int i=0; i< size; i++){
                SqlNode valueNode = update.getSourceExpressionList().get(i);
                SqlNode columnNode = update.getTargetColumnList().get(i);
                parameterIndex.addLast(columnNode.toString(), getSqlNodeValue(valueNode));
            }

//            List<ParameterIndex> parameterIndices =  doBuildParameterIndex0(tableModify, update, update., source);
            return null;
        }
        
        return null;

    }

    private static List<ParameterIndex> doBuildParameterIndex(final ObjectTableModify tableModify,
            final SqlInsert insert) {
        final List<SqlNode> columnList = insert.getTargetColumnList().getList();
        final SqlNode source = insert.getSource();

        return doBuildParameterIndex0(tableModify, insert, columnList, source);
    }

    private static List<ParameterIndex> doBuildParameterIndex0(final ObjectTableModify tableModify,
            final SqlNode sql, final List<SqlNode> columnList, final SqlNode source) {
        if( source instanceof SqlSelect ) {
            // prepared statement or insert select
            return processSubSelect(tableModify, sql, columnList, (SqlSelect) source);
        } else if( source instanceof SqlCall ) {
            return processSqlCallTree(tableModify, (SqlCall) source, columnList, sql);
        }
        throw new RuntimeException("statement is not supported: " + sql.toString());
    }

    private static List<ParameterIndex> processSqlCallTree(final ObjectTableModify tableModify, final SqlCall sqlCall,
            final List<SqlNode> columnList, final SqlNode sql) {
        final List<ParameterIndex> parameterIndexes = new ArrayList<>();
        for ( int i = 0; i < sqlCall.operandCount(); i++ ) {
            final SqlNode operand = sqlCall.operand(i);
            switch (operand.getKind()) {
            case SELECT:
                parameterIndexes.addAll(processSubSelect(tableModify, sql, columnList, (SqlSelect) operand));
                break;
            default:
                final ParameterIndex parameterIndex = new ParameterIndex();
                processSqlCallTree0(tableModify, operand, parameterIndex);
                parameterIndexes.add(parameterIndex);
            }
        }
        return parameterIndexes;
    }

    private static void processSqlCallTree0(final ObjectTableModify tableModify, final SqlNode sqlNode,
            final ParameterIndex parameterIndex) {

        if( sqlNode instanceof SqlCall ) {
            final SqlCall sqlCall = (SqlCall) sqlNode;
            for ( int i = 0; i < sqlCall.operandCount(); i++ ) {
                final SqlNode operand = sqlCall.operand(i);
                if( operand instanceof SqlCall ) {
                    processSqlCallTree0(tableModify, operand, parameterIndex);
                } else if( operand instanceof SqlLiteral ) {
                    final String columnName = tableModify.getTableDefinition().getColumn(i).getName();
                    parameterIndex.addLast(columnName, getValue((SqlLiteral) operand), null);
                }
            }
        }
    }

    private static List<ParameterIndex> processSubSelect(final ObjectTableModify tableModify, final SqlNode sql,
            final List<SqlNode> columnList, final SqlSelect select) {
        final ParameterIndex parameterIndex = new ParameterIndex(tableModify.getTableDefinition().getColumnCount());

        final SqlNode from = select.getFrom();
        if( from instanceof SqlScanNode ) {
            final Bindables.BindableTableScan tableScan = ((SqlScanNode) from).getTableScan();
            parameterIndex.setSubSelect(tableScan);
        }

        final List<SqlNode> selectList = select.getSelectList().getList();
        for ( int i = 0; i < selectList.size(); i++ ) {
            final SqlNode sqlNode = selectList.get(i);
            final Pair<Object, Integer> columnValue = getSqlNodeValue(sqlNode);
            final String fieldName;
            if( columnList.isEmpty() ) {
                // use tableDef
                fieldName = tableModify.getTableDefinition().getColumn(i).getName();
            } else {
                checkParamIndex(columnList.size(), i, sql, MysqlSqlDialect.DEFAULT);
                final SqlIdentifier column = (SqlIdentifier) columnList.get(i);
                final String name = column.names.get(0);
                final int idx = name.indexOf('$');
                if( idx >= 0 ) {
                    final int columnIndex = Integer.parseInt(name.substring(idx + 1));
                    fieldName = tableModify.getTableDefinition().getColumn(columnIndex).getName();
                } else {
                    fieldName = name;
                }
            }

            parameterIndex.addLast(fieldName, columnValue);
        }
        return Collections.singletonList(parameterIndex);
    }

    private static void checkParamIndex(final int size, final int index, final SqlNode sqlNode,
            final SqlDialect dialect) {
        if( size <= index ) {
            throw new IllegalStateException("sql error, value count is great than column count, please check the sql: "
                    + sqlNode.toSqlString(dialect));
        }
    }

    /** @return value -> index */
    private static Pair<Object, Integer> getSqlNodeValue(SqlNode sqlNode) {
        switch (sqlNode.getKind()) {
        case DYNAMIC_PARAM:
            final SqlDynamicParam dynamicParam = (SqlDynamicParam) sqlNode;
            return ImmutablePair.of(ParameterIndex.dynamicValue(), dynamicParam.getIndex());
        case LITERAL:
            final SqlLiteral sqlLiteral = (SqlLiteral) sqlNode;
            return ImmutablePair.of(getValue(sqlLiteral), null);
        case AS:
            // 有嵌套
            final SqlCall sqlCall = (SqlCall) sqlNode;
            final SqlNode operand = sqlCall.operand(0);
            return getSqlNodeValue(operand);
        default:
            throw new RuntimeException(
                    "unsupported sql statement, only ? and value are supported, please check the sql: "
                            + sqlNode.toSqlString(MysqlSqlDialect.DEFAULT));
        }
    }

    private static Object getValue(final SqlLiteral sqlLiteral) {
        final Object columnValue;
        if( sqlLiteral instanceof SqlCharStringLiteral ) {
            columnValue = sqlLiteral.getStringValue();
        } else if( sqlLiteral instanceof SqlNumericLiteral ) {
            columnValue = sqlLiteral.bigDecimalValue();
        } else if( sqlLiteral instanceof SqlAbstractDateTimeLiteral ) {
            columnValue = ((SqlAbstractDateTimeLiteral) sqlLiteral).toFormattedString();
        } else {
            columnValue = sqlLiteral.getValue().toString();
        }
        return columnValue;
    }

}
