package com.view.jndc.manage.controller.jndc_server_app;

import com.view.jndc.manage.model.jndc_server_app.JndcServerAppStructMapper;

import java.util.List;

import com.view.jndc.manage.serviceI.jndc_server_app.JndcServerAppServiceI;
// import com.view.free.common.base.web.EncryptedResponse;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_server_app.vo.JndcServerAppVO;
import com.view.jndc.manage.model.jndc_server_app.d_o.JndcServerAppDO;
import com.view.jndc.manage.model.jndc_server_app.dto.JndcServerAppDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jndcServerApp")
@RequiredArgsConstructor
public class JndcServerAppController {

    private final JndcServerAppServiceI jndcServerAppService;

    /**
     * 创建JndcServerApp
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(@RequestBody(required = false) JndcServerAppDTO jndcServerAppDTO) {
        jndcServerAppService.save(jndcServerAppDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcServerApp
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(
            @RequestBody(required = false) JndcServerAppDTO jndcServerAppDTO) {
        jndcServerAppService.updateById(jndcServerAppDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcServerApp
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(
            @RequestBody(required = false) JndcServerAppDTO jndcServerAppDTO) {
        jndcServerAppService.removeById(jndcServerAppDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcServerApp
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(
            @RequestBody(required = false) JndcServerAppDTO jndcServerAppDTO) {
        IPage page = jndcServerAppService.queryPage(jndcServerAppDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcServerApp
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(
            @RequestBody(required = false) JndcServerAppDTO jndcServerAppDTO) {
        List<JndcServerAppVO> list = jndcServerAppService.queryList(jndcServerAppDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcServerApp
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(
            @RequestBody(required = false) JndcServerAppDTO jndcServerAppDTO) {
        JndcServerAppDTO rs = jndcServerAppService.getById(jndcServerAppDTO.getId());
        JndcServerAppVO vo = JndcServerAppStructMapper.INSTANCE.toVO(rs);
        return EncryptedResponse.success(vo);
    }
}
