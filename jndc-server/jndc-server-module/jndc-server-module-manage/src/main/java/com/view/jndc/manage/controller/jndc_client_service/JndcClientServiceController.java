package com.view.jndc.manage.controller.jndc_client_service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_client_service.JndcClientServiceStructMapper;
import com.view.jndc.manage.model.jndc_client_service.dto.JndcClientServiceDTO;
import com.view.jndc.manage.model.jndc_client_service.vo.JndcClientServiceVO;
import com.view.jndc.manage.serviceI.jndc_client_service.JndcClientServiceServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jndcClientService")
@RequiredArgsConstructor
public class JndcClientServiceController {

    private final JndcClientServiceServiceI jndcClientServiceService;

    /**
     * 创建JndcClientService
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(
            @RequestBody(required = false) JndcClientServiceDTO jndcClientServiceDTO) {
        jndcClientServiceService.save(jndcClientServiceDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcClientService
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(
            @RequestBody(required = false) JndcClientServiceDTO jndcClientServiceDTO) {
        jndcClientServiceService.updateById(jndcClientServiceDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcClientService
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(
            @RequestBody(required = false) JndcClientServiceDTO jndcClientServiceDTO) {
        jndcClientServiceService.removeById(jndcClientServiceDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcClientService
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(
            @RequestBody(required = false) JndcClientServiceDTO jndcClientServiceDTO) {
        IPage page = jndcClientServiceService.queryPage(jndcClientServiceDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcClientService
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(
            @RequestBody(required = false) JndcClientServiceDTO jndcClientServiceDTO) {
        List<JndcClientServiceVO> list = jndcClientServiceService.queryList(jndcClientServiceDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcClientService
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(
            @RequestBody(required = false) JndcClientServiceDTO jndcClientServiceDTO) {
        JndcClientServiceDTO rs = jndcClientServiceService.getById(jndcClientServiceDTO.getId());
        JndcClientServiceVO vo = JndcClientServiceStructMapper.INSTANCE.toVO(rs);
        return EncryptedResponse.success(vo);
    }
}
