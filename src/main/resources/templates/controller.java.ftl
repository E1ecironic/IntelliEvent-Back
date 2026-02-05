package ${package.Controller};

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};
import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.common.domin.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;

<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

/**
 * <p>
 * ${table.comment!} 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Slf4j
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
@Tag(name = "${table.comment!}管理")
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    @Autowired
    private ${table.serviceName} ${table.serviceName?uncap_first};

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询${table.comment!}")
    public Result<${entity}> getById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        ${entity} entity = ${table.serviceName?uncap_first}.getById(id);
        return Result.success(entity);
    }

    @PostMapping
    @Operation(summary = "新增${table.comment!}")
    public Result<Boolean> save(@RequestBody ${entity} entity) {
        boolean result = ${table.serviceName?uncap_first}.save(entity);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改${table.comment!}")
    public Result<Boolean> update(@RequestBody ${entity} entity) {
        boolean result = ${table.serviceName?uncap_first}.updateById(entity);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除${table.comment!}")
    public Result<Boolean> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        boolean result = ${table.serviceName?uncap_first}.removeById(id);
        return Result.success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询${table.comment!}")
    public Result page(@RequestBody ${entity} entity) {
        IPage<${entity}> page = ${table.serviceName?uncap_first}.pagelist(entity);
        return Result.success(PageResult.returnResult(page.getTotal(), page.getRecords()));
    }
}