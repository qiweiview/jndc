package com.view.jndc.manage.controller.jndc_server;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_server.JndcServerStructMapper;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.view.jndc.manage.model.jndc_server.vo.JndcServerVO;
import com.view.jndc.manage.serviceI.jndc_server.JndcServerServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @RequestMapping(value = "listenOperation", method = RequestMethod.POST)
    public EncryptedResponse listenOperation(@RequestBody(required = false) JndcServerDTO jndcServerDTO) {
        jndcServerService.listenOperation(jndcServerDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    @RequestMapping(value = "pauseOperation", method = RequestMethod.POST)
    public EncryptedResponse pauseOperation(@RequestBody(required = false) JndcServerDTO jndcServerDTO) {
        jndcServerService.pauseOperation(jndcServerDTO.getId());
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
