package com.view.jndc.manage.controller.jndc_server;

import com.view.jndc.manage.model.jndc_server.JndcServerStructMapper;

import java.util.List;

import com.view.jndc.manage.serviceI.jndc_server.JndcServerServiceI;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_server.vo.JndcServerVO;
import com.view.jndc.manage.model.jndc_server.d_o.JndcServerDO;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jndcServer")
@RequiredArgsConstructor
public class JndcServerController {

    private final JndcServerServiceI jndcServerService;

    /**
     * 创建JndcServer
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(@RequestBody(required = false) JndcServerDTO jndcServerDTO) {
        jndcServerService.save(jndcServerDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcServer
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(@RequestBody(required = false) JndcServerDTO jndcServerDTO) {
        jndcServerService.updateById(jndcServerDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcServer
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(@RequestBody(required = false) JndcServerDTO jndcServerDTO) {
        jndcServerService.removeById(jndcServerDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcServer
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(@RequestBody(required = false) JndcServerDTO jndcServerDTO) {
        IPage page = jndcServerService.queryPage(jndcServerDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcServer
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(@RequestBody(required = false) JndcServerDTO jndcServerDTO) {
        List<JndcServerVO> list = jndcServerService.queryList(jndcServerDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcServer
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(@RequestBody(required = false) JndcServerDTO jndcServerDTO) {
        JndcServerDTO rs = jndcServerService.getById(jndcServerDTO.getId());
        JndcServerVO vo = JndcServerStructMapper.INSTANCE.toVO(rs);
        return EncryptedResponse.success(vo);
    }
}
