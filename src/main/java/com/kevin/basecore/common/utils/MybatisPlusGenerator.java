package com.kevin.basecore.common.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.IGenerateMapperMethodHandler;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.INameConvert;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;
import com.baomidou.mybatisplus.generator.model.MapperMethod;
import com.kevin.basecore.common.domin.BaseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.type.JdbcType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class MybatisPlusGenerator {

/*    @Value("${spring.datasource.url}")
    private static String dbUrl;
    @Value("${spring.datasource.username}")
    private static String dbUser;
    @Value("${spring.datasource.password}")
    private static String dbPassword;*/

    private static final String dbUrl = "jdbc:mysql://localhost:3306/intellievent";
    private static final String dbUser = "root";
    private static final String dbPassword = "root";

    /**
     * 全部配置清单版
     */
    public static void generator1() {
        /*
         * FastAutoGenerator
         * 0、create  创建构造器，配置数据源
         * 1、DataSourceConfig.Builder  数据库配置
         * 2、GlobalConfig.Builder  全局配置
         * 3、PackageConfig.Builder  包配置
         * 4、StrategyConfig.Builder  策略配置
         *   4.1、全局策略配置
         *   4.2、Entity策略配置
         *   4.3、Controller策略配置
         *   4.4、Service策略配置
         *   4.5、Mapper策略配置
         * 5、InjectionConfig.Builder  注入配置
         * 6、templateEngine  选择模板引擎
         * 7、执行
         * */
        // 0、使用FastAutoGenerator构造器，同时进行数据源配置
        FastAutoGenerator.create(new DataSourceConfig.Builder("url","user","root"))
                // 1、数据库配置，一般没特殊需求不用配置
                .dataSourceConfig(builder -> {
                    builder.schema("mybatis-plus") // 设置数据库schema（默认值）
                            .keyWordsHandler(new MySqlKeyWordsHandler()) // 设置数据库关键字处理器（默认值）
                            // 当 MySQL 下 tinyint 字段长度大于 1 时，默认转换成 Byte，如果想继续转换成 Integer，可使用如下代码
                            .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                                // 兼容旧版本转换成Integer
                                if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                                    return DbColumnType.INTEGER;
                                }
                                return typeRegistry.getColumnType(metaInfo);
                            });
                })
                // 2、全局配置
                .globalConfig(builder -> {
                    builder.outputDir(System.getProperty("os.name").toLowerCase().contains("windows") ? "D://" : "/tmp") // 设置文件的输出目录【 windows:D://  linux or mac:/tmp 】
                            .disableOpenDir() // 禁止打开输出目录（默认 true）
                            .author("baomidou") // 设置作者（默认值）
                            .enableKotlin() // 开启 kotlin 模式（默认 false）
                            .enableSwagger() // 开启 swagger 模式（默认 false），与 springdoc 不可同时使用
                            .enableSpringdoc() // 开启 springdoc 模式（默认 false），与 swagger 不可同时使用
                            .disableServiceInterface() // 禁止生成service 接口（默认 true）；增加此开关的原因：在某些项目实践中，只需要生成service实现类，不需要抽象sevice接口；针对某些项目，生成service接口，开发时反而麻烦，这种情况，可以将该属性设置为false
                            .dateType(DateType.TIME_PACK) // 设置时间类型对应策略（默认值），数据库时间类型 到 实体类时间类型 对应策略
                            .commentDate(() -> new SimpleDateFormat("yyyy-MM-dd").format(new Date())) // 设置注释日期（默认值）,example: () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
                            .commentDate("yyyy-MM-dd"); // 设置注释日期，直接使用格式也可以
                })
                // 3、包配置
                .packageConfig(builder -> {
                    builder.parent("com.baomidou") // 设置父包名（默认值）
                            .moduleName("") // 设置模块名（默认值），就是父包名的子模块名称，适用于单体多模块项目
                            .entity("entity") // 设置 Entity 包名（默认值）
                            .service("service") // 设置 Service 包名（默认值）
                            .serviceImpl("service.impl") // 置 Service Impl 包名（默认值）设
                            .mapper("mapper") // 设置 Mapper 包名（默认值）
                            .xml("mapper.xml") // 设置 Mapper XML 包名（默认值）
                            .controller("controller") // 设置 Controller 包名（默认值）
                            .pathInfo(new HashMap<>()) // 用一个map直接设置各个包的路径，主要用来修改mapper.xml路径，否则默认在java/mapper的目录下，例：Collections.singletonMap(OutputFile.mapperXml, "/path/to/xml")
                            .joinPackage(""); // 连接父子包名，整体目录结构为 outputDir.parent.moduleName.subPackage，适用于微服务项目
                })
                // 4、策略配置
                .strategyConfig(builder -> {
                    builder
                            // 4.1、全局策略配置
                            .enableCapitalMode() // 开启大写命名（默认 false）
                            .enableSkipView() // 开启跳过视图（默认 false）
                            .disableSqlFilter() // 禁用sql过滤（默认 false），语法不能支持使用sql过滤表的话，可以考虑关闭此开关.
                            .enableSchema() // 启用 schema（默认 false），多 schema 场景时启用
                            .addTablePrefix() // 增加过滤表前缀，支持可变参数和List列表
                            .addTableSuffix() // 增加过滤表后缀，支持可变参数和List列表
                            .addFieldPrefix() // 增加过滤字段前缀，支持可变参数和List列表
                            .addFieldSuffix() // 增加过滤字段后缀，支持可变参数和List列表
                            .addInclude() // 增加表匹配（内存过滤），支持可变参数和List列表，与 addExclude 互斥，只能配置一项，支持正则匹配，如 ^t_.* 匹配所有以 t_ 开头的表名
                            .addExclude() // 增加表排除匹配（内存过滤），支持可变参数和List列表,与 addInclude 互斥，只能配置一项，支持正则匹配，如 .*st$ 匹配所有以 st 结尾的表名
                            .likeTable(new LikeTable("")) // 模糊表匹配（SQL 过滤），与 notLikeTable 互斥，只能配置一项
                            .notLikeTable(new LikeTable("")) // 模糊表排除（SQL 过滤），与 likeTable 互斥，只能配置一项
                            .outputFile((path, ot) -> new File(path)) // 自定义模板输出文件处理，暂时不清楚是干啥的

                            // 4.2、Entity策略配置
                            .entityBuilder()
                            .nameConvert(new INameConvert() {
                                @Override public String entityNameConvert(TableInfo tableInfo) { return tableInfo.getName(); }
                                @Override public String propertyNameConvert(TableField field) { return field.getName(); }}) // 自定义名称转换实现
                            .superClass(BaseEntity.class) // 设置父类，使用公共字段时使用
                            .superClass("com.baomidou.global.BaseEntity") // 设置父类，也可以用 类全称带包名
                            .disableSerialVersionUID() // 禁用生成 serialVersionUID（默认 true）
                            .enableSerialAnnotation() // 启用生成 @Serial 注解（默认 false），需JAVA 14
                            .enableColumnConstant() // 开启生成字段常量（默认 false），public static final String ID = "test_id";
                            .enableChainModel() // 开启链式模型（默认 false）
                            .enableLombok() // 开启lombok模型（默认 false），默认添加Getter、Setter、ToString
                            .enableLombok(new ClassAnnotationAttributes("@Data","lombok.Data")) // 开启lombok模型（默认 false），会把注解属性都加入进去，无论是否启用GlobalConfig的isKotlin()，同时get,set,toString都将不会生成，需自行控制添加
                            .enableRemoveIsPrefix() // 开启Boolean类型字段移除is前缀（默认 false）
                            .enableTableFieldAnnotation() // 开启生成实体时生成字段注解（默认 false）
                            .enableActiveRecord() // 开启 ActiveRecord 模式（默认 false）
                            .versionColumnName("version") // 设置乐观锁字段名（数据库字段），versionColumnName 与 versionPropertyName 二选一即可
                            .versionPropertyName("version") // 设置乐观锁属性名（实体），versionColumnName 与 versionPropertyName 二选一即可
                            .logicDeleteColumnName("deleted") // 设置逻辑删除字段名（数据库字段），logicDeleteColumnName 与 logicDeletePropertyName 二选一即可
                            .logicDeletePropertyName("deleteFlag") // 设置逻辑删除属性名（实体），logicDeleteColumnName 与 logicDeletePropertyName 二选一即可
                            .naming(NamingStrategy.underline_to_camel) // 设置数据库表映射到实体的命名策略（默认值 下划线转驼峰命名）
                            .columnNaming(NamingStrategy.underline_to_camel) // 设置数据库表字段映射到实体的命名策略（默认值：null，未指定按照 naming 执行）
                            .addSuperEntityColumns() // 添加父类公共字段，支持可变参数和List列表
                            .addIgnoreColumns() // 添加忽略字段，支持可变参数和List列表
                            .addTableFills() // 添加表字段填充，支持可变参数和List列表
                            .idType(IdType.AUTO) // 设置全局主键类型（默认值 自增）
                            .convertFileName(entityName -> entityName) // 自定义转换输出文件名称
                            .formatFileName("format") // 设置格式化文件名称
                            .enableFileOverride() // 开启覆盖已有文件（默认 false），针对 Entity
                            .javaTemplate("path") // 指定模板路径
                            .kotlinTemplatePath("path") // 设置自定义模板路径
                            .disable() // 禁用 Entity 生成（默认 true）
                            .addClassAnnotation(new ClassAnnotationAttributes("@Data","lombok.Data")) // 添加类注解，这里主要用于添加其他注解
                            .tableFieldAnnotationHandler((tableInfo, tableField) -> null) // 自定义字段注解处理器
                            .tableAnnotationHandler((tableInfo, entity) -> null) // 自定义表注解处理器
                            .toString(true) // 设置是否生成ToString方法（默认 true）
                            .fieldUseJavaDoc(true) // 设置字段是否生成文档注释（默认 true），当注释字段注释不为空才生效
                            .importPackageFunction(strings -> null) // 自定义导包处理方法
                            .annotationAttributesFunction(annotationAttributes -> null) // 自定义注解处理方法 (含类与字段)

                            // 4.3、Controller策略配置
                            .controllerBuilder()
                            .superClass("") // 设置父类，可以用 类全称带包名 或 父类.class
                            .enableHyphenStyle() // 开启驼峰转连字符（默认 false）
                            .enableRestStyle() // 开启生成 @RestController 控制器（默认 false），采用 restful 风格的api
                            .convertFileName(entityName -> null) // 自定义转换输出文件名称
                            .formatFileName("format") // 格式化文件名称
                            .enableFileOverride() // 开启覆盖已有文件（默认 false），针对 Controller
                            .disable() // 禁用 Controller 生成（默认 true）
                            .template("path") // 设置自定义模板路径

                            // 4.4、Service策略配置
                            .serviceBuilder()
                            .superServiceClass("") // 设置接口父类，可以用 类全称带包名 或 父类.class
                            .superServiceImplClass("") // 设置实现类父类，可以用 类全称带包名 或 父类.class
                            .convertServiceFileName(entityName -> null) // 自定义转换输出service接口文件名称
                            .convertServiceImplFileName(entityName -> null) // 自定义转换输出service实现类文件名称
                            .formatServiceFileName("format") // 自定义格式化service接口文件名称
                            .formatServiceImplFileName("format") // 自定义格式化service实现类文件名称
                            .enableFileOverride() // 开启覆盖已有文件（默认 false），针对 Service
                            .disable() // 禁用 Service 接口和实现类生成（默认 true）
                            .disableService() // 禁用 Service 接口生成（默认 true）
                            .disableServiceImpl() // 禁用 Service 实现类生成（默认 true）
                            .serviceTemplate("path") // 设置自定义service接口模板路径
                            .serviceImplTemplate("path") // 设置自定义service实现类模板路径

                            // 4.5、Mapper策略配置
                            .mapperBuilder()
                            .superClass("") // 设置父类，可以用 类全称带包名 或 父类.class
                            .mapperAnnotation(Mapper.class) // 自定义添加注解，例：@Mapper注解 Mapper.class
                            .enableBaseResultMap() // 开启 BaseResultMap（默认 false）,如果配置了resultMap，mybatis会根据查询到的条目数量自动进行判断，如果是一条就返回对象，如果是多条就返回List对象列表
                            .enableBaseColumnList() // 开启 BaseColumnList
                            .cache(PerpetualCache.class) // 自定义缓存实现类，需实现Cache接口
                            .convertMapperFileName(entityName -> null) // 自定义输出Mapper文件名称转换
                            .convertXmlFileName(entityName -> null) // 自定义转换Xml文件名称处理
                            .formatMapperFileName("format") // 自定义格式化Mapper文件名称
                            .formatXmlFileName("format") // 格式化Xml文件名称
                            .enableFileOverride() // 开启覆盖已有文件（默认 false），针对 Mapper
                            .mapperTemplate("path") // 设置自定义Mapper文件模板路径
                            .mapperXmlTemplate("path") // 设置自定义Xml文件模板路径
                            .disable() // 禁用Mapper接口和Xml文件生成（默认 true）
                            .disableMapper() // 禁用Mapper接口生成（默认 true）
                            .disableMapperXml() // 禁用MapperXml生成（默认 true）
                            .generateMapperMethodHandler(new IGenerateMapperMethodHandler() {
                                @Override public List<MapperMethod> getMethodList(TableInfo tableInfo) { return null; }
                                @Override public Set<String> getImportPackages(TableInfo tableInfo) { return null; }}) // 自定义Mapper层方法生成处理器
                            .importPackageFunction(strings -> null); // 自定义导包处理方法
                })
                // 5、注入配置
                .injectionConfig(builder -> {
                    builder.beforeOutputFile((tableInfo, stringObjectMap) -> {}) // 输出文件之前执行的逻辑，在生成文件之前执行自定义逻辑，如打印表信息或修改配置数据
                            .customMap(new HashMap<>()) // 自定义配置 Map 对象，用于在模板中访问自定义的配置信息，如项目名称、作者等
                            .customFile(new HashMap<>()); // 自定义配置模板文件，用于指定自定义的模板文件路径，可以格式化文件名
                })
                // 6、选择模板引擎
                .templateEngine(new FreemarkerTemplateEngine()) // 设置使用Freemarker引擎模板，默认的是Velocity引擎模板
                // 7、执行
                .execute();

    }


    /**
     * 常用配置清单版
     */
    public static void generator2() {
        // 0、使用FastAutoGenerator构造器，同时进行数据源配置
        FastAutoGenerator.create(new DataSourceConfig.Builder("url","user","root"))
                // 2、全局配置
                .globalConfig(builder -> {
                    builder.outputDir((System.getProperty("user.dir")+"/src/main/java")) // 设置文件的输出目录 根目录/src/main/java
                            .disableOpenDir() // 禁止打开输出目录（默认 true）
                            .author("baomidou") // 设置作者（默认值）
                            .enableSwagger() // 开启 swagger 模式（默认 false），与 springdoc 不可同时使用
                            .commentDate("yyyy-MM-dd"); // 设置注释日期，直接使用格式也可以
                })
                // 3、包配置
                .packageConfig(builder -> {
                    builder.parent("com.baomidou") // 设置父包名（默认值）
                            .moduleName("") // 设置模块名（默认值），就是父包名的子模块名称，适用于单体多模块项目
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir")+"/src/main/resources/mapper")) // 用一个map直接设置各个包的路径，主要用来修改mapper.xml路径，否则默认在java/mapper的目录下，例：Collections.singletonMap(OutputFile.mapperXml, "/path/to/xml")
                            .joinPackage(""); // 连接父子包名，整体目录结构为 outputDir.parent.moduleName.subPackage，适用于微服务项目
                })
                // 4、策略配置
                .strategyConfig(builder -> {
                    builder
                            // 4.1、全局策略配置
                            .enableSkipView() // 开启跳过视图（默认 false）
                            .addInclude() // 增加表匹配（内存过滤），支持可变参数和List列表，与 addExclude 互斥，只能配置一项，支持正则匹配，如 ^t_.* 匹配所有以 t_ 开头的表名

                            // 4.2、Entity策略配置
                            .entityBuilder()
                            .superClass(BaseEntity.class) // 设置父类，使用公共字段时使用
                            .disableSerialVersionUID() // 禁用生成 serialVersionUID（默认 true）
                            .enableChainModel() // 开启链式模型（默认 false）
                            .enableLombok(new ClassAnnotationAttributes("@Data","lombok.Data")) // 开启lombok模型（默认 false），会把注解属性都加入进去，无论是否启用GlobalConfig的isKotlin()，同时get,set,toString都将不会生成，需自行控制添加
                            .enableTableFieldAnnotation() // 开启生成实体时生成字段注解（默认 false）
                            .enableActiveRecord() // 开启 ActiveRecord 模式（默认 false）
                            .versionColumnName("version") // 设置乐观锁字段名（数据库字段），versionColumnName 与 versionPropertyName 二选一即可
                            .logicDeleteColumnName("deleted") // 设置逻辑删除字段名（数据库字段），logicDeleteColumnName 与 logicDeletePropertyName 二选一即可
                            .addSuperEntityColumns() // 添加父类公共字段，支持可变参数和List列表
                            .enableFileOverride() // 开启覆盖已有文件（默认 false），针对 Entity
                            .disable() // 禁用 Entity 生成（默认 true）

                            // 4.3、Controller策略配置
                            .controllerBuilder()
                            .superClass("") // 设置父类，可以用 类全称带包名 或 父类.class
                            .enableRestStyle() // 开启生成 @RestController 控制器（默认 false），采用 restful 风格的api
                            .enableFileOverride() // 开启覆盖已有文件（默认 false），针对 Controller
                            .disable() // 禁用 Controller 生成（默认 true）

                            // 4.4、Service策略配置
                            .serviceBuilder()
                            .superServiceClass("") // 设置接口父类，可以用 类全称带包名 或 父类.class
                            .superServiceImplClass("") // 设置实现类父类，可以用 类全称带包名 或 父类.class
                            .enableFileOverride() // 开启覆盖已有文件（默认 false），针对 Service
                            .disable() // 禁用 Service 接口和实现类生成（默认 true）

                            // 4.5、Mapper策略配置
                            .mapperBuilder()
                            .superClass("") // 设置父类，可以用 类全称带包名 或 父类.class
                            .mapperAnnotation(Mapper.class) // 自定义添加注解，例：@Mapper注解 Mapper.class
                            .enableFileOverride() // 开启覆盖已有文件（默认 false），针对 Mapper
                            .disable(); // 禁用Mapper接口和Xml文件生成（默认 true）
                })
                // 6、选择模板引擎
                .templateEngine(new FreemarkerTemplateEngine()) // 设置使用Freemarker引擎模板，默认的是Velocity引擎模板
                // 7、执行
                .execute();

    }

    /**
     * 实际使用版
     * Spring Boot项目生成全部表示例，有调整可以自己修改
     */
    /**
     * 实际使用版 - 生成包含Swagger注解的Controller
     */
    /**
     * 实际使用版 - 生成包含Swagger2注解的Controller
     */
    public static void generator3(String dbName) {
        FastAutoGenerator.create(dbUrl, dbUser, dbPassword)
                .globalConfig(builder -> {
                    builder.outputDir((System.getProperty("user.dir")+"/src/main/java"))
                            .disableOpenDir()
                            .author("kevin")
                            //.enableSwagger()  // 确保启用Swagger2
                            .enableSpringdoc()
                            .commentDate("yyyy-MM-dd");
                })
                .packageConfig(builder -> {
                    builder.parent("com.kevin.intellieventback")
                            .entity("entity")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("mapper")
                            .xml("mapper.xml")
                            .controller("controller");
                })
                .strategyConfig(builder -> {
                    builder
                            // 全局策略
                            .enableSkipView()
                            .addInclude(dbName)

                            // Entity策略
                            .entityBuilder()
                            .superClass(BaseEntity.class)
                            .enableLombok(new ClassAnnotationAttributes("@Data","lombok.Data"))
                            .enableTableFieldAnnotation()
                            .enableFileOverride()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)

                            // Controller策略 - 添加Swagger注解
                            .controllerBuilder()
                            .enableRestStyle()
                            .enableHyphenStyle()
                            .enableFileOverride()
                            .formatFileName("%sController")
                            .template("templates/controller.java") // 指定自定义模板


                            // Mapper策略
                            .mapperBuilder()
                            .mapperAnnotation(Mapper.class)
                            .enableFileOverride()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper")

                            // Service策略
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .enableFileOverride();
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    public static void main(String[] args) {
        // generator1(); // 全部配置清单版
        // generator2(); // 常用配置清单版
        generator3("users"); // 实际使用版
    }

}
