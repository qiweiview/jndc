package com.view.jndc.manage.controller.jndc_clietn_service;

import com.view.jndc.manage.model.jndc_clietn_service.JndcClietnServiceStructMapper;

import java.util.List;

import com.view.jndc.manage.serviceI.jndc_clietn_service.JndcClietnServiceServiceI;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_clietn_service.vo.JndcClietnServiceVO;
import com.view.jndc.manage.model.jndc_clietn_service.d_o.JndcClietnServiceDO;
import com.view.jndc.manage.model.jndc_clietn_service.dto.JndcClietnServiceDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jndcClietnService")
@RequiredArgsConstructor
public class JndcClietnServiceController {

    private final JndcClietnServiceServiceI jndcClietnServiceService;

    /**
     * 创建JndcClietnService
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(
            @RequestBody(required = false) JndcClietnServiceDTO jndcClietnServiceDTO) {
        jndcClietnServiceService.save(jndcClietnServiceDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcClietnService
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(
            @RequestBody(required = false) JndcClietnServiceDTO jndcClietnServiceDTO) {
        jndcClietnServiceService.updateById(jndcClietnServiceDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcClietnService
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(
            @RequestBody(required = false) JndcClietnServiceDTO jndcClietnServiceDTO) {
        jndcClietnServiceService.removeById(jndcClietnServiceDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcClietnService
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(
            @RequestBody(required = false) JndcClietnServiceDTO jndcClietnServiceDTO) {
        IPage page = jndcClietnServiceService.queryPage(jndcClietnServiceDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcClietnService
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(
            @RequestBody(required = false) JndcClietnServiceDTO jndcClietnServiceDTO) {
        List<JndcClietnServiceVO> list = jndcClietnServiceService.queryList(jndcClietnServiceDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcClietnService
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(
            @RequestBody(required = false) JndcClietnServiceDTO jndcClietnServiceDTO) {
        JndcClietnServiceDTO rs = jndcClietnServiceService.getById(jndcClietnServiceDTO.getId());
        JndcClietnServiceVO vo = JndcClietnServiceStructMapper.INSTANCE.toVO(rs);
        return EncryptedResponse.success(vo);
    }
}
