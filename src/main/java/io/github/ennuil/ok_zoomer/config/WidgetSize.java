package io.github.ennuil.ok_zoomer.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.metadata.MetadataType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WidgetSize {
	MetadataType<Size, Builder> TYPE = MetadataType.create(() -> Optional.of(Size.FULL), Builder::new);

	WidgetSize.Size value();

	public final class Processor implements ConfigFieldAnnotationProcessor<WidgetSize> {
		@Override
		public void process(WidgetSize annotation, MetadataContainerBuilder<?> builder) {
			builder.metadata(TYPE, size -> size.set(annotation.value()));
		}
	}

	public final class Builder implements MetadataType.Builder<Size> {
		private Size size = Size.FULL;

		public void set(Size size) {
			this.size = size;
		}

		@Override
		public Size build() {
			return this.size;
		}
	}

	enum Size {
		HALF,
		FULL
	}
}
