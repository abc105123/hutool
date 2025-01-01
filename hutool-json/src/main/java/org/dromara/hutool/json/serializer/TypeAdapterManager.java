/*
 * Copyright (c) 2013-2025 Hutool Team and hutool.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hutool.json.serializer;

import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.lang.loader.LazyFunLoader;
import org.dromara.hutool.core.lang.loader.Loader;
import org.dromara.hutool.core.lang.tuple.Pair;
import org.dromara.hutool.core.lang.tuple.Triple;
import org.dromara.hutool.core.lang.tuple.Tuple;
import org.dromara.hutool.core.reflect.ConstructorUtil;
import org.dromara.hutool.core.reflect.TypeUtil;
import org.dromara.hutool.json.JSON;
import org.dromara.hutool.json.JSONException;
import org.dromara.hutool.json.serializer.impl.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * JSON适配器（序列化和反序列化）管理器，用于管理JSON序列化器，注册和注销自定义序列化器和反序列化器。<br>
 * 此管理器管理着两种类型的序列化器和反序列化器：
 * <ul>
 *     <li>类型精准匹配方式。通过Java对象类型匹配，只会匹配查找的类型，而不匹配子类。可以调用{@link #register(Type, TypeAdapter)}注册。</li>
 *     <li>匹配器（Matcher）方式。通过判断序列化和反序列化器中match方法，找到自定义的序列化和反序列化器，可以调用{@link #register(TypeAdapter)}注册。</li>
 * </ul>
 * <p>
 * 管理器的使用分为两种方式：
 * <ul>
 *     <li>全局模式：  使用{@link TypeAdapterManager#getInstance()}调用单例，全局可用。</li>
 *     <li>实例模式：  使用{@link TypeAdapterManager#of()}创建实例，局部可用，不加载默认的转换器。</li>
 * </ul>
 *
 * @author Looly
 * @since 6.0.0
 */
public class TypeAdapterManager {
	/**
	 * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
	 */
	private static class SingletonHolder {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		private static final TypeAdapterManager INSTANCE = registerDefault(new TypeAdapterManager());
	}

	/**
	 * 获得单例的 TypeAdapterManager
	 *
	 * @return TypeAdapterManager
	 */
	public static TypeAdapterManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * 创建空的TypeAdapterManager
	 *
	 * @return TypeAdapterManager
	 */
	public static TypeAdapterManager of() {
		return new TypeAdapterManager();
	}

	/**
	 * 用户自定义序列化器，存储自定义匹配规则的一类对象的转换器
	 */
	private final Loader<Set<MatcherJSONSerializer<?>>> serializerSetLoader;
	/**
	 * 用户自定义精确类型转换器<br>
	 * 主要存储类型明确（无子类）的转换器
	 */
	private final Loader<Map<Type, JSONSerializer<?>>> serializerMapLoader;
	/**
	 * 用户自定义类型转换器，存储自定义匹配规则的一类对象的转换器
	 */
	private final Loader<Set<MatcherJSONDeserializer<?>>> deserializerSetLoader;
	/**
	 * 用户自定义精确类型转换器<br>
	 * 主要存储类型明确（无子类）的转换器
	 */
	private final Loader<Map<Type, JSONDeserializer<?>>> deserializerMapLoader;

	/**
	 * 构造
	 */
	public TypeAdapterManager() {
		serializerSetLoader = LazyFunLoader.of(LinkedHashSet::new);
		serializerMapLoader = LazyFunLoader.of(HashMap::new);
		deserializerSetLoader = LazyFunLoader.of(LinkedHashSet::new);
		deserializerMapLoader = LazyFunLoader.of(HashMap::new);
	}

	// region ----- register

	/**
	 * 注册自定义类型适配器，用于自定义对象序列化和反序列化<br>
	 * 提供的适配器必须为实现{@link MatcherJSONSerializer}或{@link MatcherJSONDeserializer}接口<br>
	 * 当两个接口都实现时，同时注册序列化和反序列化器
	 *
	 * @param typeAdapter 自定义类型适配器
	 * @return this
	 */
	public TypeAdapterManager register(final TypeAdapter typeAdapter) {
		Assert.notNull(typeAdapter, "typeAdapter must be not null!");
		if (typeAdapter instanceof MatcherJSONSerializer || typeAdapter instanceof MatcherJSONDeserializer) {
			if (typeAdapter instanceof MatcherJSONSerializer) {
				serializerSetLoader.get().add((MatcherJSONSerializer<?>) typeAdapter);
			}
			if (typeAdapter instanceof MatcherJSONDeserializer) {
				deserializerSetLoader.get().add((MatcherJSONDeserializer<?>) typeAdapter);
			}
			return this;
		}

		throw new JSONException("Adapter: {} is not MatcherJSONSerializer or MatcherJSONDeserializer", typeAdapter.getClass());
	}

