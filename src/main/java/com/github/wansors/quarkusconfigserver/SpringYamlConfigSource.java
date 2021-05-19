package com.github.wansors.quarkusconfigserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import io.smallrye.common.classloader.ClassPathUtils;
import io.smallrye.common.constraint.Assert;
import io.smallrye.config.common.MapBackedConfigSource;

/**
 * Yaml config source
 *
 * @author <a href="mailto:phillip.kruger@redhat.com">Phillip Kruger</a>
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class SpringYamlConfigSource extends MapBackedConfigSource {
	private static final long serialVersionUID = -418186029484953531L;

	private static final String NAME_PREFIX = "SpringYamlConfigSource[source=";

	private final Set<String> propertyNames;

	public SpringYamlConfigSource(String name, Map<String, String> source, int ordinal) {
		super(name, source, ordinal, false);
		propertyNames = filterPropertyNames(source);
	}

	public SpringYamlConfigSource(URL url, int ordinal) throws IOException {
		this(NAME_PREFIX + url.toString() + "]",
				ClassPathUtils.readStream(url, (Function<InputStream, Map<String, String>>) inputStream -> {
					try {
						return streamToMap(inputStream);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}), ordinal);
	}

	@Override
	public Set<String> getPropertyNames() {
		return propertyNames;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String> streamToMap(InputStream inputStream) throws IOException {
		Assert.checkNotNullParam("inputStream", inputStream);
		final Map<String, String> yamlInput = new TreeMap<>();
		try {
			final Iterable<Object> objects = new Yaml(new StringConstructor()).loadAll(inputStream);
			for (Object object : objects) {
				if (object instanceof Map) {
					yamlInput.putAll(yamlInputToMap((Map<Object, Object>) object));
				}
			}
			inputStream.close();
		} catch (Throwable t) {
			try {
				inputStream.close();
			} catch (Throwable t2) {
				t.addSuppressed(t2);
			}
			throw t;
		}
		return yamlInput;
	}

	private static Map<String, String> yamlInputToMap(final Map<Object, Object> yamlInput) {
		final Map<String, String> properties = new TreeMap<>();
		if (yamlInput != null) {
			flattenYaml("", yamlInput, properties, false);
		}
		return properties;
	}

	@SuppressWarnings("unchecked")
	private static void flattenYaml(String path, Map<Object, Object> source, Map<String, String> target,
			boolean indexed) {
		source.forEach((originalKey, value) -> {
			String key;
			if (originalKey == null) {
				key = "";
			} else {
				key = originalKey.toString();
			}

			if (!key.isEmpty() && path != null && !path.isEmpty()) {
				key = indexed ? path + key : path + "." + key;
			} else if (path != null && !path.isEmpty()) {
				key = path;
			}

			if (value == null) {
				target.put(key, "");
				target.put(ConfigurationService.TYPE_PREFIX + key, String.class.getName());
			} else if (value instanceof String) {
				target.put(key, (String) value);
				target.put(ConfigurationService.TYPE_PREFIX + key, String.class.getName());
			} else if (value instanceof Map) {
				flattenYaml(key, (Map<Object, Object>) value, target, false);
			} else if (value instanceof List) {
				final List<Object> list = (List<Object>) value;
				// flattenList(key, list, target);
				for (int i = 0; i < list.size(); i++) {
					flattenYaml(key, Collections.singletonMap("[" + i + "]", list.get(i)), target, true);
				}
				// Empty List case
				if (list.isEmpty()) {
					target.put(key, "[]");
					target.put(ConfigurationService.TYPE_PREFIX + key, List.class.getName());
				}
			} else {
				target.put(key, value.toString());
				target.put(ConfigurationService.TYPE_PREFIX + key, value.getClass().getName());

			}

		});
	}

	private static Set<String> filterPropertyNames(Map<String, String> source) {
		final Set<String> filteredKeys = new HashSet<>();
		for (final String key : new HashSet<>(source.keySet())) {
			if (key.startsWith(SpringYamlConfigSource.class.getName() + ".filter.")) {
				String originalKey = key.substring(55);
				source.put(originalKey, source.remove(key));
			} else {
				filteredKeys.add(key);
			}
		}
		return filteredKeys;
	}

	/**
	 * Override some of the yaml constructors, so that the value written in the
	 * flatten result is more alike with the source. For instance, timestamps may be
	 * written in a completely different format which prevents converters to convert
	 * the correct value.
	 */
	private static class StringConstructor extends Constructor {
		public StringConstructor() {
			yamlConstructors.put(Tag.TIMESTAMP, new ConstructYamlStr());
		}
	}

	public String getValueType(String key) {
		return getValue(ConfigurationService.TYPE_PREFIX + key);
	}
}
