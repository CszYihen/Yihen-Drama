package com.yihen.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一响应结果封装类 - RESTful API标准响应格式
 * 
 * 设计目的：
 * 1. 统一API响应格式，便于前端统一处理
 * 2. 封装业务状态码和错误信息，提高可读性
 * 3. 支持泛型，灵活处理不同类型的返回数据
 * 4. 提供静态工厂方法，简化对象创建过程
 * 
 * 响应格式设计：
 * {
 *   "code": 200,           // 业务状态码
 *   "message": "操作成功",  // 响应消息
 *   "data": {...}          // 具体数据（可为null）
 * }
 * 
 * HTTP状态码 vs 业务状态码：
 * - HTTP状态码：由Web服务器返回，表示HTTP请求的状态
 * - 业务状态码：由应用程序定义，表示业务操作的结果
 * - 本系统约定：200成功，500业务失败，可扩展其他错误码
 * 
 * 前后端协作：
 * - 前端根据code字段判断操作是否成功
 * - message字段可直接显示给用户
 * - data字段包含具体的业务数据
 * 
 * Java泛型教学：
 * - <T> 表示泛型类型参数，T可以是任意类型
 * - 静态方法中的<T>是方法级别的泛型声明
 * - 泛型擦除：运行时泛型信息会被擦除，编译时类型检查
 * 
 * @param <T> 返回数据的类型，可以是任何Java对象
 * @author 智能学习平台开发团队
 * @version 1.0
 */
@Schema(description = "统一API响应结果")
@Data
public class Result<T> {
    
    @Schema(description = "业务状态码", 
            example = "200", 
            allowableValues = {"200", "400", "401", "403", "404", "500"})
    private Integer code;
    
    @Schema(description = "响应消息，成功时描述操作结果，失败时描述错误原因", 
            example = "操作成功")
    private String message;
    
    @Schema(description = "响应数据，具体的业务数据内容")
    private T data;
    
    // 手动添加getter和setter方法
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    /**
     * 成功响应的工厂方法（带数据）
     * 
     * 使用场景：查询操作、创建操作等需要返回数据的情况
     * 
     * 静态方法优势：
     * - 无需new关键字，代码更简洁
     * - 方法名具有语义，增加可读性
     * - 可以内部处理对象初始化逻辑
     * 
     * @param data 要返回的业务数据
     * @param <T> 数据类型（由编译器自动推断）
     * @return 封装了成功状态和数据的Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);           // 设置成功状态码
        result.setMessage("操作成功");  // 设置默认成功消息
        result.setData(data);          // 设置返回数据
        return result;
    }
    
    /**
     * 成功响应的工厂方法（带数据和自定义消息）
     * 
     * 使用场景：需要自定义成功消息的情况
     * 例如："用户创建成功"、"题目保存成功"等
     * 
     * @param data 要返回的业务数据
     * @param message 自定义成功消息
     * @param <T> 数据类型
     * @return 封装了成功状态、自定义消息和数据的Result对象
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);    // 使用自定义消息
        result.setData(data);
        return result;
    }
    
    /**
     * 成功响应的工厂方法（仅消息，无数据）
     * 
     * 使用场景：删除操作、更新操作等无需返回数据的情况
     * 前端只需要知道操作是否成功
     * 
     * @param message 成功消息
     * @param <T> 泛型声明（data将为null）
     * @return 封装了成功状态和消息的Result对象（data为null）
     */
    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        // data默认为null，表示无返回数据
        return result;
    }
    
    /**
     * 失败响应的工厂方法（使用默认错误码）
     * 
     * 使用场景：一般的业务异常、参数验证失败等
     * 使用通用的500错误码表示服务器内部错误
     * 
     * 异常处理最佳实践：
     * - 不暴露内部实现细节（如数据库错误）
     * - 提供用户友好的错误信息
     * - 记录详细的错误日志供开发人员分析
     * 
     * @param message 错误消息，应该是用户可理解的描述
     * @param <T> 泛型声明（data为null）
     * @return 封装了失败状态和错误消息的Result对象
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);           // 默认服务器内部错误码
        result.setMessage(message);
        // data为null，错误时通常不返回数据
        return result;
    }
    
    /**
     * 失败响应的工厂方法（自定义错误码）
     * 
     * 使用场景：需要特定错误码的情况
     * 例如：
     * - 400：请求参数错误
     * - 401：未登录
     * - 403：无权限
     * - 404：资源不存在
     * 
     * 错误码设计原则：
     * - 与HTTP状态码保持一致
     * - 错误码要有明确的业务含义
     * - 前端可以根据错误码进行不同的处理
     * 
     * @param code 自定义错误码
     * @param message 错误消息
     * @param <T> 泛型声明
     * @return 封装了自定义错误码和消息的Result对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
} 