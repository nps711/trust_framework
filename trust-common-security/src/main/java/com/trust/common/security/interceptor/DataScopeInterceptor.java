package com.trust.common.security.interceptor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.trust.common.core.context.UserContext;
import com.trust.common.core.context.UserContextHolder;
import com.trust.common.security.annotation.DataScope;
import com.trust.common.security.client.AuthServiceClient;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataScopeInterceptor extends JsqlParserSupport implements InnerInterceptor {

    private final AuthServiceClient authServiceClient;

    public DataScopeInterceptor(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                            ResultHandler resultHandler, BoundSql boundSql) {
        DataScope dataScope = findDataScope(ms);
        if (dataScope == null) {
            return;
        }
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(parserSingle(mpBs.sql(), dataScope));
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        DataScope dataScope = (DataScope) obj;
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Expression where = plainSelect.getWhere();

        List<Long> deptIds = getCurrentUserDeptIds();
        if (deptIds == null || deptIds.isEmpty()) {
            return;
        }

        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(new Column(dataScope.deptAlias().isEmpty() ? "dept_id" : dataScope.deptAlias()));
        ExpressionList<Expression> expressionList = new ExpressionList<>();
        expressionList.setExpressions(deptIds.stream().map(LongValue::new).collect(Collectors.toList()));
        inExpression.setRightExpression(expressionList);

        if (where == null) {
            plainSelect.setWhere(inExpression);
        } else {
            plainSelect.setWhere(new AndExpression(where, inExpression));
        }
    }

    private DataScope findDataScope(MappedStatement ms) {
        try {
            String id = ms.getId();
            String className = id.substring(0, id.lastIndexOf('.'));
            String methodName = id.substring(id.lastIndexOf('.') + 1);
            Class<?> clazz = Class.forName(className);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(DataScope.class)) {
                    return method.getAnnotation(DataScope.class);
                }
            }
            if (clazz.isAnnotationPresent(DataScope.class)) {
                return clazz.getAnnotation(DataScope.class);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private List<Long> getCurrentUserDeptIds() {
        UserContext context = UserContextHolder.getContext();
        if (context == null || context.getUserId() == null) {
            return null;
        }
        try {
            var res = authServiceClient.getDataScopeDepts(new AuthServiceClient.UserIdReq(context.getUserId()));
            if (res != null && res.getData() != null) {
                return res.getData();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
