package com.nebula.netty.test;


import java.nio.ByteBuffer;

/**
 * <p>
 * ByteBuffer 参考：https://www.jianshu.com/p/ebc52832dca0
 * </p>
 * 属性示例：
 * byte[] buff  //buff即内部用于缓存的数组。
 * position //当前读取的位置。
 * mark //为某一读过的位置做标记，便于某些时候回退到该位置。
 * capacity //初始化时候的容量。
 * limit //当写数据到buffer中时，limit一般和capacity相等，当读数据时，limit代表buffer中有效数据的长度。
 *
 * 0 <= mark <= position <= limit <= capacity
 *
 *
 * @author zhu.chen
 * @date 2020/8/11
 * @version 1.0
 */
public class ByteBufferTest {

    /**
     * ByteBuffer Api：
     * ByteBuffer.allocate();
     * ByteBuffer.allocateDirect();
     * ByteBuffer.wrap();
     */
    public static void main(String[] args) {
        // ByteBuffer.allocate()：单位是字节
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    }

}
