package com.nebula.netty.netty.heartbeat;

import java.io.Serializable;

/**
 * <p>
 *  自定义协议
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/31
 * @version: v1.0.0
 */
public class CustomProtocol implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private String content;

    public CustomProtocol() {
    }

    public CustomProtocol(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CustomProtocol{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }

}
