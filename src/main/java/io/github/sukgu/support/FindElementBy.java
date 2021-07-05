package io.github.sukgu.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.support.AbstractFindByBuilder;
import org.openqa.selenium.support.PageFactoryFinder;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@PageFactoryFinder(FindElementBy.FindByBuilder.class)
public @interface FindElementBy {
	
	String css() default "";

	String xpath() default "";

	public static class FindByBuilder extends AbstractFindByBuilder {
		
		enum SelectorType { CSS_SELECTOR("css"), XPATH("xpath");
			private final String type;
			SelectorType(String type) {
				this.type = type;
			}
			
			@Override
		    public String toString() {
		        return type;
		    }
		}
		
		@Override
		public By buildIt(Object annotation, Field field) {
			FindElementBy findElementBy = (FindElementBy) annotation;
			assertValidFindBy(findElementBy);
			By ans = buildByFromFindElementBy(findElementBy);
			return ans;
		}
		
		private By buildByFromFindElementBy(FindElementBy findBy) {
			if (!"".equals(findBy.css())) {
				return new FindByCssSelector(findBy.css(), SelectorType.CSS_SELECTOR.toString());
			}

			if (!"".equals(findBy.xpath())) {
				return new FindByXPath(findBy.xpath(), SelectorType.XPATH.toString());
			}
			
			return null;
		}
		
		private void assertValidFindBy(FindElementBy findBy) {

			Set<String> finders = new HashSet<>();

			if (!"".equals(findBy.css())) finders.add("css:" + findBy.css());
			if (!"".equals(findBy.xpath())) finders.add("xpath: " + findBy.xpath());

			// A zero count is okay: it means to look by name or id.
			if (finders.size() > 1) {
				throw new IllegalArgumentException(
						String.format("You must specify at most one location strategy. Number found: %d (%s)",
								finders.size(), finders.toString()));
			}
		}
	}
}
