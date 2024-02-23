package org.dromara.system.controller.system;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.system.domain.SysArea;
import org.dromara.system.domain.vo.SysAreaVo;
import org.dromara.system.service.ISysAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Tag(name = "SysAreaController", description = "行政区划信息")
@RestController
@RequestMapping("/system/area")
public class SysAreaController {

    @Autowired
    private ISysAreaService sysAreaService;

    @Operation(summary = "行政区列表")
    @GetMapping("/list")
    public R list(SysArea sysArea) {

        try {
            List<Tree<Integer>> areaList = RedisUtils.getCacheList("area");//redisService.getCacheList("area");

            if (CollectionUtil.isNotEmpty(areaList)) {
                return R.ok(areaList);
            }

            List<SysArea> list = sysAreaService.getList(sysArea);
            if (CollectionUtil.isEmpty(list)) {
                throw new BaseException("暂无数据");
            }


            List<TreeNode<Integer>> treeNodes = list.stream().map(area -> {
                return new TreeNode<>(area.getId(), area.getParentId(), area.getExtName(), area.getExtId());
            }).collect(Collectors.toList());
            List<Tree<Integer>> treeList = TreeUtil.build(treeNodes, 0);
            if (CollectionUtil.isNotEmpty(treeList)) {
                RedisUtils.setCacheList("area", treeList);
            }

            return R.ok(treeList);
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("行政区列表获取失败", ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "获取下一级行政区列表")
    @GetMapping("/getChildListById")
    public R getChildListById(@NotNull(message = "ID不允许为空") Integer id) {
        try {
            List<SysAreaVo> list = sysAreaService.getChildListById(id);
            return R.ok(list);
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("获取下一级行政区列表失败", ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "获取行政区信息")
    @GetMapping("/getAreaInfo/{id}")
    public R getAreaInfo(@PathVariable("id") Integer id) {
        try {
            SysArea areaInfo = sysAreaService.selectAreaById(id);
            return R.ok(areaInfo);
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("获取行政区信息", ex);
            return R.fail("内部服务错误");
        }
    }

}
