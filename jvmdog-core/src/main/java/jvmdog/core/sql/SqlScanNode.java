package jvmdog.core.sql;

import java.util.List;

import org.apache.calcite.interpreter.Bindables;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.parser.SqlParserPos;

public final class SqlScanNode extends SqlIdentifier {

    private final Bindables.BindableTableScan tableScan;

    public SqlScanNode(final Bindables.BindableTableScan tableScan, final List<String> names, final SqlParserPos pos) {
        super(names, pos);
        this.tableScan = tableScan;
    }

    public Bindables.BindableTableScan getTableScan() {
        return tableScan;
    }
}