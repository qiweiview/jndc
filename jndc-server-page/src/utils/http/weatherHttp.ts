import axios from "axios";

// 定义API密钥和基础URL
const API_KEY = "8022e2b1977b4686ba71e6a29c9a593a"; // 将此处替换为你的API Key
const BASE_URL = "https://devapi.qweather.com/v7/weather";

// 定义接口来表示API返回的数据结构
export interface WeatherData {
  temp: string; // 当前温度
  text: string; // 天气情况描述
  icon: string; // 天气情况图标
  // 你可以根据需要添加更多字段，如湿度、风速等
}

export interface WeatherResponse {
  now: WeatherData; // 当前天气数据
  code: string; // 返回状态码
  updateTime: string; // 数据更新时间
}

// 获取当前天气数据的函数
export const getWeather = async (location: string): Promise<WeatherData> => {
  try {
    // 发起GET请求，获取当前天气数据
    const response = await axios.get<WeatherResponse>(`${BASE_URL}/now`, {
      params: {
        key: API_KEY,
        location: location
      }
    });

    // 检查API返回的状态码，确保请求成功
    if (response.data.code === "200") {
      return response.data.now;
    } else {
      throw new Error(`API Error: ${response.data.code}`);
    }
  } catch (error) {
    console.error("Error fetching weather data:", error);
    throw error;
  }
};
