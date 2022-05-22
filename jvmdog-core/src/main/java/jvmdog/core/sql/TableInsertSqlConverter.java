package jvmdog.core.sql;

import java.util.List;

import org.apache.calcite.adapter.enumerable.EnumerableInterpreter;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.interpreter.Bindables;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.rel2sql.SqlImplementor.Clause;
import org.apache.calcite.rel.rel2sql.SqlImplementor.Result;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.util.Util;

import com.google.common.collect.ImmutableList;

public class TableInsertSqlConverter extends RelToSqlConverter {
    public TableInsertSqlConverter(SqlDialect dialect, JavaTypeFactory typeFactory) {
      super(dialect);
      Util.discard(typeFactory);
    }

    /** @see #dispatch */
    public Result visit(EnumerableInterpreter interpreter) {
      final RelNode input = interpreter.getInput();
      if (input instanceof Bindables.BindableTableScan) {
        Bindables.BindableTableScan tableScan = (Bindables.BindableTableScan) input;
        final ImmutableList<RexNode> filters = tableScan.filters;
        final List<String> qualifiedName = tableScan.getTable().getQualifiedName();
        // nested query
        return result(
            new SqlScanNode(tableScan, qualifiedName, SqlParserPos.ZERO),
            ImmutableList.of(Clause.FROM),
            input,
            null);
      }
      if (input instanceof TableScan) {
        return visit((TableScan) input);
      }
      return visit(input);
    }

    @Override
    public Result visit(final TableScan e) {
      return super.visit(e);
    }
  }

