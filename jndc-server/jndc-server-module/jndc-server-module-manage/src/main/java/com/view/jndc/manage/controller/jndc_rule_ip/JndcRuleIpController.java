package com.view.jndc.manage.controller.jndc_rule_ip;

import com.view.jndc.manage.model.jndc_rule_ip.JndcRuleIpStructMapper;
import java.util.List;
import com.view.jndc.manage.serviceI.jndc_rule_ip.JndcRuleIpServiceI;
// import com.view.free.common.base.web.EncryptedResponse;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_rule_ip.vo.JndcRuleIpVO;
import com.view.jndc.manage.model.jndc_rule_ip.d_o.JndcRuleIpDO;
import com.view.jndc.manage.model.jndc_rule_ip.dto.JndcRuleIpDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jndcRuleIp")
@RequiredArgsConstructor
public class JndcRuleIpController {

  private final JndcRuleIpServiceI jndcRuleIpService;

  /** 创建JndcRuleIp */
  @RequestMapping(value = "save", method = RequestMethod.POST)
  public EncryptedResponse save(@RequestBody(required = false) JndcRuleIpDTO jndcRuleIpDTO) {
    jndcRuleIpService.save(jndcRuleIpDTO);
    return EncryptedResponse.success("操作成功");
  }

  /** 更新JndcRuleIp */
  @RequestMapping(value = "update", method = RequestMethod.POST)
  public EncryptedResponse update(@RequestBody(required = false) JndcRuleIpDTO jndcRuleIpDTO) {
    jndcRuleIpService.updateById(jndcRuleIpDTO);
    return EncryptedResponse.success("操作成功");
  }

  /** 删除JndcRuleIp */
  @RequestMapping(value = "delete", method = RequestMethod.POST)
  public EncryptedResponse delete(@RequestBody(required = false) JndcRuleIpDTO jndcRuleIpDTO) {
    jndcRuleIpService.removeById(jndcRuleIpDTO.getId());
    return EncryptedResponse.success("操作成功");
  }

  /** 分页查询JndcRuleIp */
  @RequestMapping(value = "queryPage", method = RequestMethod.POST)
  public EncryptedResponse queryPage(@RequestBody(required = false) JndcRuleIpDTO jndcRuleIpDTO) {
    IPage page = jndcRuleIpService.queryPage(jndcRuleIpDTO);
    return EncryptedResponse.success(page);
  }

  /** 查询JndcRuleIp */
  @RequestMapping(value = "queryList", method = RequestMethod.POST)
  public EncryptedResponse queryList(@RequestBody(required = false) JndcRuleIpDTO jndcRuleIpDTO) {
    List<JndcRuleIpVO> list = jndcRuleIpService.queryList(jndcRuleIpDTO);
    return EncryptedResponse.success(list);
  }

  /** 根据主键获取JndcRuleIp */
  @RequestMapping(value = "queryById", method = RequestMethod.POST)
  public EncryptedResponse queryById(@RequestBody(required = false) JndcRuleIpDTO jndcRuleIpDTO) {
    JndcRuleIpDTO rs = jndcRuleIpService.getById(jndcRuleIpDTO.getId());
    JndcRuleIpVO vo = JndcRuleIpStructMapper.INSTANCE.toVO(rs);
    return EncryptedResponse.success(vo);
  }
}
