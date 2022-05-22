package jvmdog.core.sql;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.calcite.interpreter.Bindables;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

public class ParameterIndex {
    /** 在执行底层数据源更新数据的过程中，需要使用到当前更新语句对应的{@link ParameterIndex}对象 */
    public static final String CONTEXT_PARAMETER_KEY = "__PARAMETER_INDEX__";

    private static final DynamicValue DYNAMIC_VALUE = DynamicValue.DYNAMIC_VALUE;
    private static final int DEFAULT_INDEX_SIZE = 8;

    /**
     * key: columnName， 也就是{@link ColumnDef#getName()}
     *
     * <p>value: 参数值，如果SQL中的参数是？,则value是{@link #DYNAMIC_VALUE}
     */
    private LinkedHashMap<String, Object> index;

    /**
     * 表示第几个参数， key: columnName, value： 参数的位置
     *
     * @see #index
     */
    private Map<String, Integer> positions;

    /** 子查询，没有则为null */
    private Bindables.BindableTableScan subSelect;

    public ParameterIndex() {
      this(DEFAULT_INDEX_SIZE);
    }

    public ParameterIndex(final int indexSize) {
      this.index = Maps.newLinkedHashMapWithExpectedSize(indexSize);
      this.positions = Maps.newHashMapWithExpectedSize(indexSize);
    }

    public static DynamicValue dynamicValue() {
      return DYNAMIC_VALUE;
    }

    public int position(final String parameterName) {
      final Integer pos = positions.get(parameterName);
      if (pos == null) {
        throw new IllegalArgumentException("parameter[" + parameterName + "] not exists");
      }
      return pos;
    }

    public ParameterIndex addLast(final String parameterName, final Pair<Object, Integer> value) {
      index.put(parameterName, value.getLeft());
      positions.put(parameterName, value.getRight());
      return this;
    }
    public ParameterIndex addLast(final String parameterName, final Object value, final Integer position) {
      index.put(parameterName, value);
      positions.put(parameterName, position);
      return this;
    }

    public Map<String, Object> parameterValues() {
      return index;
    }

    public Map<String, Object> getIndex() {
      return index;
    }

    public void setIndex(final LinkedHashMap<String, Object> index) {
      this.index = index;
    }

    public Map<String, Integer> getPositions() {
      return positions;
    }

    public void setPositions(final Map<String, Integer> positions) {
      this.positions = positions;
    }

    public Bindables.BindableTableScan getSubSelect() {
      return subSelect;
    }

    public void setSubSelect(final Bindables.BindableTableScan subSelect) {
      this.subSelect = subSelect;
    }

    public enum DynamicValue {
      DYNAMIC_VALUE;
    }
  }
