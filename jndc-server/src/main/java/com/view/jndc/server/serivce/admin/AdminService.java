package com.view.jndc.server.serivce.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.core.utils.AESUtils;
import com.view.core.utils.RuntimeUtils;
import com.view.core.utils.UniqueId;
import com.view.jndc.server.config.cache.NDCMemoryCache;
import com.view.jndc.server.config.exception.InvalidTokenException;
import com.view.jndc.server.dao.admin.AdminDao;
import com.view.jndc.server.model.admin.PureUserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminService {
    private final AdminDao adminDao;

    private final NDCMemoryCache ndcMemoryCache;

    public int createUser(PureUserEntity pureUserEntity) {
        String username = pureUserEntity.getUsername();
        String password = pureUserEntity.getPassword();
        if (username == null || password == null) {
            throw new RuntimeException("用户名或密码为空");
        }

        //存在风险，可以使用签名算法替换
        String encrypt = AESUtils.encrypt(password);
        pureUserEntity.setPassword(encrypt);


        PureUserEntity byName = getByName(pureUserEntity.getUsername());
        if (byName != null) {
            throw new RuntimeException("用户名已存在");
        }
        pureUserEntity.init();
        int insert = adminDao.insert(pureUserEntity);
        return insert;
    }


    public PureUserEntity getByName(String username) {
        return adminDao.selectOne(new QueryWrapper<PureUserEntity>().eq("username", username));
    }

    public Page<PureUserEntity> queryUserPage(PureUserEntity pureUserEntity) {
        Integer current = pureUserEntity.getCurrent();
        if (current == null) {
            current = 1;
        }
        Integer size = pureUserEntity.getSize();
        if (size == null) {
            size = 10;
        }


        Page<PureUserEntity> page = new Page<>(current, size);

        Page<PureUserEntity> pureUserEntityPage = adminDao.queryUserPage(page, pureUserEntity);
        //id
        pureUserEntityPage.getRecords().parallelStream().forEach(x -> x.tobeResponse());
        return pureUserEntityPage;
    }

    public PureUserEntity login(PureUserEntity inputUser) {
        PureUserEntity dbUser = getByName(inputUser.getUsername());
        if (dbUser == null) {
            throw new RuntimeException("用户不存在");
        }
        String password = inputUser.getPassword();
        String encrypt = AESUtils.encrypt(password);
        if (!encrypt.equals(dbUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        //刷新当前token
        String generate = UniqueId.generate();
        dbUser.setAccessToken(generate);
        adminDao.updateById(dbUser);


        //写入缓存
        ndcMemoryCache.putExpired(generate, dbUser, 5 * 60);
        return dbUser.desensitization();
    }

    public PureUserEntity info(String authorizationHeader) {
        PureUserEntity byToken;
        try {
            byToken = ndcMemoryCache.get(authorizationHeader, PureUserEntity.class);
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException("登录状态过期");
        }

        if (byToken == null) {
            throw new InvalidTokenException("凭证不存在");
        }
        return byToken;
    }

    public void init() {
        PureUserEntity byName = getByName("admin");
        if (byName == null) {
            PureUserEntity pureUserEntity = new PureUserEntity();
            pureUserEntity.setUsername("admin");
            String runtimeUniqueId = RuntimeUtils.getRuntimeUniqueId();
            log.info("=======初始化密码:{}，请及时更改=======", runtimeUniqueId);
            pureUserEntity.setPassword(runtimeUniqueId);
            createUser(pureUserEntity);
        }
    }

    public int deleteUser(PureUserEntity pureUserEntity) {
        pureUserEntity.tobeRequest();

        PureUserEntity byId = adminDao.selectById(pureUserEntity.getId());
        if (byId == null) {
            throw new RuntimeException("用户不存在");
        }
        if (byId.getUsername().equals("admin")) {
            throw new RuntimeException("admin用户不可删除");
        }
        return adminDao.deleteById(byId.getId());
    }

    public String resetPassword(PureUserEntity pureUserEntity) {
        pureUserEntity.tobeRequest();
        PureUserEntity byId = adminDao.selectById(pureUserEntity.getId());
        if (byId == null) {
            throw new RuntimeException("用户不存在");
        }


        String generate = UniqueId.generate();
        String encrypt = AESUtils.encrypt(generate);
        byId.setPassword(encrypt);
        byId.updateOperation();
        adminDao.updateById(byId);

        //超级管理员密码修改后，写入文件防止丢失
        if ("admin".equals(byId.getUsername())) {
            String runtimeDir = RuntimeUtils.getRuntimeDir() + File.separator + "modified_admin_password.txt";
            try {
                FileUtils.writeStringToFile(new File(runtimeDir), generate, "UTF-8");
            } catch (IOException e) {
                log.error("写入工作目录失败", e);
            }
        }

        return generate;
    }
}
