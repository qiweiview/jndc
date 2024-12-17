package com.view.jndc.manage.controller.jndc_server_app_bind;

import com.view.jndc.manage.model.jndc_server_app_bind.JndcServerAppBindStructMapper;

import java.util.List;

import com.view.jndc.manage.serviceI.jndc_server_app_bind.JndcServerAppBindServiceI;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_server_app_bind.vo.JndcServerAppBindVO;
import com.view.jndc.manage.model.jndc_server_app_bind.d_o.JndcServerAppBindDO;
import com.view.jndc.manage.model.jndc_server_app_bind.dto.JndcServerAppBindDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jndcServerAppBind")
@RequiredArgsConstructor
public class JndcServerAppBindController {

    private final JndcServerAppBindServiceI jndcServerAppBindService;

    /**
     * 创建JndcServerAppBind
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(
            @RequestBody(required = false) JndcServerAppBindDTO jndcServerAppBindDTO) {
        jndcServerAppBindService.save(jndcServerAppBindDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcServerAppBind
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(
            @RequestBody(required = false) JndcServerAppBindDTO jndcServerAppBindDTO) {
        jndcServerAppBindService.updateById(jndcServerAppBindDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcServerAppBind
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(
            @RequestBody(required = false) JndcServerAppBindDTO jndcServerAppBindDTO) {
        jndcServerAppBindService.removeById(jndcServerAppBindDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcServerAppBind
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(
            @RequestBody(required = false) JndcServerAppBindDTO jndcServerAppBindDTO) {
        IPage page = jndcServerAppBindService.queryPage(jndcServerAppBindDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcServerAppBind
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(
            @RequestBody(required = false) JndcServerAppBindDTO jndcServerAppBindDTO) {
        List<JndcServerAppBindVO> list = jndcServerAppBindService.queryList(jndcServerAppBindDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcServerAppBind
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(
            @RequestBody(required = false) JndcServerAppBindDTO jndcServerAppBindDTO) {
        JndcServerAppBindDTO rs = jndcServerAppBindService.getById(jndcServerAppBindDTO.getId());
        JndcServerAppBindVO vo = JndcServerAppBindStructMapper.INSTANCE.toVO(rs);
        return EncryptedResponse.success(vo);
    }
}
