package com.nebula.netty.test;

import java.io.*;

/**
 * <p>
 * 序列化编解码测试
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/24
 * @version: v1.0.0
 */
public class EncodedTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        User user = new User();
        user.setName("zhangsan");
        user.setAge("11");
        //1：使用Jdk序列化方式
        FileOutputStream fos = new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource("").getPath() + "1.txt");
        ObjectOutputStream oop = new ObjectOutputStream(fos);
        oop.writeObject(user);
        oop.flush();
        oop.close();
        FileInputStream fis = new FileInputStream(Thread.currentThread().getContextClassLoader().getResource("").getPath() + "1.txt");
        ObjectInputStream ois = new ObjectInputStream(fis);
        User user1 = (User) ois.readObject();
        System.out.println(user1.getName() + ":" + user1.getAge());
        // 计算序列化后对象大小
        // 程序内部创建一个byte数组的缓冲区，然后利用ByteArrayOutputStream和ByteArrayInputStream的实例向数组中写入或读出byte型数据
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ObjectOutputStream oop1 = new ObjectOutputStream(bas);
        oop1.writeObject(user);
        oop1.flush();
        oop1.close();
        System.out.println("序列化后数据大小：" + bas.toByteArray().length);
//        System.out.println("大小：" + baos.toByteArray().length);
//        // 2：使用ByteBuffer
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        byte[] name = user.getName().getBytes();
//        buffer.putInt(name.length);
//        buffer.put(name);
//        byte[] age = user.getAge().getBytes();
//        buffer.putInt(age.length);
//        buffer.put(age);
//        buffer.flip();
//        byte[] result = new byte[buffer.remaining()];
//        System.out.println("大小：" + result.length);
    }

    private static class User implements Serializable {

        private static final long serialVersionUID = -923093844627541597L;

        private String name;

        private String age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }

}
