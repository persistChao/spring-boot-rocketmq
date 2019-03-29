package com.answer.exception;

/**
 * @descreption
 * @Author answer
 * @Date 2019/3/29 16 37
 */
public enum RocketMQErrorEnum implements ErrorCode{
    PARAM_NULL("MQ_001", "参数为空"),

    NOT_FOUND_CONSUMSERVICE("mq_100", "根据topic和tag没有找到对应的消费服务"),

    HANDLE_RESULT_NULL("MQ_101", "消费方法返回值为空"),
    CONSUME_FATIL("MQ_102", "消费失败");

    private String code;

    private String msg;

    private RocketMQErrorEnum(String code , String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }


}
