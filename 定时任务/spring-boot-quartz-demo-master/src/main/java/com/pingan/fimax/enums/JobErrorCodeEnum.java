package com.pingan.fimax.enums;

/**
 *
 */
public enum JobErrorCodeEnum {
    SUCCESS("200", "success"),
    SERVER_ERROR("300000", "server exception"),
    JOB_NAME_IS_EMPTY("300001", "jobName 为空"),
    JOB_KEY_EXIST("300002", "该任务已存在"),
    DELETE_FAILED("300003", "删除任务失败"),
    JOB_DOESNT_EXIS("300004", "该任务不存在"),
    JOB_ALREADY_RUNNING("300005", "该任务正在进行"),
    JOB_NOT_IN_PAUSED_STATE("300006", "该任务不在暂停状态");


    private String val;
    private String msg;

    private JobErrorCodeEnum(String val, String msg) {
        this.val = val;
        this.msg = msg;
    }

    public String getVal() {
        return val;
    }

    public String getMsg() {
        return msg;
    }
}
