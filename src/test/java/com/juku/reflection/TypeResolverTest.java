package com.juku.reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.juku.reflection.TypeResolverTest.DefaultRestView;
import com.juku.reflection.TypeResolverTest.RestView;

import org.junit.Test;

@RunWith(JUnit4.class)
public class TypeResolverTest {

	private List<String> list = new ArrayList<String>();
	private Map<String, Method> map;

	/**
	 * ParameterizedType 的几个主要方法 1、Type[] getActualTypeArguments(); 2、Type
	 * getRawType(); 3、Type getOwnerType();
	 * 
	 * Type[] getActualTypeArguments(); 返回 这个 Type 类型的参数的实际类型数组。 如
	 * Map<String,Person> map 这个 ParameterizedType 返回的是 String 类,Person 类的全限定类名的
	 * Type 。
	 * 
	 * Type getRawType() 返回的是当前这个 ParameterizedType 的类型。 如 Map<String,Person> map 这个
	 * ParameterizedType 返回的是 Map 类的全限定类名的 Type。
	 * 
	 * Type getOwnerType();这个比较少用到。返回的是这个 ParameterizedType 所在的类的 Type （注意当前的
	 * ParameterizedType 必须属于所在类的 member）。解释起来有点别扭，还是直接用代码说明吧。 比如 Map<String,Person>
	 * map 这个 ParameterizedType 的 getOwnerType() 为 null，而 Map.Entry<String,
	 * String>entry 的 getOwnerType() 为 Map 所属于的 Type。
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	@Test
	public void ParameterizedTypeDescription() throws NoSuchFieldException, SecurityException {
		Type type = TypeResolverTest.class.getDeclaredField("list").getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			for (Type _type : paramType.getActualTypeArguments()) {
				System.out.println(_type);
			}
			System.out.println(paramType.getRawType());
			System.out.println(paramType.getOwnerType());
		}

		type = TypeResolverTest.class.getDeclaredField("map").getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			for (Type _type : paramType.getActualTypeArguments()) {
				System.out.println(_type);
			}
			System.out.println(paramType.getRawType());
			System.out.println(paramType.getOwnerType());
		}
	}

	/**
	 * TypeVariable 变量 比如 public class TypeVariableBean<K extends InputStream &
	 * Serializable, V> ，K ，V 都是属于类型变量。 主要方法 Type[] getBounds(); 得到上边界的 Type数组，如 K
	 * 的上边界数组是 InputStream 和 Serializable。 V 没有指定的话，上边界是 Object D
	 * getGenericDeclaration(); 返回的是声明这个 Type 所在的类 的 Type String getName(); 返回的是这个
	 * type variable 的名称
	 */
	@Test
	public void TypeVariableDescription() {

	}

	@Test
	public void ClassDescription() throws NoSuchMethodException, SecurityException {
		Class<?> clazz = DefaultRestView.class;
		Method method = clazz.getMethod("getData");

		Type returnType = method.getGenericReturnType();

		Type superType = clazz.getGenericSuperclass();
		if (superType instanceof ParameterizedType) {
			ParameterizedType parentAsType = (ParameterizedType) superType;

			Type[] types = parentAsType.getActualTypeArguments();
//			for(Type type:types) {
//				System.out.println(type);
//			}
			// <>前的类型
			Class<?> parentAsClass = (Class<?>) parentAsType.getRawType();

			TypeVariable<?>[] parentTypeVars = parentAsClass.getTypeParameters();
			for (int i = 0; i < parentTypeVars.length; i++) {
				if (returnType == parentTypeVars[i]) {
					System.out.println(types[i]);
				}
			}
		}
	}

	@Test
	public void ClassDescription2() throws NoSuchMethodException, SecurityException {
		RestView<String> view = new RestView<String>();
		Class<?> clazz = view.getClass();
		Type returnType = clazz.getMethod("getData").getGenericReturnType();

		TypeVariable<?>[] vars = clazz.getTypeParameters();

		Type type = clazz;
		ParameterizedType paramType = (ParameterizedType) type;
		Type[] actualTypes = paramType.getActualTypeArguments();
		for (int i = 0; i < vars.length; i++) {
			if (returnType == vars[i]) {
				Type result = actualTypes[i];
				System.out.println(result);
			}
		}

	}

	class XmlDefaultRestView extends DefaultRestView {

	}

	class DefaultRestView extends RestView<String> {

	}

	class RestView<T> {
		private Integer code;
		private String message;

		private List<T> records;
		private T data;

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

		public List<T> getRecords() {
			return records;
		}

		public void setRecords(List<T> records) {
			this.records = records;
		}

	}
}
