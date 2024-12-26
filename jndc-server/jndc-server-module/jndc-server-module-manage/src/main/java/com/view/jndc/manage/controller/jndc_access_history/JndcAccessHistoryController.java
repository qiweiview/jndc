package com.view.jndc.manage.controller.jndc_access_history;

import com.view.jndc.manage.model.jndc_access_history.JndcAccessHistoryStructMapper;
import java.util.List;
import com.view.jndc.manage.serviceI.jndc_access_history.JndcAccessHistoryServiceI;
// import com.view.free.common.base.web.EncryptedResponse;
import com.view.free_lite.common.config.model.base.EncryptedResponse;
import com.view.jndc.manage.model.jndc_access_history.vo.JndcAccessHistoryVO;
import com.view.jndc.manage.model.jndc_access_history.d_o.JndcAccessHistoryDO;
import com.view.jndc.manage.model.jndc_access_history.dto.JndcAccessHistoryDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jndcAccessHistory")
@RequiredArgsConstructor
public class JndcAccessHistoryController {

  private final JndcAccessHistoryServiceI jndcAccessHistoryService;

  /** 创建JndcAccessHistory */
  @RequestMapping(value = "save", method = RequestMethod.POST)
  public EncryptedResponse save(
      @RequestBody(required = false) JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    jndcAccessHistoryService.save(jndcAccessHistoryDTO);
    return EncryptedResponse.success("操作成功");
  }

  /** 更新JndcAccessHistory */
  @RequestMapping(value = "update", method = RequestMethod.POST)
  public EncryptedResponse update(
      @RequestBody(required = false) JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    jndcAccessHistoryService.updateById(jndcAccessHistoryDTO);
    return EncryptedResponse.success("操作成功");
  }

  /** 删除JndcAccessHistory */
  @RequestMapping(value = "delete", method = RequestMethod.POST)
  public EncryptedResponse delete(
      @RequestBody(required = false) JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    jndcAccessHistoryService.removeById(jndcAccessHistoryDTO.getId());
    return EncryptedResponse.success("操作成功");
  }

  /** 分页查询JndcAccessHistory */
  @RequestMapping(value = "queryPage", method = RequestMethod.POST)
  public EncryptedResponse queryPage(
      @RequestBody(required = false) JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    IPage page = jndcAccessHistoryService.queryPage(jndcAccessHistoryDTO);
    return EncryptedResponse.success(page);
  }

  /** 查询JndcAccessHistory */
  @RequestMapping(value = "queryList", method = RequestMethod.POST)
  public EncryptedResponse queryList(
      @RequestBody(required = false) JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    List<JndcAccessHistoryVO> list = jndcAccessHistoryService.queryList(jndcAccessHistoryDTO);
    return EncryptedResponse.success(list);
  }

  /** 根据主键获取JndcAccessHistory */
  @RequestMapping(value = "queryById", method = RequestMethod.POST)
  public EncryptedResponse queryById(
      @RequestBody(required = false) JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    JndcAccessHistoryDTO rs = jndcAccessHistoryService.getById(jndcAccessHistoryDTO.getId());
    JndcAccessHistoryVO vo = JndcAccessHistoryStructMapper.INSTANCE.toVO(rs);
    return EncryptedResponse.success(vo);
  }
}
