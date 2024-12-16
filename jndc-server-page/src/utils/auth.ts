import Cookies from "js-cookie";
import { storageLocal } from "@pureadmin/utils";
import { useUserStoreHook } from "@/store/modules/user";

export interface DataInfo<T> {
  /** token */
  accessToken: string;
  /** 用于调用刷新accessToken的接口时所需的token */
  refreshToken: string;
  /** 头像 */
  avatar?: string;
  /** 用户名 */
  username?: string;
  /** 昵称 */
  nickname?: string;
  /** 当前登录用户的角色 */
  roles?: Array<string>;
  id: number;
  /** 邮箱 */
  email?: string;
  /** 手机号 */
  phone?: string;
  /** 性别 */
  gender?: number;
  /** 生日 */
  birthday?: Date;
  /** 简介 */
  intro?: string;
}

export const userKey = "user-info";
export const TokenKey = "accessToken";
/**
 * 通过`multiple-tabs`是否在`cookie`中，判断用户是否已经登录系统，
 * 从而支持多标签页打开已经登录的系统后无需再登录。
 * 浏览器完全关闭后`multiple-tabs`将自动从`cookie`中销毁，
 * 再次打开浏览器需要重新登录系统
 * */
export const multipleTabsKey = "multiple-tabs";

/** 获取`token` */
export function getToken(): any {
  // 此处与`TokenKey`相同，此写法解决初始化时`Cookies`中不存在`TokenKey`报错
  return Cookies.get(TokenKey);
}

/**
 * @description 设置`token`以及一些必要信息并采用无感刷新`token`方案
 * 无感刷新：后端返回`accessToken`（访问接口使用的`token`）、`refreshToken`（用于调用刷新`accessToken`的接口时所需的`token`，`refreshToken`的过期时间（比如30天）应大于`accessToken`的过期时间（比如2小时））、`expires`（`accessToken`的过期时间）
 * 将`accessToken`、`expires`、`refreshToken`这三条信息放在key值为authorized-token的cookie里（过期自动销毁）
 * 将`avatar`、`username`、`nickname`、`roles`、`refreshToken`、`expires`这六条信息放在key值为`user-info`的localStorage里（利用`multipleTabsKey`当浏览器完全关闭后自动销毁）
 */
export function setToken(data: DataInfo<Date>) {
  const { accessToken, refreshToken } = data;
  const { isRemembered, loginDay } = useUserStoreHook();
  Cookies.set(TokenKey, accessToken);
  Cookies.set(multipleTabsKey, "true");

  function setUserKey({
    avatar,
    username,
    nickname,
    roles,
    id,
    email,
    phone,
    gender,
    birthday,
    intro
  }) {
    useUserStoreHook().SET_AVATAR(avatar);
    useUserStoreHook().SET_USERNAME(username);
    useUserStoreHook().SET_NICKNAME(nickname);
    useUserStoreHook().SET_ROLES(roles);
    useUserStoreHook().SET_ID(id);
    useUserStoreHook().SET_EMAIL(email);
    useUserStoreHook().SET_PHONE(phone);
    useUserStoreHook().SET_GENDER(gender);
    useUserStoreHook().SET_BIRTHDAY(birthday);
    useUserStoreHook().SET_INTRO(intro);
    storageLocal().setItem(userKey, {
      refreshToken,
      avatar,
      username,
      nickname,
      roles,
      id,
      email,
      phone,
      gender,
      birthday,
      intro
    });
  }

  if (data.username && data.roles) {
    const { username, roles, id, email, phone, gender, birthday, intro } = data;
    setUserKey({
      avatar: data?.avatar ?? "",
      username,
      nickname: data?.nickname ?? "",
      roles,
      id,
      email,
      phone,
      gender,
      birthday,
      intro
    });
  } else {
    const avatar =
      storageLocal().getItem<DataInfo<number>>(userKey)?.avatar ?? "";
    const username =
      storageLocal().getItem<DataInfo<number>>(userKey)?.username ?? "";
    const nickname =
      storageLocal().getItem<DataInfo<number>>(userKey)?.nickname ?? "";
    const roles =
      storageLocal().getItem<DataInfo<number>>(userKey)?.roles ?? [];
    const id = storageLocal().getItem<DataInfo<number>>(userKey)?.id ?? null;
    const email =
      storageLocal().getItem<DataInfo<number>>(userKey)?.email ?? "";
    const phone =
      storageLocal().getItem<DataInfo<number>>(userKey)?.phone ?? "";
    const gender =
      storageLocal().getItem<DataInfo<number>>(userKey)?.gender ?? 0;
    const birthday =
      storageLocal().getItem<DataInfo<number>>(userKey)?.birthday ?? null;
    const intro =
      storageLocal().getItem<DataInfo<number>>(userKey)?.intro ?? "";
    setUserKey({
      avatar,
      username,
      nickname,
      roles,
      id,
      email,
      phone,
      gender,
      birthday,
      intro
    });
  }
}

/** 删除`token`以及key值为`user-info`的localStorage信息 */
export function removeToken() {
  Cookies.remove(TokenKey);
  Cookies.remove(multipleTabsKey);
  storageLocal().removeItem(userKey);
}

/** 格式化token（jwt格式） */
export const formatToken = (token: string): string => {
  return "Bearer " + token;
};
