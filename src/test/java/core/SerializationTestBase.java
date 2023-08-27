package core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.assertj.core.api.Assertions.fail;

public abstract class SerializationTestBase {
    public static <T extends Serializable> void assertCanSerialize(T obj) {
        try {
            serializeAndDeserialize(obj);
        } catch (ClassNotFoundException e) {
            fail("This exception should never happen: ", e);
        } catch (IOException e) {
            fail("Serialization failed: ", e);
        }
    }

    public static <T extends Serializable> T serializeAndDeserialize(T obj)
            throws IOException, ClassNotFoundException {
        return deserialize(serialize(obj), (Class<T>) obj.getClass());
    }

    public static <T extends Serializable> byte[] serialize(T obj)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }

    public static <T extends Serializable> T deserialize(byte[] b, Class<T> cl)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        return cl.cast(o);
    }
}
