package pocket4j;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import pocket4j.auth.Authorization;
import pocket4j.conf.Configuration;

public class PocketFactory implements Serializable {

	private static final Constructor<Pocket> POCKET_CONSTRUCTOR;
	private final Configuration conf;

	static {
		String className = "pocket4j.PocketImpl";

		Constructor<Pocket> constructor;
		Class clazz;
		try {
			clazz = Class.forName(className);
			constructor = clazz.getDeclaredConstructor(Configuration.class,
					Authorization.class);
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		} catch (ClassNotFoundException e) {
			throw new AssertionError(e);
		}
		POCKET_CONSTRUCTOR = constructor;
	}

	public PocketFactory(Configuration conf) {
		if (conf == null) {
			throw new NullPointerException("configuration cannot be null");
		}
		this.conf = conf;
	}

	public Pocket getInstance(Authorization auth) {
		try {
			return POCKET_CONSTRUCTOR.newInstance(conf, auth);
		} catch (InstantiationException e) {
			throw new AssertionError(e);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		} catch (InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}

}
