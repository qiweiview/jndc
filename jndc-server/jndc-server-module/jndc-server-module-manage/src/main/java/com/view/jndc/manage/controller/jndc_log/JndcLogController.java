package com.view.jndc.manage.controller.jndc_log;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_log.JndcLogStructMapper;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.model.jndc_log.vo.JndcLogVO;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jndcLog")
@RequiredArgsConstructor
public class JndcLogController {

    private final JndcLogServiceI jndcLogService;

    /**
     * 创建JndcLog
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(@RequestBody(required = false) JndcLogDTO jndcLogDTO) {
        jndcLogService.save(jndcLogDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcLog
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(@RequestBody(required = false) JndcLogDTO jndcLogDTO) {
        jndcLogService.updateById(jndcLogDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcLog
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(@RequestBody(required = false) JndcLogDTO jndcLogDTO) {
        jndcLogService.removeById(jndcLogDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    @RequestMapping(value = "deleteBatch", method = RequestMethod.POST)
    public EncryptedResponse deleteBatch(@RequestBody(required = false) JndcLogDTO jndcLogDTO) {
        jndcLogService.deleteBatch(jndcLogDTO.getIdList());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcLog
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(@RequestBody(required = false) JndcLogDTO jndcLogDTO) {
        IPage page = jndcLogService.queryPage(jndcLogDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcLog
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(@RequestBody(required = false) JndcLogDTO jndcLogDTO) {
        List<JndcLogVO> list = jndcLogService.queryList(jndcLogDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcLog
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(@RequestBody(required = false) JndcLogDTO jndcLogDTO) {
        JndcLogDTO rs = jndcLogService.getById(jndcLogDTO.getId());
        JndcLogVO vo = JndcLogStructMapper.INSTANCE.toVO(rs);
        return EncryptedResponse.success(vo);
    }
}
