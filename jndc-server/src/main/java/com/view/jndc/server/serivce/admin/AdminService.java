package com.view.jndc.server.serivce.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.core.utils.AESUtils;
import com.view.core.utils.UniqueId;
import com.view.jndc.server.config.cache.NDCMemoryCache;
import com.view.jndc.server.config.exception.TokenExpireException;
import com.view.jndc.server.dao.admin.AdminDao;
import com.view.jndc.server.model.admin.PureUserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        Page<PureUserEntity> page = new Page<>(pureUserEntity.getCurrent(), pureUserEntity.getSize());
        return adminDao.queryUserPage(page, pureUserEntity);
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
        } catch (TokenExpireException e) {
            throw new RuntimeException("登录状态过期");
        }

        if (byToken == null) {
            throw new RuntimeException("凭证不存在");
        }
        return byToken;
    }

    public void init() {
        PureUserEntity byName = getByName("admin");
        if (byName == null) {
            PureUserEntity pureUserEntity = new PureUserEntity();
            pureUserEntity.setUsername("admin");
            String generate = UniqueId.generate();
            log.info("=======初始化密码:{}，请及时更改=======", generate);
            pureUserEntity.setPassword(generate);
            createUser(pureUserEntity);
        }
    }
}
