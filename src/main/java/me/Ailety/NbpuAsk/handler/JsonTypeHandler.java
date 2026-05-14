package me.Ailety.NbpuAsk.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@MappedTypes(Map.class)
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Class<T> type;

    public JsonTypeHandler(Class<T> type) {
        this.type = type;
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE); // 全局配置驼峰映射
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        }
        catch (IOException e) {
            System.out.println("Naming Strategy: " + objectMapper.getPropertyNamingStrategy());
            throw new SQLException("Failed to convert JSON to String", e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        if (json != null) {
            try {
                return objectMapper.readValue(json, type);
            }
            catch (IOException e) {
                System.out.println("Naming Strategy: " + objectMapper.getPropertyNamingStrategy());
                throw new SQLException("Failed to convert String to JSON", e);
            }
        }
        return null;
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        if (json != null) {
            try {
                return objectMapper.readValue(json, type);
            }
            catch (IOException e) {
                throw new SQLException("Failed to convert String to JSON", e);
            }
        }
        return null;
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        if (json != null) {
            try {
                return objectMapper.readValue(json, type);
            }
            catch (IOException e) {
                throw new SQLException("Failed to convert String to JSON", e);
            }
        }
        return null;
    }

}
