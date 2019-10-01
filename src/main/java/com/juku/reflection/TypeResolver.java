package com.juku.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

/**
 * 获取泛型参数类型要注意下面这些点：
 * 当一个方法返回值包含泛型或参数列表包含泛型。这个时候声明这些方法或参数值的接口和类可以是 ParameterizedType 或 Class
 * 同时，由于面向对象语言的继承特性，泛型方法或参数也可以是从其他类或接口。
 * 而 ParameterizedType的参数泛型类型 TypeVariable从另外一个角度，也可以是 ParameterizedType
 * @author chensheng
 */
public abstract class TypeResolver 
{

	/**
	 * 解析方法的返回值
	 * @param method
	 * @param clazz
	 * @return
	 */
	public static Type resolveReturnType(Method method,Class<?> clazz) {
		Class<?> declaringClass = method.getDeclaringClass();
		Type returnType = method.getGenericReturnType();
		
		if(returnType instanceof TypeVariable) {
			return resolveForVar((TypeVariable<?>)returnType,clazz,declaringClass);
		}
		return null;
	}
	

	private static Type resolveForVar(TypeVariable<?> typeVar,Class<?> srcClass,Class<?> declaringClass) {
		if(srcClass == declaringClass) {
			Type srcType = srcClass;
			if(srcType instanceof TypeVariable) {
				Type[] bounds = typeVar.getBounds();
			      if (bounds.length > 0) {
			        return bounds[0];
			      }
			      return Object.class;
			}
			ParameterizedType parentAsType = (ParameterizedType) srcType;
			Class<?> parentRawType = (Class<?>) parentAsType.getRawType();
			TypeVariable<?>[] rawTypes = parentRawType.getTypeParameters();
			for(int i=0;i<rawTypes.length;i++) {
				if(typeVar == rawTypes[i]) {
					return parentAsType.getActualTypeArguments()[i];
				}
			}
		}
		
		Type superClass = srcClass.getGenericSuperclass();
		if(superClass instanceof ParameterizedType) {
			ParameterizedType parentAsType = (ParameterizedType) superClass;
			Class<?> parentRawType = (Class<?>) parentAsType.getRawType();
			
			TypeVariable<?>[] rawTypes = parentRawType.getTypeParameters();
			if(parentRawType == declaringClass) {
				for(int i=0;i<rawTypes.length;i++) {
					if(typeVar == rawTypes[i]) {
						return parentAsType.getActualTypeArguments()[i];
					}
				}				
			}else {
				return resolveForVar(typeVar,(Class<?>)superClass,declaringClass);
			}

		}else if(superClass instanceof Class) {
			return resolveForVar(typeVar,(Class<?>)superClass,declaringClass);
		}
		return Object.class;
	}

	
}
