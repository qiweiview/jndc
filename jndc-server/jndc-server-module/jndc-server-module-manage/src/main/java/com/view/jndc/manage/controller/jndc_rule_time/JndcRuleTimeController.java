package com.view.jndc.manage.controller.jndc_rule_time;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_rule_time.JndcRuleTimeStructMapper;
import com.view.jndc.manage.model.jndc_rule_time.dto.JndcRuleTimeDTO;
import com.view.jndc.manage.model.jndc_rule_time.vo.JndcRuleTimeVO;
import com.view.jndc.manage.serviceI.jndc_rule_time.JndcRuleTimeServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jndcRuleTime")
@RequiredArgsConstructor
public class JndcRuleTimeController {

    private final JndcRuleTimeServiceI jndcRuleTimeService;

    /**
     * 创建JndcRuleTime
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public EncryptedResponse save(@RequestBody(required = false) JndcRuleTimeDTO jndcRuleTimeDTO) {
        jndcRuleTimeService.save(jndcRuleTimeDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 更新JndcRuleTime
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public EncryptedResponse update(@RequestBody(required = false) JndcRuleTimeDTO jndcRuleTimeDTO) {
        jndcRuleTimeService.updateById(jndcRuleTimeDTO);
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 删除JndcRuleTime
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public EncryptedResponse delete(@RequestBody(required = false) JndcRuleTimeDTO jndcRuleTimeDTO) {
        jndcRuleTimeService.removeById(jndcRuleTimeDTO.getId());
        return EncryptedResponse.success("操作成功");
    }

    /**
     * 分页查询JndcRuleTime
     */
    @RequestMapping(value = "queryPage", method = RequestMethod.POST)
    public EncryptedResponse queryPage(
            @RequestBody(required = false) JndcRuleTimeDTO jndcRuleTimeDTO) {
        IPage page = jndcRuleTimeService.queryPage(jndcRuleTimeDTO);
        return EncryptedResponse.success(page);
    }

    /**
     * 查询JndcRuleTime
     */
    @RequestMapping(value = "queryList", method = RequestMethod.POST)
    public EncryptedResponse queryList(
            @RequestBody(required = false) JndcRuleTimeDTO jndcRuleTimeDTO) {
        List<JndcRuleTimeVO> list = jndcRuleTimeService.queryList(jndcRuleTimeDTO);
        return EncryptedResponse.success(list);
    }

    /**
     * 根据主键获取JndcRuleTime
     */
    @RequestMapping(value = "queryById", method = RequestMethod.POST)
    public EncryptedResponse queryById(
            @RequestBody(required = false) JndcRuleTimeDTO jndcRuleTimeDTO) {
        JndcRuleTimeDTO rs = jndcRuleTimeService.getById(jndcRuleTimeDTO.getId());
        JndcRuleTimeVO vo = JndcRuleTimeStructMapper.INSTANCE.toVO(rs);
        return EncryptedResponse.success(vo);
    }
}
