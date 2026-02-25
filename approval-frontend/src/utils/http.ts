import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { message } from 'ant-design-vue'

class HttpRequest {
  private instance: AxiosInstance

  constructor() {
    this.instance = axios.create({
      baseURL: '/api',
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    this.setupInterceptors()
  }

  private setupInterceptors() {
    // 请求拦截器
    this.instance.interceptors.request.use(
      (config) => {
        // 可以在这里添加 token
        const token = localStorage.getItem('token')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // 响应拦截器
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => {
        const { data } = response
        
        console.log('🔥 API Response:', response.config.url, data)
        
        // 根据后端返回的数据结构调整
        if (data.success === false) {
          message.error(data.message || '请求失败')
          return Promise.reject(data)
        }
        
        // JeecgBoot 返回结构：{ success: true, result: {...}, message: '' }
        return data.result !== undefined ? data.result : data
      },
      (error) => {
        if (error.response) {
          const { status } = error.response
          
          switch (status) {
            case 401:
              message.error('未授权，请重新登录')
              // 可以跳转到登录页
              break
            case 403:
              message.error('拒绝访问')
              break
            case 404:
              message.error('请求的资源不存在')
              break
            case 500:
              message.error('服务器错误')
              break
            default:
              message.error('请求失败')
          }
        } else {
          message.error('网络错误，请检查网络连接')
        }
        
        return Promise.reject(error)
      }
    )
  }

  public get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.get(url, config)
  }

  public post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.post(url, data, config)
  }

  public put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.put(url, data, config)
  }

  public delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.delete(url, config)
  }
}

export const http = new HttpRequest()

