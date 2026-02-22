# MapStruct 转换器使用指南

## 1. 添加依赖

项目已集成 MapStruct 1.5.5.Final，依赖在 pom.xml 中配置：

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

## 2. 创建转换器

### 2.1 创建 Converter 接口

```java
package com.yihen.converter;

import com.yihen.entity.ModelDefinition;
import com.yihen.controller.vo.ModelDefinitionRequestVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ModelDefinitionConverter extends BaseConverter<ModelDefinition, ModelDefinition> {

    ModelDefinitionConverter INSTANCE = Mappers.getMapper(ModelDefinitionConverter.class);

    // 转换为Entity（忽略时间字段）
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    ModelDefinition toEntity(ModelDefinitionRequestVO source);

    // 转换为VO
    @Mapping(target = "id", ignore = true)
    ModelDefinitionRequestVO toVO(ModelDefinition source);
}
```

### 2.2 创建 RequestVO

```java
@Data
@Schema(description = "模型定义请求")
public class ModelDefinitionRequestVO {
    private Long id;
    private String providerName;
    private String providerCode;
    private String baseUrl;
    private Byte status;
}
```

## 3. 在Service中使用

### 3.1 使用MapStruct转换器

```java
@Service
public class ModelDefinitionService {

    @Autowired
    private ModelDefinitionConverter converter;

    @Autowired
    private ModelDefinitionMapper mapper;

    public void create(ModelDefinitionRequestVO requestVO) {
        // VO -> Entity
        ModelDefinition entity = converter.toEntity(requestVO);
        mapper.insert(entity);
    }

    public ModelDefinitionRequestVO getById(Long id) {
        // Entity -> VO
        ModelDefinition entity = mapper.selectById(id);
        return converter.toVO(entity);
    }

    public List<ModelDefinitionRequestVO> list() {
        List<ModelDefinition> entities = mapper.selectList(null);
        // 批量转换
        return converter.toVOList(entities);
    }
}
```

### 3.2 使用通用转换工具类

```java
// 单个转换
ModelDefinition entity = ConvertUtils.convertToEntity(requestVO, ModelDefinition.class);

// 批量转换
List<ModelDefinition> entities = ConvertUtils.convertToEntityList(voList, ModelDefinition.class);

// 深度复制
ModelDefinition target = new ModelDefinition();
ConvertUtils.copyProperties(source, target);
```

## 4. 转换器目录结构

```
com.yihen.converter/
├── BaseConverter.java          # 基础转换器接口
├── ConvertUtils.java           # 通用转换工具类
├── ConverterRegistry.java      # 转换器注册表
├── ConverterAutoConfig.java    # 自动配置
├── ModelDefinitionConverter.java
└── ModelInstanceConverter.java
```

## 5. 最佳实践

1. **推荐使用 MapStruct**：对于复杂的转换逻辑，使用 MapStruct 自动生成实现代码
2. **使用 @Mapping 注解**：显式指定字段映射关系
3. **忽略自动填充字段**：如 id、createTime、updateTime 等
4. **使用 @Autowired 注入**：Spring 会自动注入转换器实现
5. **批量转换使用 Stream**：使用 toEntityList() / toVOList() 方法

## 6. 常见问题

### Q: 编译后没有生成实现类？
A: 检查 pom.xml 中是否添加了 mapstruct-processor 依赖，并确保 IDE 开启了注解处理器。

### Q: 如何处理字段名不一致？
A: 使用 @Mapping 注解：
```java
@Mapping(source = "providerName", target = "name")
```

### Q: 如何处理枚举转换？
A: 在 @Mapper 中使用 @ValueMapping：
```java
@ValueMapping(source = "TEXT", target = "TEXT_MODEL")
```

