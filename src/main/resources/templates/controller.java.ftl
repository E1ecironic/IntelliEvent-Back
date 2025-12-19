package ${package.Controller};

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};
import com.kevin.basecore.common.domin.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
@Api(tags = "${table.comment!}管理")
<#if superControllerClass??>
    public class ${table.controllerName} extends ${superControllerClass} {
<#else>
    public class ${table.controllerName} {
</#if>

@Autowired
private ${table.serviceName} ${table.serviceName?uncap_first};

@GetMapping("/{id}")
@ApiOperation(value = "根据ID查询${table.comment!}")
public Result<${entity}> getById(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
${entity} entity = ${table.serviceName?uncap_first}.getById(id);
return Result.success(entity);
}

@PostMapping
@ApiOperation(value = "新增${table.comment!}")
public Result
<Boolean> save(@RequestBody ${entity} entity) {
    boolean result = ${table.serviceName?uncap_first}.save(entity);
    return Result.success(result);
    }

    @PutMapping
    @ApiOperation(value = "修改${table.comment!}")
    public Result
    <Boolean> update(@RequestBody ${entity} entity) {
        boolean result = ${table.serviceName?uncap_first}.updateById(entity);
        return Result.success(result);
        }

        @DeleteMapping("/{id}")
        @ApiOperation(value = "删除${table.comment!}")
        public Result
        <Boolean> delete(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
            boolean result = ${table.serviceName?uncap_first}.removeById(id);
            return Result.success(result);
            }

            @GetMapping("/page")
            @ApiOperation(value = "分页查询${table.comment!}")
            public Result
            <IPage
            <${entity}>> page(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
            IPage<${entity}> page = new Page<>(pageNum, pageSize);
            ${table.serviceName?uncap_first}.page(page);
            return Result.success(page);
            }
            }