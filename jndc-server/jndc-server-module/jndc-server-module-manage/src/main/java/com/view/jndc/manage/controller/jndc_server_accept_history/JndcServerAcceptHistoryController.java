package com.view.jndc.manage.controller.jndc_server_accept_history;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_server_accept_history.JndcServerAcceptHistoryStructMapper;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import com.view.jndc.manage.model.jndc_server_accept_history.vo.JndcServerAcceptHistoryVO;
import com.view.jndc.manage.serviceI.jndc_server_accept_history.JndcServerAcceptHistoryServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jndcServerAcceptHistory")
@RequiredArgsConstructor
public class JndcServerAcceptHistoryController {

    private final JndcServerAcceptHistoryServiceI jndcServerAcceptHistoryService;

    /**
     * 创建JndcServerAcceptHistory
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(
            @RequestBody(required = false) JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
        jndcServerAcceptHistoryService.save(jndcServerAcceptHistoryDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcServerAcceptHistory
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(
            @RequestBody(required = false) JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
        jndcServerAcceptHistoryService.updateById(jndcServerAcceptHistoryDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcServerAcceptHistory
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(
            @RequestBody(required = false) JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
        jndcServerAcceptHistoryService.removeById(jndcServerAcceptHistoryDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcServerAcceptHistory
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(
            @RequestBody(required = false) JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
        IPage page = jndcServerAcceptHistoryService.queryPage(jndcServerAcceptHistoryDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcServerAcceptHistory
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(
            @RequestBody(required = false) JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
        List<JndcServerAcceptHistoryVO> list =
                jndcServerAcceptHistoryService.queryList(jndcServerAcceptHistoryDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcServerAcceptHistory
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(
            @RequestBody(required = false) JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
        JndcServerAcceptHistoryDTO rs =
                jndcServerAcceptHistoryService.getById(jndcServerAcceptHistoryDTO.getId());
        JndcServerAcceptHistoryVO vo = JndcServerAcceptHistoryStructMapper.INSTANCE.toVO(rs);
    return EncryptedResponse.success(vo);
  }
}
