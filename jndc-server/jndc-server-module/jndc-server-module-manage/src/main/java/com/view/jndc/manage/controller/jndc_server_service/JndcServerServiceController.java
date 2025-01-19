package com.view.jndc.manage.controller.jndc_server_service;

import com.view.jndc.manage.model.jndc_server_service.JndcServerServiceStructMapper;

import java.util.List;

import com.view.jndc.manage.serviceI.jndc_server_service.JndcServerServiceServiceI;
// import com.view.free.common.base.web.EncryptedResponse;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_server_service.vo.JndcServerServiceVO;
import com.view.jndc.manage.model.jndc_server_service.d_o.JndcServerServiceDO;
import com.view.jndc.manage.model.jndc_server_service.dto.JndcServerServiceDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jndcServerService")
@RequiredArgsConstructor
public class JndcServerServiceController {

    private final JndcServerServiceServiceI jndcServerServiceService;

    /**
     * 创建JndcServerService
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(
            @RequestBody(required = false) JndcServerServiceDTO jndcServerServiceDTO) {
        jndcServerServiceService.save(jndcServerServiceDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcServerService
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(
            @RequestBody(required = false) JndcServerServiceDTO jndcServerServiceDTO) {
        jndcServerServiceService.updateById(jndcServerServiceDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcServerService
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(
            @RequestBody(required = false) JndcServerServiceDTO jndcServerServiceDTO) {
        jndcServerServiceService.removeById(jndcServerServiceDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcServerService
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(
            @RequestBody(required = false) JndcServerServiceDTO jndcServerServiceDTO) {
        IPage page = jndcServerServiceService.queryPage(jndcServerServiceDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcServerService
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(
            @RequestBody(required = false) JndcServerServiceDTO jndcServerServiceDTO) {
        List<JndcServerServiceVO> list = jndcServerServiceService.queryList(jndcServerServiceDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcServerService
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(
            @RequestBody(required = false) JndcServerServiceDTO jndcServerServiceDTO) {
        JndcServerServiceDTO rs = jndcServerServiceService.getById(jndcServerServiceDTO.getId());
        JndcServerServiceVO vo = JndcServerServiceStructMapper.INSTANCE.toVO(rs);
        return EncryptedResponse.success(vo);
    }
}