	/**
	 * 注册自定义类型适配器，用于自定义对象序列化和反序列化
	 *
	 * @param type        类型
	 * @param typeAdapter 自定义序列化器，{@code null}表示移除
	 * @return this
	 */
	public TypeAdapterManager register(final Type type, final TypeAdapter typeAdapter) {
		Assert.notNull(type);
		if (typeAdapter instanceof JSONSerializer || typeAdapter instanceof JSONDeserializer) {
			if (typeAdapter instanceof JSONSerializer) {
				serializerMapLoader.get().put(type, (JSONSerializer<?>) typeAdapter);
			}
			if (typeAdapter instanceof JSONDeserializer) {
				deserializerMapLoader.get().put(type, (JSONDeserializer<?>) typeAdapter);
			}
			return this;
		}

		throw new JSONException("Adapter: {} is not JSONSerializer or JSONDeserializer", typeAdapter.getClass());
	}
	// endregion

	// region ----- getSerializer or Deserializer

	/**
	 * 获取匹配器对应的序列化器
	 *
	 * @param bean 对象
	 * @param type 类型
	 * @return JSONSerializer
	 */
	@SuppressWarnings({"unchecked"})
	public JSONSerializer<Object> getSerializer(final Object bean, final Type type) {
		final Class<?> rawType = TypeUtil.getClass(type);
		if (null == rawType) {
			return null;
		}
		if (JSONSerializer.class.isAssignableFrom(rawType)) {
			return (JSONSerializer<Object>) ConstructorUtil.newInstanceIfPossible(rawType);
		}

		if (this.serializerMapLoader.isInitialized()) {
			final Map<Type, JSONSerializer<?>> serializerMap = this.serializerMapLoader.get();
			if (!serializerMap.isEmpty()) {
				final JSONSerializer<?> result = serializerMap.get(rawType);
				if (null != result) {
					return (JSONSerializer<Object>) result;
				}
			}
		}

		// Matcher
		if (this.serializerSetLoader.isInitialized()) {
			for (final MatcherJSONSerializer<?> serializer : this.serializerSetLoader.get()) {
				if (serializer.match(bean, null)) {
					return (MatcherJSONSerializer<Object>) serializer;
				}
			}
		}

		// 此处返回null，错误处理在mapper中
		return null;
	}

	/**
	 * 获取匹配器对应的反序列化器
	 *
	 * @param json JSON, 单独查找强类型匹配传{@code null}
	 * @param type 类型, 单独查匹配器传{@code null}
	 * @return JSONDeserializer，始终非空
	 */
	@SuppressWarnings("unchecked")
	public JSONDeserializer<Object> getDeserializer(final JSON json, final Type type) {
		final Class<?> rawType = TypeUtil.getClass(type);
		if (null == rawType) {
			return null;
		}
		if (JSONDeserializer.class.isAssignableFrom(rawType)) {
			return (JSONDeserializer<Object>) ConstructorUtil.newInstanceIfPossible(rawType);
		}

		if (this.deserializerMapLoader.isInitialized()) {
			final Map<Type, JSONDeserializer<?>> deserializerMap = this.deserializerMapLoader.get();
			if (!deserializerMap.isEmpty()) {
				final JSONDeserializer<?> result = deserializerMap.get(rawType);
				if (null != result) {
					return (JSONDeserializer<Object>) result;
				}
			}
		}

		// Matcher
		if (this.deserializerSetLoader.isInitialized()) {
			final Set<MatcherJSONDeserializer<?>> deserializerSet = this.deserializerSetLoader.get();
			if (!deserializerSet.isEmpty()) {
				return (JSONDeserializer<Object>) deserializerSet.stream()
					.filter(deserializer -> deserializer.match(json, type))
					.findFirst()
					.orElse(null);
			}
		}

		// 此处返回null，错误处理在mapper中
		return null;
	}
	// endregion

	/**
	 * 注册默认的序列化器和反序列化器
	 *
	 * @param manager {@code SerializerManager}
	 * @return TypeAdapterManager
	 */
	private static TypeAdapterManager registerDefault(final TypeAdapterManager manager) {

		// 自定义序列化器
		manager.register(ResourceSerializer.INSTANCE);
		manager.register(TokenerSerializer.INSTANCE);
		manager.register(ResourceBundleSerializer.INSTANCE);

		// 自定义反序列化器
		manager.register(JSONPrimitiveDeserializer.INSTANCE);
		manager.register(KBeanDeserializer.INSTANCE);
		manager.register(RecordDeserializer.INSTANCE);
		manager.register(Triple.class, TripleDeserializer.INSTANCE);
		manager.register(Pair.class, PairDeserializer.INSTANCE);
		manager.register(Tuple.class, TupleDeserializer.INSTANCE);

		// 自定义类型适配器
		manager.register(CharSequenceTypeAdapter.INSTANCE);
		manager.register(DateTypeAdapter.INSTANCE);
		manager.register(CalendarTypeAdapter.INSTANCE);
		manager.register(TemporalTypeAdapter.INSTANCE);
		manager.register(TimeZoneTypeAdapter.INSTANCE);
		manager.register(EnumTypeAdapter.INSTANCE);
		manager.register(ThrowableTypeAdapter.INSTANCE);
		manager.register(EntryTypeAdapter.INSTANCE);
		manager.register(MapTypeAdapter.INSTANCE);
		manager.register(IterTypeAdapter.INSTANCE);
		manager.register(ArrayTypeAdapter.INSTANCE);
		// 最低优先级
		manager.register(BeanTypeAdapter.INSTANCE);

		return manager;
	}
}
