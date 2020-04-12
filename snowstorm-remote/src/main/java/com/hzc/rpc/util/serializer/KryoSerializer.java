package com.hzc.rpc.util.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.BeanSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.*;

/**
 * @author: hzc
 * @Date: 2020/03/05  11:34
 * @Description:
 */
public class KryoSerializer {

    private Class aClass = null;

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(aClass, new BeanSerializer(kryo, aClass));
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        return kryo;
    });

    public  KryoSerializer(Class aClass) {
        this.aClass = aClass;
    }

    public byte[] serializer(Object object) {
        Kryo kryo = kryoThreadLocal.get();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output, object);
        output.flush();
        output.close();
        return byteArrayOutputStream.toByteArray();
    }


    public Object deserializer(byte[] bytes) {
        Kryo kryo = kryoThreadLocal.get();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        Object o = kryo.readObject(input, aClass);
        input.close();
        return o;
    }

    public Class<?> getAClass() {
        return aClass;
    }

    public void setAClass(Class aClass) {
        this.aClass = aClass;
    }
}
