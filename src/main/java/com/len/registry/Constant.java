package com.len.registry;

/**
 * ZooKeeper constant
 */
public interface Constant {

    /**
     * 注册相关的信息
     */
    int ZK_SESSION_TIMEOUT = 5000;
    // 服务的注册目录
    String ZK_REGISTRY_PATH = "/simplerpc";
    //
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
