package com.view.jndc.manage.controller.jndc_client;

import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_client.JndcClientStructMapper;

import java.util.List;

import com.view.jndc.manage.serviceI.jndc_client.JndcClientServiceI;

import com.view.jndc.manage.model.jndc_client.vo.JndcClientVO;
import com.view.jndc.manage.model.jndc_client.d_o.JndcClientDO;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jndcClient")
@RequiredArgsConstructor
public class JndcClientController {

    private final JndcClientServiceI jndcClientService;

    /**
     * 创建JndcClient
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(@RequestBody(required = false) JndcClientDTO jndcClientDTO) {
        jndcClientService.save(jndcClientDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcClient
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(@RequestBody(required = false) JndcClientDTO jndcClientDTO) {
        jndcClientService.updateById(jndcClientDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcClient
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(@RequestBody(required = false) JndcClientDTO jndcClientDTO) {
        jndcClientService.removeById(jndcClientDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcClient
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(@RequestBody(required = false) JndcClientDTO jndcClientDTO) {
        IPage page = jndcClientService.queryPage(jndcClientDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcClient
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(@RequestBody(required = false) JndcClientDTO jndcClientDTO) {
        List<JndcClientVO> list = jndcClientService.queryList(jndcClientDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcClient
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(@RequestBody(required = false) JndcClientDTO jndcClientDTO) {
        JndcClientDTO rs = jndcClientService.getById(jndcClientDTO.getId());
        JndcClientVO vo = JndcClientStructMapper.INSTANCE.toVO(rs);
        return EncryptedResponse.success(vo);
    }
}
