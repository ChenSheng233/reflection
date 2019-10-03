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
			return resolveTypeVariable((TypeVariable<?>)returnType,clazz,declaringClass);
		}else if(returnType instanceof ParameterizedType) {
			return resolveParameterizedType((ParameterizedType)returnType,clazz,declaringClass);
		}
		return null;
	}
	

	private static Type resolveTypeVariable(TypeVariable<?> typeVar,Type srcType,Class<?> declaringClass) {
		Class<?> srcClass = null;
		if(srcType instanceof Class) {
			srcClass = (Class<?>)srcType;
		}
		if(srcClass == declaringClass) {
			if(srcType instanceof TypeVariable) {
				Type[] bounds = typeVar.getBounds();
			      if (bounds.length > 0) {
			        return bounds[0];
			      }
			      return Object.class;
			}else if(srcType instanceof ParameterizedType) {
				ParameterizedType parentAsType = (ParameterizedType) srcType;
				Class<?> parentRawType = (Class<?>) parentAsType.getRawType();
				TypeVariable<?>[] rawTypes = parentRawType.getTypeParameters();
				for(int i=0;i<rawTypes.length;i++) {
					if(typeVar == rawTypes[i]) {
						return parentAsType.getActualTypeArguments()[i];
					}
				}				
			}
			
			
		}
		Type superClass = null;
		if(srcClass!=null) {
			superClass = srcClass.getGenericSuperclass();
		}else {
			superClass = srcType;
		}
		
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
				return resolveTypeVariable(typeVar,(Class<?>)superClass,declaringClass);
			}

		}else if(superClass instanceof Class) {
			return resolveTypeVariable(typeVar,(Class<?>)superClass,declaringClass);
		}
		return Object.class;
	}


	public static ParameterizedType resolveParameterizedType(ParameterizedType typeVar,Type srcType,Class<?> declaringClass) {
		Class<?> srcClass = (Class<?>)srcType;
		if(srcClass == declaringClass) {
			return typeVar;
		}
		
		Type superType = srcClass.getGenericSuperclass();		
		
		// 返回泛型参数泛型变量数组
		Type[] returnTypes = typeVar.getActualTypeArguments();
		Type[] result = new Type[returnTypes.length];
		for(int i=0;i<result.length;i++) {
			if(returnTypes[i] instanceof TypeVariable) {
				result[i] = resolveTypeVariable((TypeVariable<?>)returnTypes[i],superType,declaringClass);
			}else if(returnTypes[i] instanceof ParameterizedType) {
				result[i] = resolveParameterizedType((ParameterizedType)returnTypes[i],superType,declaringClass);
			}
		}
		
		Class<?> rawClass = (Class<?>)typeVar.getRawType();
		return new ParameterizedTypeImple(result,rawClass,null);
	}
	
	private static class ParameterizedTypeImple implements ParameterizedType{
		private Type[] actualTypeArguments;
		private Type rawType;
		private Type owerType;
		public ParameterizedTypeImple(Type[] actualTypeArguments, Type rawType, Type owerType) {
			super();
			this.actualTypeArguments = actualTypeArguments;
			this.rawType = rawType;
			this.owerType = owerType;
		}
		@Override
		public Type[] getActualTypeArguments() {
			// TODO Auto-generated method stub
			return actualTypeArguments;
		}
		@Override
		public Type getRawType() {
			// TODO Auto-generated method stub
			return rawType;
		}
		@Override
		public Type getOwnerType() {
			// TODO Auto-generated method stub
			return owerType;
		}
		@Override
		public String toString() {
			return "ParameterizedTypeImple [actualTypeArguments=" + Arrays.toString(actualTypeArguments) + ", rawType="
					+ rawType + ", owerType=" + owerType + "]";
		}		
	}

	
}
