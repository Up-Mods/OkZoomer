package page.langeweile.ok_zoomer.config.metadata;

import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.metadata.MetadataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RangeSubset {
	MetadataType<Range, Builder> TYPE = MetadataType.create(Optional::empty, Builder::new);

	int min();
	int max();

	final class Processor implements ConfigFieldAnnotationProcessor<RangeSubset> {
		@Override
		public void process(RangeSubset annotation, MetadataContainerBuilder<?> builder) {
			builder.metadata(TYPE, size -> size.set(annotation.min(), annotation.max()));
		}
	}

	final class Builder implements MetadataType.Builder<Range> {
		private Range range = new Range(0, 100);

		public void set(int min, int max) {
			this.range = new Range(min, max);
		}

		@Override
		public Range build() {
			return this.range;
		}
	}

	record Range(
		int min,
		int max
	) {}
}
