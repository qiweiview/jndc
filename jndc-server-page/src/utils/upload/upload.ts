import {
  createFile,
  getFilePreSignedUrl,
  upload
} from "@/api/system/file/file";
import { baseUrlApi } from "@/api/utils";
import CryptoJS from "crypto-js";
import type {
  UploadRawFile,
  UploadRequestOptions
} from "element-plus/es/components/upload/src/upload";
import axios from "axios";
// 重写ElUpload上传方法
export const useUpload = () => {
  // 后端上传地址
  const uploadUrl = baseUrlApi("/sysFile/upload");

  // 前端上传
  const uploadFileByFront = async (options: UploadRequestOptions) => {
    // 1.1 生成文件名称
    const fileName = await generateFileName(options.file);
    // 1.2 获取文件预签名地址
    const presignedInfo = await getFilePreSignedUrl(fileName);
    // 1.3 上传文件（不能使用 ElUpload 的 ajaxUpload 方法的原因：其使用的是 FormData 上传，Minio 不支持）
    return axios
      .put(presignedInfo.uploadUrl, options.file, {
        headers: {
          "Content-Type": options.file.type
        }
      })
      .then(() => {
        // 1.4. 记录文件信息到后端（异步）
        const file = {
          configId: presignedInfo.configId,
          url: presignedInfo.url,
          path: fileName,
          name: options.file.name,
          type: options.file.type,
          size: options.file.size
        };
        createFile(file);
        // 通知成功，数据格式保持与后端上传的返回结果一致
        return { data: presignedInfo.url };
      });
  };

  // 后端上传
  const uploadFileByBack = async (options: UploadRequestOptions | File) => {
    return new Promise((resolve, reject) => {
      let fileToUpload: File;

      if ("file" in options) {
        // options 是 UploadRequestOptions 类型
        fileToUpload = options.file;
      } else {
        // options 是 File 类型
        fileToUpload = options;
      }
      upload({ file: fileToUpload })
        .then(res => {
          if (res.code == 0) {
            resolve(res);
          } else {
            reject(res);
          }
        })
        .catch(res => {
          reject(res);
        });
    });
  };

  return {
    uploadUrl,
    uploadFileByFront,
    uploadFileByBack
  };
};

/**
 * 创建文件信息
 * @param vo 文件预签名信息
 * @param name 文件名称
 * @param file 文件
 */
// function createFile(
//   vo: FileApi.FilePresignedUrlRespVO,
//   name: string,
//   file: UploadRawFile
// ) {
//   const fileVo = {
//     configId: vo.configId,
//     url: vo.url,
//     path: name,
//     name: file.name,
//     type: file.type,
//     size: file.size
//   };
//   FileApi.createFile(fileVo);
//   return fileVo;
// }

/**
 * 生成文件名称（使用算法SHA256）
 * @param file 要上传的文件
 */
async function generateFileName(file: UploadRawFile) {
  // 读取文件内容
  const data = await file.arrayBuffer();
  const wordArray = CryptoJS.lib.WordArray.create(data);
  // 计算SHA256
  const sha256 = CryptoJS.SHA256(wordArray).toString();
  // 拼接后缀
  const ext = file.name.substring(file.name.lastIndexOf("."));
  return `${sha256}${ext}`;
}

/**
 * 上传类型
 */
// enum UPLOAD_TYPE {
//   // 客户端直接上传（只支持S3服务）
//   CLIENT = "client",
//   // 客户端发送到后端上传
//   SERVER = "server"
// }
