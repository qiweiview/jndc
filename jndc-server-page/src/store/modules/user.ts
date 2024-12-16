import { defineStore } from "pinia";
import {
  resetRouter,
  router,
  routerArrays,
  storageLocal,
  store,
  type userType
} from "../utils";
import { getLogin, getLogout, refreshTokenApi } from "@/api/auth/auth";
import { useMultiTagsStoreHook } from "./multiTags";
import { type DataInfo, removeToken, setToken, userKey } from "@/utils/auth";
import { message } from "@/utils/message";

export const useUserStore = defineStore({
  id: "pure-user",
  state: (): userType => ({
    // 头像
    avatar: storageLocal().getItem<DataInfo<number>>(userKey)?.avatar ?? "",
    // 用户名
    username: storageLocal().getItem<DataInfo<number>>(userKey)?.username ?? "",
    // 昵称
    nickname: storageLocal().getItem<DataInfo<number>>(userKey)?.nickname ?? "",
    // 权限
    roles: storageLocal().getItem<DataInfo<number>>(userKey)?.roles ?? [],
    // id
    id: storageLocal().getItem<DataInfo<number>>(userKey)?.id ?? null,
    // 邮箱
    email: storageLocal().getItem<DataInfo<number>>(userKey)?.email ?? "",
    // 手机号
    phone: storageLocal().getItem<DataInfo<number>>(userKey)?.phone ?? "",
    // 性别
    gender: storageLocal().getItem<DataInfo<number>>(userKey)?.gender ?? 0,
    // 生日
    birthday:
      storageLocal().getItem<DataInfo<number>>(userKey)?.birthday ?? null,
    // 简介
    intro: storageLocal().getItem<DataInfo<number>>(userKey)?.intro ?? "",
    // 是否勾选了登录页的免登录
    isRemembered: false,
    // 登录页的免登录存储几天，默认7天
    loginDay: 7
  }),
  getters: {
    userInfo(state) {
      return {
        avatar: state.avatar,
        username: state.username,
        nickname: state.nickname,
        roles: state.roles,
        id: state.id,
        email: state.email,
        phone: state.phone,
        gender: state.gender,
        birthday: state.birthday,
        intro: state.intro,
        isRemembered: state.isRemembered,
        loginDay: state.loginDay
      };
    }
  },
  actions: {
    /** 存储头像 */
    SET_AVATAR(avatar: string) {
      this.avatar = avatar;
      storageLocal().setItem(userKey, this.userInfo);
    },
    /** 存储用户名 */
    SET_USERNAME(username: string) {
      this.username = username;
      storageLocal().setItem(userKey, this.userInfo);
    },
    /** 存储昵称 */
    SET_NICKNAME(nickname: string) {
      this.nickname = nickname;
      storageLocal().setItem(userKey, this.userInfo);
    },
    /** 存储角色 */
    SET_ROLES(roles: Array<string>) {
      this.roles = roles;
    },
    /** 存储id */
    SET_ID(id: number) {
      this.id = id;
    },
    /** 存储邮箱 */
    SET_EMAIL(email: string) {
      this.email = email;
      storageLocal().setItem(userKey, this.userInfo);
    },
    /** 存储手机号 */
    SET_PHONE(phone: string) {
      this.phone = phone;
      storageLocal().setItem(userKey, this.userInfo);
    },
    /** 存储性别 */
    SET_GENDER(gender: number) {
      this.gender = gender;
      storageLocal().setItem(userKey, this.userInfo);
    },
    /** 存储生日 */
    SET_BIRTHDAY(birthday: Date) {
      this.birthday = birthday;
      storageLocal().setItem(userKey, this.userInfo);
    },
    /** 存储简介 */
    SET_INTRO(intro: string) {
      this.intro = intro;
      storageLocal().setItem(userKey, this.userInfo);
    },
    /** 存储是否勾选了登录页的免登录 */
    SET_ISREMEMBERED(bool: boolean) {
      this.isRemembered = bool;
    },
    /** 设置登录页的免登录存储几天 */
    SET_LOGINDAY(value: number) {
      this.loginDay = Number(value);
    },
    /** 判断是否为自己 */
    isSelf(id: number) {
      return this.id == id;
    },

    /** 登入 */
    async loginByUsername(data) {
      return new Promise<any>((resolve, reject) => {
        getLogin(data)
          .then(data => {
            if (data?.code == 0) setToken(data.data);
            resolve(data);
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    /** 前端登出（不调用接口） */
    logOut() {
      this.username = "";
      this.roles = [];
      removeToken();
      useMultiTagsStoreHook().handleTags("equal", [...routerArrays]);
      resetRouter();
      router.push("/login");
    },

    /** 登出（调用接口） */
    logOutByApi() {
      getLogout().then(res => {
        if (res.code == 0) {
          message("退出成功", { type: "success" });
        }
      });
    },
    /** 刷新`token` */
    async handRefreshToken(data) {
      return new Promise<any>((resolve, reject) => {
        refreshTokenApi(data)
          .then(data => {
            if (data) {
              setToken(data.data);
              resolve(data);
            }
          })
          .catch(error => {
            reject(error);
          });
      });
    }
  }
});

export function useUserStoreHook() {
  return useUserStore(store);
}
